package jqsoft.ru.nanodegree.popularmoviesapp.fragments;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
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
import jqsoft.ru.nanodegree.popularmoviesapp.api.MovieDbApi;
import jqsoft.ru.nanodegree.popularmoviesapp.api.MovieDbService;
import jqsoft.ru.nanodegree.popularmoviesapp.common.FavoritesStorage;
import jqsoft.ru.nanodegree.popularmoviesapp.models.Movie;
import jqsoft.ru.nanodegree.popularmoviesapp.models.MovieListResult;

public class MainActivityFragment extends Fragment {
    private static final String MOVIE_LIST = "movieList";

    private static final String STATE_ACTIVATED_POSITION = "activated_position";

    /**
     * The fragment's current callback object, which is notified of movie item
     * clicks.
     */
    private Callbacks mCallbacks = sDummyCallbacks;
    /**
     * The current activated item position. Only used on tablets.
     */
    private int mActivatedPosition = GridView.INVALID_POSITION;

    private SharedPreferences settings;
    private GridView gvMovieList;
    private ProgressBar pbLoading;
    private String currentSortBy;
    private ArrayList<Movie> movieList;

    private boolean isActivateOnMovieClick = false;

    /**
     * A callback interface that allows main activity to be notified of movie
     * selection.
     */
    public interface Callbacks {
        void onMovieSelected(Movie chosenMovie);
    }

    /**
     * A dummy implementation of the {@link Callbacks} interface that does
     * nothing. Used only when this fragment is not attached to an activity.
     */
    private static final Callbacks sDummyCallbacks = new Callbacks() {
        @Override
        public void onMovieSelected(Movie chosenMovie) {
        }
    };

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        if (!(activity instanceof Callbacks)) {
            throw new IllegalStateException("Activity must implement fragment's callbacks.");
        }

        mCallbacks = (Callbacks) activity;
    }

    @Override
    public void onDetach() {
        super.onDetach();

        // Reset the active callbacks interface to the dummy implementation.
        mCallbacks = sDummyCallbacks;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (movieList != null) {
            outState.putParcelableArrayList(MOVIE_LIST, movieList);
        }
        if (mActivatedPosition != GridView.INVALID_POSITION) {
            // Save the activated item position.
            outState.putInt(STATE_ACTIVATED_POSITION, mActivatedPosition);
        }
        super.onSaveInstanceState(outState);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void setActivatedPosition(int position) {
        if (position == GridView.INVALID_POSITION) {
            gvMovieList.setItemChecked(mActivatedPosition, false);
        } else {
            gvMovieList.setItemChecked(position, true);
        }

        mActivatedPosition = position;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            isActivateOnMovieClick = (gvMovieList.getChoiceMode() == GridView.CHOICE_MODE_SINGLE);
        }

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
        } else {
            if (currentSortBy.equals(getString(R.string.pref_sort_order_favorites_value))) {
                getMovies();
            }
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
                if (isActivateOnMovieClick) {
                    mActivatedPosition = position;
                }
                Movie chosenMovie = (Movie) parent.getItemAtPosition(position);
                mCallbacks.onMovieSelected(chosenMovie);
            }
        });
        gvMovieList.setEmptyView(fragmentView.findViewById(android.R.id.empty));
        pbLoading = (ProgressBar) fragmentView.findViewById(R.id.pbLoading);
        return fragmentView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Restore the previously activated item position.
        if (savedInstanceState != null
                && savedInstanceState.containsKey(STATE_ACTIVATED_POSITION)) {
            setActivatedPosition(savedInstanceState.getInt(STATE_ACTIVATED_POSITION));
        }
    }

    private class GetMovieListTask extends AsyncTask<Void, Void, MovieListResult> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pbLoading.setVisibility(View.VISIBLE);
            gvMovieList.getEmptyView().setVisibility(View.GONE);
            gvMovieList.setVisibility(View.GONE);
        }

        protected MovieListResult doInBackground(Void... params) {
            try {
                if (!currentSortBy.equals(getString(R.string.pref_sort_order_favorites_value))) {
                    MovieDbApi movieDbApi = new MovieDbApi();
                    MovieDbService movieDbService = movieDbApi.getService();
                    return movieDbService.getMovieList(currentSortBy);
                } else {
                    MovieListResult movieListResult = new MovieListResult();
                    movieListResult.setMovieList(FavoritesStorage.getFavorites(getActivity()));
                    return movieListResult;
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

            if (isActivateOnMovieClick) {
                if (mActivatedPosition == GridView.INVALID_POSITION && movieList.size() != 0) {
                    gvMovieList.performItemClick(gvMovieList, 0, gvMovieList.getItemIdAtPosition(0));
                }
            }

            pbLoading.setVisibility(View.GONE);
            gvMovieList.setVisibility(View.VISIBLE);
        }
    }

    public class MovieAdapter extends BaseAdapter {
        private final Context mContext;
        final List<Movie> mMovieList;

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

            Picasso.with(mContext).load(getItem(position).getPosterUrl()).fit().into(posterHolder.ivPoster, new Callback() {
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
