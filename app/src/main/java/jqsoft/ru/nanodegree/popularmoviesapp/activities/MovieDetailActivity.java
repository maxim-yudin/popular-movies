package jqsoft.ru.nanodegree.popularmoviesapp.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import jqsoft.ru.nanodegree.popularmoviesapp.common.Constants;
import jqsoft.ru.nanodegree.popularmoviesapp.fragments.MovieDetailActivityFragment;
import jqsoft.ru.nanodegree.popularmoviesapp.R;
import jqsoft.ru.nanodegree.popularmoviesapp.models.Movie;


public class MovieDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            if (extras.containsKey(Constants.MOVIE)) {
                if (savedInstanceState == null) {
                    MovieDetailActivityFragment movieDetailActivityFragment =
                            MovieDetailActivityFragment.newInstance((Movie) extras.getParcelable(Constants.MOVIE));
                    getSupportFragmentManager().beginTransaction().add(android.R.id.content, movieDetailActivityFragment).commit();
                }
            }
        }
    }
}
