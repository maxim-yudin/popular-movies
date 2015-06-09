package jqsoft.ru.nanodegree.popularmoviesapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;


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
                            MovieDetailActivityFragment.newInstance((Movie) extras.getSerializable(Constants.MOVIE));
                    getSupportFragmentManager().beginTransaction().add(android.R.id.content, movieDetailActivityFragment).commit();
                }
            }
        }
    }
}
