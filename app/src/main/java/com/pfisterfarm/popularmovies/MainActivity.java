package com.pfisterfarm.popularmovies;

import android.app.DownloadManager;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private static final String sApiKey = BuildConfig.API_KEY;

    ArrayList<Movie> popularMovies;
    ArrayList<Movie> topRatedMovies;

    MovieAdapter popularMovieAdapter;

    String jsonResponse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        final String MOVIE_KEY = "movie";

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {
            popularMovies = new fetchMoviesTask().execute("popular").get();
            topRatedMovies = new fetchMoviesTask().execute("top_rated").get();

            popularMovieAdapter = new MovieAdapter(this, popularMovies);

            GridView gridView = (GridView) findViewById(R.id.movieGrid);
            gridView.setAdapter(popularMovieAdapter);

            gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    Intent detailIntent = new Intent(MainActivity.this, DetailActivity.class);
                    detailIntent.putExtra(MOVIE_KEY, popularMovies.get(i));
                    startActivity(detailIntent);
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    public class fetchMoviesTask extends AsyncTask<String, Void, ArrayList<Movie>>  {

        private static final String resultsStr = "results";
        private static final String movieTitleStr = "title";
        private static final String releaseDateStr = "release_date";
        private static final String posterPathStr = "poster_path";
        private static final String voteAverageStr = "vote_average";
        private static final String plotSynopsisStr = "overview";

        ArrayList<Movie> returnArray;

        @Override
        protected ArrayList<Movie> doInBackground(String... strings) {

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

                returnArray = new ArrayList<Movie>();

                try {
                    JSONObject moviesObject = new JSONObject(fetchResponse);
                    JSONArray moviesArray = moviesObject.getJSONArray(resultsStr);

                    for (int i = 0; i < moviesArray.length(); i++) {

                        JSONObject oneMovie = moviesArray.getJSONObject(i);

                        returnArray.add(new Movie(oneMovie.getString(movieTitleStr),
                                oneMovie.getString(releaseDateStr),
                                oneMovie.getString(posterPathStr),
                                (float) oneMovie.getDouble(voteAverageStr),
                                oneMovie.getString(plotSynopsisStr)));
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }


            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
            return returnArray;
        }

    }
}
