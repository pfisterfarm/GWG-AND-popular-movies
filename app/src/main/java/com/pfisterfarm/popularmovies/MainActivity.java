package com.pfisterfarm.popularmovies;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static final String sApiKey = BuildConfig.API_KEY;

    ArrayList<Movie> popularMovies;
    ArrayList<Movie> topRatedMovies;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView testing = (TextView) findViewById(R.id.foobar);
        testing.setText(sApiKey);
    }
}
