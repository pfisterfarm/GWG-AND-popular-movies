package com.pfisterfarm.popularmovies;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import com.pfisterfarm.popularmovies.utils.helpers;

import static java.time.format.DateTimeFormatter.ISO_DATE;

public class MainActivity extends AppCompatActivity {

    private static final String sApiKey = BuildConfig.API_KEY;
    private static final int POPULAR = 0;
    private static final int TOPRATED = 1;

    ArrayList<Movie> popularMovies;
    ArrayList<Movie> topRatedMovies;

    MovieAdapter popularMovieAdapter, topRatedMovieAdapter;
    GridView gridView;
    Parcelable scrollState;

    int displayMode = POPULAR;

    private static final String displayModeStr = "displayMode";
    private static final String popularStr = "popular";
    private static final String topRatedStr = "top_rated";
    private static final String scrollPosStr = "scroll_pos";

    String jsonResponse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        final String MOVIE_KEY = "movie";

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        gridView = (GridView) findViewById(R.id.movieGrid);

        if (!helpers.isOnline(getApplicationContext())) {
            helpers.showOKAlertDialog(this, R.string.no_network_title, R.string.no_network_text);
        } else {
                try {
                    if ((savedInstanceState == null) ||
                            !(((savedInstanceState.containsKey(displayModeStr)) &&
                                    (savedInstanceState.containsKey(popularStr)) &&
                                    (savedInstanceState.containsKey(topRatedStr))))) {
                        popularMovies = new fetchMoviesTask().execute("popular").get();
                        topRatedMovies = new fetchMoviesTask().execute("top_rated").get();
                    } else {
                        displayMode = savedInstanceState.getInt(displayModeStr);
                        popularMovies = savedInstanceState.getParcelableArrayList(popularStr);
                        topRatedMovies = savedInstanceState.getParcelableArrayList(topRatedStr);
                        scrollState = savedInstanceState.getParcelable(scrollPosStr);
                        gridView.onRestoreInstanceState(scrollState);
                    }

                    popularMovieAdapter = new MovieAdapter(this, popularMovies);
                    topRatedMovieAdapter = new MovieAdapter(this, topRatedMovies);

                    switch (displayMode) {
                        case POPULAR:
                            gridView.setAdapter(popularMovieAdapter);
                            break;
                        case TOPRATED:
                            gridView.setAdapter(topRatedMovieAdapter);
                            break;
                    }

                    gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                            Intent detailIntent = new Intent(MainActivity.this, DetailActivity.class);
                            switch (displayMode) {
                                case POPULAR:
                                    detailIntent.putExtra(MOVIE_KEY, popularMovies.get(i));
                                    break;
                                case TOPRATED:
                                    detailIntent.putExtra(MOVIE_KEY, topRatedMovies.get(i));
                                    break;
                            }
                            startActivity(detailIntent);
                        }
                    });

                    android.support.design.widget.BottomNavigationView bottomNav = (android.support.design.widget.BottomNavigationView) findViewById(R.id.bottom_nav);

                    bottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
                        @Override
                        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                            switch (item.getItemId()) {
                                case R.id.action_popular:
                                    if (displayMode != POPULAR) {
                                        displayMode = POPULAR;
                                        gridView.setAdapter(popularMovieAdapter);
                                        gridView.invalidate();
                                    }
                                    break;
                                case R.id.action_top_rated:
                                    if (displayMode != TOPRATED) {
                                        displayMode = TOPRATED;
                                        gridView.setAdapter(topRatedMovieAdapter);
                                        gridView.invalidate();
                                    }
                                    // favorites menu item is a placeholder until Stage 2
                                case R.id.action_favorite:
                                    break;
                            }
                            return true;
                        }
                    });

                } catch (Exception e) {
                    e.printStackTrace();
                }
        }
    }

    private static void setupGrid() {

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt(displayModeStr, displayMode);
        outState.putParcelableArrayList(popularStr, popularMovies);
        outState.putParcelableArrayList(topRatedStr, topRatedMovies);
        scrollState = gridView.onSaveInstanceState();
        outState.putParcelable(scrollPosStr, scrollState);
        super.onSaveInstanceState(outState);
    }

    public class fetchMoviesTask extends AsyncTask<String, Void, ArrayList<Movie>>  {

        private static final String idStr = "id";
        private static final String resultsStr = "results";
        private static final String movieTitleStr = "title";
        private static final String releaseDateStr = "release_date";
        private static final String posterPathStr = "poster_path";
        private static final String backdropPathStr = "backdrop_path";
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

                    SimpleDateFormat dateParser = new SimpleDateFormat("yyyy-MM-dd",
                            java.util.Locale.getDefault());

                    for (int i = 0; i < moviesArray.length(); i++) {

                        JSONObject oneMovie = moviesArray.getJSONObject(i);

                        Date releaseDateObj = dateParser.parse(oneMovie.getString(releaseDateStr));

                        returnArray.add(new Movie(oneMovie.getLong(idStr),
                                oneMovie.getString(movieTitleStr),
                                DateFormat.getDateInstance(DateFormat.MEDIUM).format(releaseDateObj),
                                oneMovie.getString(posterPathStr),
                                oneMovie.getString(backdropPathStr),
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
