package jqsoft.ru.nanodegree.popularmoviesapp.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import jqsoft.ru.nanodegree.popularmoviesapp.R;
import jqsoft.ru.nanodegree.popularmoviesapp.api.MovieDbApi;
import jqsoft.ru.nanodegree.popularmoviesapp.api.MovieDbService;
import jqsoft.ru.nanodegree.popularmoviesapp.common.Constants;
import jqsoft.ru.nanodegree.popularmoviesapp.models.Movie;
import jqsoft.ru.nanodegree.popularmoviesapp.models.Review;
import jqsoft.ru.nanodegree.popularmoviesapp.models.ReviewListResult;
import jqsoft.ru.nanodegree.popularmoviesapp.models.Trailer;
import jqsoft.ru.nanodegree.popularmoviesapp.models.TrailerListResult;

public class MovieDetailActivityFragment extends Fragment {
    public static final String TRAILER_LIST = "trailerList";
    public static final String REVIEW_LIST = "reviewList";

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

    private Movie movie;
    private ArrayList<Trailer> trailerList;
    private ArrayList<Review> reviewList;

    public static MovieDetailActivityFragment newInstance(Movie movie) {
        MovieDetailActivityFragment fragment = new MovieDetailActivityFragment();

        Bundle args = new Bundle();
        args.putParcelable(Constants.MOVIE, movie);
        fragment.setArguments(args);

        return fragment;
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
        super.onSaveInstanceState(outState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.fragment_movie_detail, container, false);
        pbLoading = (ProgressBar) fragmentView.findViewById(R.id.pbLoading);
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
        return fragmentView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        movie = getMovie();

        Picasso.with(getActivity()).load(movie.getBackdropUrl()).into(ivBackDrop, new Callback() {
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
                || !savedInstanceState.containsKey(REVIEW_LIST)) {
            new GetMoreInfoAboutMovieTask().execute();
        } else {
            trailerList = savedInstanceState.getParcelableArrayList(TRAILER_LIST);
            reviewList = savedInstanceState.getParcelableArrayList(REVIEW_LIST);

            showReviewsAndTrailers();
        }
    }

    private class GetMoreInfoAboutMovieTask extends AsyncTask<Void, Void, MoreInfoAboutMovieResult> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pbLoading.setVisibility(View.VISIBLE);
            svContent.setVisibility(View.GONE);
        }

        protected MoreInfoAboutMovieResult doInBackground(Void... params) {
            try {
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

                MoreInfoAboutMovieResult moreInfoAboutMovieResult = new MoreInfoAboutMovieResult();
                moreInfoAboutMovieResult.trailerList = trailerListResult.getTrailerList();
                moreInfoAboutMovieResult.reviewList = reviewListResult.getReviewList();
                return moreInfoAboutMovieResult;
            } catch (Exception e) {
                // if some erros occurs, e.g. no internet
                return null;
            }
        }

        protected void onPostExecute(MoreInfoAboutMovieResult result) {
            if (getActivity() == null) {
                return;
            }

            if (result == null) {
                Toast.makeText(getActivity(), R.string.some_error, Toast.LENGTH_SHORT).show();
                pbLoading.setVisibility(View.GONE);
                svContent.setVisibility(View.VISIBLE);
                return;
            }

            trailerList = new ArrayList<>(result.trailerList);
            reviewList = new ArrayList<>(result.reviewList);

            showReviewsAndTrailers();
        }
    }

    private class MoreInfoAboutMovieResult {
        public List<Trailer> trailerList;
        public List<Review> reviewList;
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
    }

    final View.OnClickListener trailerClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            String youtubeKey = (String) view.getTag();
            if (youtubeKey != null) {
                openTrailer(youtubeKey);
            }
        }
    };

    public void openTrailer(String youtubeKey) {
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
}
