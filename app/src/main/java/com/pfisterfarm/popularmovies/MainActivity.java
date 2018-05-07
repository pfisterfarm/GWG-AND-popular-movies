package com.pfisterfarm.popularmovies;

import android.app.DownloadManager;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import java.util.ArrayList;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private static final String sApiKey = BuildConfig.API_KEY;

    ArrayList<Movie> popularMovies;
    ArrayList<Movie> topRatedMovies;

    String jsonResponse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        popularMovies = new ArrayList<Movie>();
        topRatedMovies = new ArrayList<Movie>();

        fetchMovies(popularMovies, "popular");
        fetchMovies(topRatedMovies, "top_rated");


    }

    public class fetchMoviesTask extends AsyncTask<String, Void, String>  {

        @Override
        protected String doInBackground(String... strings) {

            String whichMovies = strings[0];
            String fetchResponse;

            HttpUrl.Builder urlBuilder = HttpUrl.parse("http://api.themoviedb.org/3/movie/" + whichMovies).newBuilder();
            urlBuilder.addQueryParameter("api_key", sApiKey);
            String url = urlBuilder.build().toString();

            OkHttpClient client = new OkHttpClient();

            Request request = new Request.Builder()
                    .url(url)
                    .build();

            try {
                Response response = client.newCall(request).execute();
                fetchResponse = response.body().string();
            } catch (Exception e) {
                e.printStackTrace();
                fetchResponse = null;
            }
            return fetchResponse;
        }

        @Override
        protected void onPostExecute(String s) {
            jsonResponse = s;
        }
    }

    private void fetchMovies(ArrayList<Movie> movieArray, String whichMovies) {

        new fetchMoviesTask().execute(whichMovies);


    }
}
