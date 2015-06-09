package jqsoft.ru.nanodegree.popularmoviesapp;

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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MainActivityFragment extends Fragment {
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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        settings = PreferenceManager.getDefaultSharedPreferences(getActivity());

        currentSortBy = settings.getString(getString(R.string.pref_sort_order_key),
                getString(R.string.pref_sort_order_popularity_desc_value));

        if (savedInstanceState == null) {
            getMovies();
        } else {
            if (movieList != null) {
                gvMovieList.setAdapter(new MovieAdapter(getActivity(), movieList));
                pbLoading.setVisibility(View.GONE);
                gvMovieList.setVisibility(View.VISIBLE);
            } else {
                getMovies();
            }
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

    private class GetMovieListTask extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pbLoading.setVisibility(View.VISIBLE);
            gvMovieList.setVisibility(View.GONE);
        }

        protected String doInBackground(String... params) {
            try {
                return ApiHelper.getMovieList(currentSortBy);
            } catch (Exception e) {
                // if some erros occurs, e.g. no internet
                return null;
            }
        }

        protected void onPostExecute(String result) {
            if (getActivity() == null) {
                return;
            }

            if (result == null) {
                Toast.makeText(getActivity(), R.string.some_error, Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                JSONObject movieListJson = new JSONObject(result);
                JSONArray movieArray = movieListJson.getJSONArray("results");
                movieList = new ArrayList<>(movieArray.length());
                JSONObject movieRaw;
                Movie movie;
                for (int i = 0; i < movieArray.length(); i++) {
                    movieRaw = movieArray.getJSONObject(i);
                    movie = new Movie();
                    movie.Id = movieRaw.getString("id");
                    movie.OriginalTitle = movieRaw.getString("original_title");
                    movie.Overview = movieRaw.getString("overview");
                    movie.PosterPath = movieRaw.getString("poster_path");
                    movie.BackdropPath = movieRaw.getString("backdrop_path");
                    movie.VoteAverage = movieRaw.getString("vote_average");
                    movie.ReleaseDate = movieRaw.getString("release_date");
                    movieList.add(movie);
                }
                gvMovieList.setAdapter(new MovieAdapter(getActivity(), movieList));
            } catch (JSONException e) {
                e.printStackTrace();
            }

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

            Picasso.with(mContext).load(UrlBuilder.getPosterUrl(getItem(position).PosterPath)).into(posterHolder.ivPoster, new Callback() {
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
