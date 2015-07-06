package jqsoft.ru.nanodegree.popularmoviesapp.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import jqsoft.ru.nanodegree.popularmoviesapp.R;
import jqsoft.ru.nanodegree.popularmoviesapp.activities.MovieDetailActivity;
import jqsoft.ru.nanodegree.popularmoviesapp.api.MovieDbApi;
import jqsoft.ru.nanodegree.popularmoviesapp.api.MovieDbService;
import jqsoft.ru.nanodegree.popularmoviesapp.common.Constants;
import jqsoft.ru.nanodegree.popularmoviesapp.models.Movie;
import jqsoft.ru.nanodegree.popularmoviesapp.models.MovieListResult;

public class MainActivityFragment extends Fragment {
    public static final String MOVIE_LIST = "movieList";

    private SharedPreferences settings;
    private GridView gvMovieList;
    private ProgressBar pbLoading;
    private String currentSortBy;
    private ArrayList<Movie> movieList;

    public static MainActivityFragment newInstance() {
        MainActivityFragment fragment = new MainActivityFragment();
        return fragment;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        settings = PreferenceManager.getDefaultSharedPreferences(getActivity());

        currentSortBy = settings.getString(getString(R.string.pref_sort_order_key),
                getString(R.string.pref_sort_order_popularity_desc_value));

        if (savedInstanceState == null || !savedInstanceState.containsKey(MOVIE_LIST)) {
            getMovies();
        } else {
            movieList = savedInstanceState.getParcelableArrayList(MOVIE_LIST);
            gvMovieList.setAdapter(new MovieAdapter(getActivity(), movieList));
            pbLoading.setVisibility(View.GONE);
            gvMovieList.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (movieList != null) {
            outState.putParcelableArrayList(MOVIE_LIST, movieList);
        }
        super.onSaveInstanceState(outState);
    }

    private void getMovies() {
        new GetMovieListTask().execute();
    }

    @Override
    public void onResume() {
        super.onResume();
        String newSortBy = settings.getString(getString(R.string.pref_sort_order_key),
                getString(R.string.pref_sort_order_popularity_desc_value));
        if (!currentSortBy.equals(newSortBy)) {
            currentSortBy = newSortBy;
            getMovies();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup fragmentView = (ViewGroup) inflater.inflate(R.layout.fragment_main, container, false);
        gvMovieList = (GridView) fragmentView.findViewById(R.id.gvMovieList);
        gvMovieList.setOnItemClickListener(new GridView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Movie chosenMovie = (Movie) parent.getItemAtPosition(position);
                Intent movieDetailIntent = new Intent(getActivity(), MovieDetailActivity.class);
                movieDetailIntent.putExtra(Constants.MOVIE, chosenMovie);
                startActivity(movieDetailIntent);
            }
        });
        pbLoading = (ProgressBar) fragmentView.findViewById(R.id.pbLoading);
        return fragmentView;
    }

    private class GetMovieListTask extends AsyncTask<Void, Void, MovieListResult> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pbLoading.setVisibility(View.VISIBLE);
            gvMovieList.setVisibility(View.GONE);
        }

        protected MovieListResult doInBackground(Void... params) {
            try {
                if (!currentSortBy.equals(getString(R.string.pref_sort_order_favorites_value))) {
                    MovieDbApi movieDbApi = new MovieDbApi();
                    MovieDbService movieDbService = movieDbApi.getService();
                    return movieDbService.getMovieList(currentSortBy);
                } else {
                    // here will be favorites list
                    return null;
                }
            } catch (Exception e) {
                // if some erros occurs, e.g. no internet
                return null;
            }
        }

        protected void onPostExecute(MovieListResult result) {
            if (getActivity() == null) {
                return;
            }

            if (result == null || result.getMovieList() == null) {
                Toast.makeText(getActivity(), R.string.some_error, Toast.LENGTH_SHORT).show();
                pbLoading.setVisibility(View.GONE);
                gvMovieList.setVisibility(View.VISIBLE);
                return;
            }

            movieList = new ArrayList<>(result.getMovieList());
            gvMovieList.setAdapter(new MovieAdapter(getActivity(), movieList));

            pbLoading.setVisibility(View.GONE);
            gvMovieList.setVisibility(View.VISIBLE);
        }
    }

    public class MovieAdapter extends BaseAdapter {
        private Context mContext;
        List<Movie> mMovieList;

        public MovieAdapter(Context context, List<Movie> movieList) {
            mContext = context;
            mMovieList = movieList;
        }

        public int getCount() {
            return mMovieList.size();
        }

        public Movie getItem(int position) {
            return mMovieList.get(position);
        }

        public long getItemId(int position) {
            return 0;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            final PosterHolder posterHolder;
            if (convertView == null) {
                posterHolder = new PosterHolder();
                convertView = LayoutInflater.from(mContext).inflate(R.layout.row_poster, parent, false);
                posterHolder.ivPoster = (ImageView) convertView
                        .findViewById(R.id.ivPoster);
                posterHolder.pbPosterLoading = (ProgressBar) convertView
                        .findViewById(R.id.pbPosterLoading);
                convertView.setTag(posterHolder);
            } else {
                posterHolder = (PosterHolder) convertView.getTag();
            }

            Picasso.with(mContext).load(getItem(position).getPosterUrl()).into(posterHolder.ivPoster, new Callback() {
                @Override
                public void onSuccess() {
                    posterHolder.pbPosterLoading.setVisibility(View.GONE);
                }

                @Override
                public void onError() {
                    posterHolder.pbPosterLoading.setVisibility(View.GONE);
                }
            });

            return convertView;
        }

        private class PosterHolder {
            ImageView ivPoster;
            ProgressBar pbPosterLoading;
        }
    }
}
