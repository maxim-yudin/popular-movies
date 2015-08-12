package jqsoft.ru.nanodegree.popularmoviesapp.common;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import java.util.ArrayList;

import jqsoft.ru.nanodegree.popularmoviesapp.models.Movie;
import jqsoft.ru.nanodegree.popularmoviesapp.models.Review;
import jqsoft.ru.nanodegree.popularmoviesapp.models.Trailer;

public class FavoritesStorage {
    public static void addFavorite(Context context, Movie movie,
                                   ArrayList<Trailer> trailerList, ArrayList<Review> reviewList) {
        final ContentResolver contentResolver = context.getContentResolver();

        contentResolver.delete(Movie.Contract.CONTENT_URI,
                Movie.Contract.MOVIE_ID + "= ?", new String[]{movie.Id});

        ContentValues movieValues = new ContentValues();
        movieValues.put(Movie.Contract.MOVIE_ID, movie.Id);
        movieValues.put(Movie.Contract.BACKDROP_PATH, movie.BackdropPath);
        movieValues.put(Movie.Contract.ORIGINAL_TITLE, movie.OriginalTitle);
        movieValues.put(Movie.Contract.OVERVIEW, movie.Overview);
        movieValues.put(Movie.Contract.POSTER_PATH, movie.PosterPath);
        movieValues.put(Movie.Contract.RELEASE_DATE, movie.ReleaseDate);
        movieValues.put(Movie.Contract.VOTE_AVERAGE, movie.VoteAverage);
        contentResolver.insert(Movie.Contract.CONTENT_URI, movieValues);

        contentResolver.delete(Trailer.Contract.CONTENT_URI,
                Trailer.Contract.MOVIE_ID + "= ?", new String[]{movie.Id});

        if (trailerList != null && trailerList.size() != 0) {
            ArrayList<ContentValues> trailerListValues = new ArrayList<>();
            for (Trailer trailer : trailerList) {
                ContentValues trailerValues = new ContentValues();
                trailerValues.put(Trailer.Contract.MOVIE_ID, movie.Id);
                trailerValues.put(Trailer.Contract.KEY, trailer.Key);
                trailerValues.put(Trailer.Contract.NAME, trailer.Name);
                trailerListValues.add(trailerValues);
            }
            if (trailerListValues.size() != 0) {
                ContentValues[] trailerListArray = new ContentValues[trailerListValues.size()];
                trailerListValues.toArray(trailerListArray);
                contentResolver.bulkInsert(Trailer.Contract.CONTENT_URI, trailerListArray);
            }
        }

        contentResolver.delete(Review.Contract.CONTENT_URI,
                Review.Contract.MOVIE_ID + "= ?", new String[]{movie.Id});

        if (reviewList != null && reviewList.size() != 0) {
            ArrayList<ContentValues> reviewListValues = new ArrayList<>();
            for (Review review : reviewList) {
                ContentValues reviewValues = new ContentValues();
                reviewValues.put(Review.Contract.MOVIE_ID, movie.Id);
                reviewValues.put(Review.Contract.AUTHOR, review.Author);
                reviewValues.put(Review.Contract.CONTENT, review.Content);
                reviewListValues.add(reviewValues);
            }
            if (reviewListValues.size() != 0) {
                ContentValues[] reviewListArray = new ContentValues[reviewListValues.size()];
                reviewListValues.toArray(reviewListArray);
                contentResolver.bulkInsert(Review.Contract.CONTENT_URI, reviewListArray);
            }
        }
    }

    public static void removeFavorite(Context context, String movieId) {
        final ContentResolver contentResolver = context.getContentResolver();

        contentResolver.delete(Movie.Contract.CONTENT_URI,
                Movie.Contract.MOVIE_ID + "= ?", new String[]{movieId});

        contentResolver.delete(Trailer.Contract.CONTENT_URI,
                Trailer.Contract.MOVIE_ID + "= ?", new String[]{movieId});

        contentResolver.delete(Review.Contract.CONTENT_URI,
                Review.Contract.MOVIE_ID + "= ?", new String[]{movieId});
    }

    public static boolean isFavorite(Context context, String movieId) {
        final ContentResolver contentResolver = context.getContentResolver();

        final Cursor cursor = contentResolver.query(Movie.Contract.CONTENT_URI, new String[]{Movie.Contract._ID}, Movie.Contract.MOVIE_ID + "= ?", new String[]{movieId}, "");
        if (cursor == null) {
            return false;
        } else if (cursor.getCount() < 1) {
            cursor.close();
            return false;
        } else {
            cursor.close();
            return true;
        }
    }

    private static final String[] MOVIE_PROJECTION = new String[]{
            Movie.Contract.MOVIE_ID,
            Movie.Contract.BACKDROP_PATH,
            Movie.Contract.ORIGINAL_TITLE,
            Movie.Contract.OVERVIEW,
            Movie.Contract.POSTER_PATH,
            Movie.Contract.RELEASE_DATE,
            Movie.Contract.VOTE_AVERAGE
    };

    // these indices must match the projection MOVIE_PROJECTION that don't use getColumnIndex() method
    private static final int INDEX_MOVIE_ID = 0;
    private static final int INDEX_BACKDROP_PATH = 1;
    private static final int INDEX_ORIGINAL_TITLE = 2;
    private static final int INDEX_OVERVIEW = 3;
    private static final int INDEX_POSTER_PATH = 4;
    private static final int INDEX_RELEASE_DATE = 5;
    private static final int INDEX_VOTE_AVERAGE = 6;

    public static ArrayList<Movie> getFavorites(Context context) {
        final ContentResolver contentResolver = context.getContentResolver();

        ArrayList<Movie> movieList = new ArrayList<>();

        final Cursor cursor = contentResolver.query(Movie.Contract.CONTENT_URI, MOVIE_PROJECTION, null, null, "");

        if (cursor == null) {
            return movieList;
        } else if (cursor.getCount() < 1) {
            cursor.close();
            return movieList;
        }

        Movie movie;
        while (cursor.moveToNext()) {
            movie = new Movie();
            movie.Id = cursor.getString(INDEX_MOVIE_ID);
            movie.BackdropPath = cursor.getString(INDEX_BACKDROP_PATH);
            movie.OriginalTitle = cursor.getString(INDEX_ORIGINAL_TITLE);
            movie.Overview = cursor.getString(INDEX_OVERVIEW);
            movie.PosterPath = cursor.getString(INDEX_POSTER_PATH);
            movie.ReleaseDate = cursor.getString(INDEX_RELEASE_DATE);
            movie.VoteAverage = cursor.getString(INDEX_VOTE_AVERAGE);
            movieList.add(movie);
        }
        cursor.close();

        return movieList;
    }
}
