package jqsoft.ru.nanodegree.popularmoviesapp.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import jqsoft.ru.nanodegree.popularmoviesapp.common.Constants;
import jqsoft.ru.nanodegree.popularmoviesapp.R;
import jqsoft.ru.nanodegree.popularmoviesapp.models.Movie;

public class MovieDetailActivityFragment extends Fragment {
    private ImageView ivBackDrop;
    private ProgressBar pbBackDropLoading;
    private ImageView ivPoster;
    private ProgressBar pbPosterLoading;
    private TextView tvOriginalTitle;
    private TextView tvRating;
    private TextView tvReleaseDate;
    private TextView tvOverview;

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.fragment_movie_detail, container, false);
        ivBackDrop = (ImageView) fragmentView.findViewById(R.id.ivBackDrop);
        pbBackDropLoading = (ProgressBar) fragmentView.findViewById(R.id.pbBackDropLoading);
        ivPoster = (ImageView) fragmentView.findViewById(R.id.ivPoster);
        pbPosterLoading = (ProgressBar) fragmentView.findViewById(R.id.pbPosterLoading);
        tvOriginalTitle = (TextView) fragmentView.findViewById(R.id.tvOriginalTitle);
        tvRating = (TextView) fragmentView.findViewById(R.id.tvRating);
        tvReleaseDate = (TextView) fragmentView.findViewById(R.id.tvReleaseDate);
        tvOverview = (TextView) fragmentView.findViewById(R.id.tvOverview);
        return fragmentView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Movie movie = getMovie();
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
    }
}
