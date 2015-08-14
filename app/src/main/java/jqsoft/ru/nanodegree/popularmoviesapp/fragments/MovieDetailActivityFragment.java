package jqsoft.ru.nanodegree.popularmoviesapp.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import jqsoft.ru.nanodegree.popularmoviesapp.R;
import jqsoft.ru.nanodegree.popularmoviesapp.api.MovieDbApi;
import jqsoft.ru.nanodegree.popularmoviesapp.api.MovieDbService;
import jqsoft.ru.nanodegree.popularmoviesapp.common.Constants;
import jqsoft.ru.nanodegree.popularmoviesapp.common.FavoritesStorage;
import jqsoft.ru.nanodegree.popularmoviesapp.customviews.CheckableImageView;
import jqsoft.ru.nanodegree.popularmoviesapp.models.Movie;
import jqsoft.ru.nanodegree.popularmoviesapp.models.Review;
import jqsoft.ru.nanodegree.popularmoviesapp.models.ReviewListResult;
import jqsoft.ru.nanodegree.popularmoviesapp.models.Trailer;
import jqsoft.ru.nanodegree.popularmoviesapp.models.TrailerListResult;

public class MovieDetailActivityFragment extends Fragment {
    private static final String IS_FAVORITE = "isFavorite";
    private static final String TRAILER_LIST = "trailerList";
    private static final String REVIEW_LIST = "reviewList";

    /**
     * The fragment's current callback object, which is notified of movie item
     * clicks.
     */
    private Callbacks mCallbacks = sDummyCallbacks;

    private ImageView ivBackDrop;
    private ProgressBar pbBackDropLoading;
    private ImageView ivPoster;
    private ProgressBar pbPosterLoading;
    private TextView tvOriginalTitle;
    private TextView tvRating;
    private TextView tvReleaseDate;
    private TextView tvOverview;
    private ProgressBar pbLoading;
    private ScrollView svContent;
    private LinearLayout llReviewsContent;
    private LinearLayout llTrailersContent;
    private CheckableImageView civFavorited;
    private TextView tvNoInternet;

    private Movie movie;
    private ArrayList<Trailer> trailerList;
    private ArrayList<Review> reviewList;

    private String currentSortBy;

    /**
     * A callback interface that allows main activity to be notified of movie
     * removing from favorites.
     */
    public interface Callbacks {
        void onChangedFavoriteStatus();
    }

    /**
     * A dummy implementation of the {@link Callbacks} interface that does
     * nothing. Used only when this fragment is not attached to an activity.
     */
    private static final Callbacks sDummyCallbacks = new Callbacks() {
        @Override
        public void onChangedFavoriteStatus() {
        }
    };

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        if (!(activity instanceof Callbacks)) {
            return;
        }

