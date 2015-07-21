package jqsoft.ru.nanodegree.popularmoviesapp.common;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.Map;

import jqsoft.ru.nanodegree.popularmoviesapp.models.Movie;

/**
 * Created by maximyudin on 21.07.15.
 */
public class FavoritesStorage {
    public static final String FAVORITES_STORAGE = "favorites_storage";

    private static SharedPreferences getStorage(Context context) {
        return context.getSharedPreferences(FAVORITES_STORAGE,
                Context.MODE_PRIVATE);
    }

    public static void addFavorite(Context context, Movie movie) {
        Gson gson = new GsonBuilder().serializeNulls().create();
        getStorage(context).edit().putString(movie.Id, gson.toJson(movie)).apply();
    }

    public static void removeFavorite(Context context, String movieId) {
        getStorage(context).edit().remove(movieId).apply();
    }

    public static boolean isFavorite(Context context, String movieId) {
        return getStorage(context).contains(movieId);
    }

    public static Movie getFavorite(Context context, String movieId) {
        if (!getStorage(context).contains(movieId)) {
            return null;
        }
        Gson gson = new GsonBuilder().serializeNulls().create();
        return gson.fromJson(getStorage(context).getString(movieId, ""), Movie.class);
    }

    public static ArrayList<Movie> getFavorites(Context context) {
        ArrayList<Movie> movieList = new ArrayList<>();
        Map<String, ?> favoriteMovieListInStorage = getStorage(context).getAll();
        if (favoriteMovieListInStorage == null) {
            return movieList;
        }

        Gson gson = new GsonBuilder().serializeNulls().create();

        for (Map.Entry<String, ?> movie : favoriteMovieListInStorage.entrySet()) {
            movieList.add(gson.fromJson(movie.getValue().toString(), Movie.class));
        }

        return movieList;
    }
}
