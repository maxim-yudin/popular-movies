package jqsoft.ru.nanodegree.popularmoviesapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import jqsoft.ru.nanodegree.popularmoviesapp.R;
import jqsoft.ru.nanodegree.popularmoviesapp.common.Constants;
import jqsoft.ru.nanodegree.popularmoviesapp.fragments.MainActivityFragment;
import jqsoft.ru.nanodegree.popularmoviesapp.fragments.MovieDetailActivityFragment;
import jqsoft.ru.nanodegree.popularmoviesapp.models.Movie;

public class MainActivity extends AppCompatActivity implements MainActivityFragment.Callbacks {
    private boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (findViewById(R.id.movie_detail_content) != null) {
            mTwoPane = true;
        }
    }

    /**
     * Callback method indicating that the movie was selected.
     */
    @Override
    public void onMovieSelected(Movie chosenMovie) {
        if (mTwoPane) {
            MovieDetailActivityFragment movieDetailActivityFragment =
                    MovieDetailActivityFragment.newInstance(chosenMovie);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.movie_detail_content, movieDetailActivityFragment)
                    .commit();
        } else {
            Intent movieDetailIntent = new Intent(this, MovieDetailActivity.class);
            movieDetailIntent.putExtra(Constants.MOVIE, chosenMovie);
            startActivity(movieDetailIntent);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsIntent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