        mCallbacks = (Callbacks) activity;
    }

    @Override
    public void onDetach() {
        super.onDetach();

        // Reset the active callbacks interface to the dummy implementation.
        mCallbacks = sDummyCallbacks;
    }

    public static MovieDetailActivityFragment newInstance(Movie movie) {
        MovieDetailActivityFragment fragment = new MovieDetailActivityFragment();

        Bundle args = new Bundle();
        args.putParcelable(Constants.MOVIE, movie);
        fragment.setArguments(args);

        return fragment;
    }

    public MovieDetailActivityFragment() {
        setHasOptionsMenu(true);
    }

    private Movie getMovie() {
        return (Movie) getArguments().getParcelable(Constants.MOVIE);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (trailerList != null) {
            outState.putParcelableArrayList(TRAILER_LIST, trailerList);
        }

        if (reviewList != null) {
            outState.putParcelableArrayList(REVIEW_LIST, reviewList);
        }

        outState.putBoolean(IS_FAVORITE, civFavorited.isChecked());

        super.onSaveInstanceState(outState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.fragment_movie_detail, container, false);
        pbLoading = (ProgressBar) fragmentView.findViewById(R.id.pbLoading);
        tvNoInternet = (TextView) fragmentView.findViewById(R.id.tvNoInternet);
        svContent = (ScrollView) fragmentView.findViewById(R.id.svContent);
        ivBackDrop = (ImageView) fragmentView.findViewById(R.id.ivBackDrop);
        pbBackDropLoading = (ProgressBar) fragmentView.findViewById(R.id.pbBackDropLoading);
        ivPoster = (ImageView) fragmentView.findViewById(R.id.ivPoster);
        pbPosterLoading = (ProgressBar) fragmentView.findViewById(R.id.pbPosterLoading);
        tvOriginalTitle = (TextView) fragmentView.findViewById(R.id.tvOriginalTitle);
        tvRating = (TextView) fragmentView.findViewById(R.id.tvRating);
        tvReleaseDate = (TextView) fragmentView.findViewById(R.id.tvReleaseDate);
        tvOverview = (TextView) fragmentView.findViewById(R.id.tvOverview);
        llReviewsContent = (LinearLayout) fragmentView.findViewById(R.id.llReviewsContent);
        llTrailersContent = (LinearLayout) fragmentView.findViewById(R.id.llTrailersContent);
        civFavorited = (CheckableImageView) fragmentView.findViewById(R.id.civFavorited);
        civFavorited.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new FavoriteActionAsyncTask(getActivity()).execute();
            }
        });
        return fragmentView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        tvNoInternet.setVisibility(View.GONE);

        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getActivity());

        currentSortBy = settings.getString(getString(R.string.pref_sort_order_key),
                getString(R.string.pref_sort_order_popularity_desc_value));

        movie = getMovie();

        Picasso.with(getActivity()).load(movie.getBackdropUrl()).centerCrop().fit().into(ivBackDrop, new Callback() {
            @Override
            public void onSuccess() {
                pbBackDropLoading.setVisibility(View.GONE);
            }

            @Override
            public void onError() {
                ivBackDrop.setVisibility(View.GONE);
                pbBackDropLoading.setVisibility(View.GONE);
            }
        });
        Picasso.with(getActivity()).load(movie.getPosterUrl()).into(ivPoster, new Callback() {
            @Override
            public void onSuccess() {
                pbPosterLoading.setVisibility(View.GONE);
            }

            @Override
            public void onError() {
                ivPoster.setVisibility(View.GONE);
                pbPosterLoading.setVisibility(View.GONE);
            }
        });
        tvOriginalTitle.setText(movie.OriginalTitle);
        tvReleaseDate.setText(getString(R.string.release_date, movie.ReleaseDate));
        tvRating.setText(getString(R.string.rating, movie.VoteAverage));
        tvOverview.setText(movie.Overview);

        if (savedInstanceState == null || !savedInstanceState.containsKey(TRAILER_LIST)
                || !savedInstanceState.containsKey(REVIEW_LIST) || !savedInstanceState.containsKey(IS_FAVORITE)) {
            new GetMoreInfoAboutMovieTask(getActivity()).execute();
        } else {
            trailerList = savedInstanceState.getParcelableArrayList(TRAILER_LIST);
            reviewList = savedInstanceState.getParcelableArrayList(REVIEW_LIST);
            civFavorited.setChecked(savedInstanceState.getBoolean(IS_FAVORITE));

            showReviewsAndTrailers();
        }
    }

    private class GetMoreInfoAboutMovieTask extends AsyncTask<Void, Void, MoreInfoAboutMovieResult> {
        final Context mContext;
        private boolean isFavoriteMovie;

        public GetMoreInfoAboutMovieTask(Context context) {
            mContext = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pbLoading.setVisibility(View.VISIBLE);
            svContent.setVisibility(View.GONE);
        }

        protected MoreInfoAboutMovieResult doInBackground(Void... params) {
            try {
                isFavoriteMovie = FavoritesStorage.isFavorite(mContext, movie.Id);
                MoreInfoAboutMovieResult moreInfoAboutMovieResult = new MoreInfoAboutMovieResult();
                if (!currentSortBy.equals(getString(R.string.pref_sort_order_favorites_value))) {
                    // if now is no internet and the movie in favorites, then we load trailers and reviews from db
                    if (isFavoriteMovie && !isInternetConnected()) {
                        moreInfoAboutMovieResult.trailerList = FavoritesStorage.getTrailerOfFavoriteMovie(mContext, movie.Id);
                        moreInfoAboutMovieResult.reviewList = FavoritesStorage.getReviewOfFavoriteMovie(mContext, movie.Id);
                        return moreInfoAboutMovieResult;
                    }

                    MovieDbApi movieDbApi = new MovieDbApi();
                    MovieDbService movieDbService = movieDbApi.getService();
                    TrailerListResult trailerListResult = movieDbService.getTrailerList(movie.Id);
                    if (trailerListResult == null || trailerListResult.getTrailerList() == null) {
                        return null;
                    }
                    ReviewListResult reviewListResult = movieDbService.getReviewList(movie.Id);
                    if (reviewListResult == null || reviewListResult.getReviewList() == null) {
                        return null;
                    }

                    moreInfoAboutMovieResult.trailerList = trailerListResult.getTrailerList();
                    moreInfoAboutMovieResult.reviewList = reviewListResult.getReviewList();

                    // if the movie is favorite, then update trailer and review lists in db for using in favorites mode
                    if (isFavoriteMovie) {
                        FavoritesStorage.saveTrailersOfFavoriteMovie(mContext, movie.Id, moreInfoAboutMovieResult.trailerList);
                        FavoritesStorage.saveReviewsOfFavoriteMovie(mContext, movie.Id, moreInfoAboutMovieResult.reviewList);
                    }
                } else {
                    moreInfoAboutMovieResult.trailerList = FavoritesStorage.getTrailerOfFavoriteMovie(mContext, movie.Id);
                    moreInfoAboutMovieResult.reviewList = FavoritesStorage.getReviewOfFavoriteMovie(mContext, movie.Id);
                }
                return moreInfoAboutMovieResult;
            } catch (Exception e) {
                // if some erros occurs, e.g. no internet
                return null;
            }
        }

        protected void onPostExecute(MoreInfoAboutMovieResult result) {
            if (mContext == null) {
                return;
            }

            if (result == null) {
                pbLoading.setVisibility(View.GONE);
                svContent.setVisibility(View.GONE);
                tvNoInternet.setVisibility(View.VISIBLE);
                return;
            }

            trailerList = new ArrayList<>(result.trailerList);
            reviewList = new ArrayList<>(result.reviewList);

            civFavorited.setChecked(isFavoriteMovie);

            showReviewsAndTrailers();
        }
    }

    private class MoreInfoAboutMovieResult {
        public List<Trailer> trailerList;
        public List<Review> reviewList;
    }

    private class FavoriteActionAsyncTask extends AsyncTask<Void, Void, Boolean> {
        final Context mContext;

        public FavoriteActionAsyncTask(Context context) {
            mContext = context;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            if (FavoritesStorage.isFavorite(getActivity(), movie.Id)) {
                FavoritesStorage.removeFavorite(getActivity(), movie.Id);
            } else {
                FavoritesStorage.addFavorite(getActivity(), movie, trailerList, reviewList);
            }

            return FavoritesStorage.isFavorite(getActivity(), movie.Id);
        }

        @Override
        protected void onPostExecute(Boolean isFavoriteMovie) {
            if (mContext == null) {
                return;
            }

            civFavorited.setChecked(isFavoriteMovie);
            mCallbacks.onChangedFavoriteStatus();
        }
    }

    private void showReviewsAndTrailers() {
        final LayoutInflater inflater = getActivity().getLayoutInflater();
        llReviewsContent.removeAllViews();
        if (reviewList != null && reviewList.size() != 0) {
            View rowReview;
            TextView tvAuthor;
            TextView tvComment;
            for (Review review : reviewList) {
                rowReview = inflater.inflate(R.layout.row_review, llReviewsContent, false);
                tvAuthor = (TextView) rowReview.findViewById(R.id.tvAuthor);
                tvAuthor.setText(review.Author);
                tvComment = (TextView) rowReview.findViewById(R.id.tvComment);
                tvComment.setText(review.Content);
                llReviewsContent.addView(rowReview);
            }
        } else {
            final TextView tvEmpty = (TextView) inflater.inflate(R.layout.row_empty, llReviewsContent, false);
            tvEmpty.setText(R.string.no_reviews);
            llReviewsContent.addView(tvEmpty);
        }

        llTrailersContent.removeAllViews();
        if (trailerList != null && trailerList.size() != 0) {
            View rowTrailer;
            TextView tvTrailerTitle;
            for (Trailer trailer : trailerList) {
                rowTrailer = inflater.inflate(R.layout.row_trailer, llTrailersContent, false);
                tvTrailerTitle = (TextView) rowTrailer.findViewById(R.id.tvTrailerTitle);
                tvTrailerTitle.setText(trailer.Name);
                tvTrailerTitle.setTag(trailer.Key);
                tvTrailerTitle.setOnClickListener(trailerClick);
                llTrailersContent.addView(rowTrailer);
            }
        } else {
            final TextView tvEmpty = (TextView) inflater.inflate(R.layout.row_empty, llTrailersContent, false);
            tvEmpty.setText(R.string.no_trailers);
            llTrailersContent.addView(tvEmpty);
        }

        pbLoading.setVisibility(View.GONE);
        svContent.setVisibility(View.VISIBLE);

        getActivity().supportInvalidateOptionsMenu();
    }

    private final View.OnClickListener trailerClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            String youtubeKey = (String) view.getTag();
            if (youtubeKey != null) {
                openTrailer(youtubeKey);
            }
        }
    };

    private void openTrailer(String youtubeKey) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + youtubeKey));
        if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivity(intent);
        } else {
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse(Trailer.getYoutubeLinkByKey(youtubeKey)));
            if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
                startActivity(intent);
            }
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_details, menu);
        MenuItem mnuShare = menu.findItem(R.id.action_share);
        boolean isTrailersPresent = trailerList != null && trailerList.size() != 0;
        mnuShare.setVisible(isTrailersPresent);
        if (isTrailersPresent) {
            ShareActionProvider shareActionProvider =
                    (ShareActionProvider) MenuItemCompat.getActionProvider(mnuShare);
            if (shareActionProvider != null) {
                Intent shareIntent = new Intent();
                shareIntent.setAction(Intent.ACTION_SEND);
                shareIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.trailer_share_text,
                        Trailer.getYoutubeLinkByKey(trailerList.get(0).Key), movie.OriginalTitle));
                shareIntent.setType("text/plain");
                shareActionProvider.setShareIntent(shareIntent);
            }
        }
    }

    private boolean isInternetConnected() {
        ConnectivityManager cm =
                (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }
}
