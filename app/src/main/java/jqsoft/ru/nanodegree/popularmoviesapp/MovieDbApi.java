package jqsoft.ru.nanodegree.popularmoviesapp;

import retrofit.RestAdapter;

/**
 * Created by maximyudin on 25.06.15.
 */
public class MovieDbApi {
    public static final String API_BASE_URL = "http://api.themoviedb.org/3";
    private final MovieDbService mMovieDbService;

    public MovieDbApi() {
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(API_BASE_URL)
                .build();
        this.mMovieDbService = restAdapter.create(MovieDbService.class);
    }

    public MovieDbService getService() {
        return this.mMovieDbService;
    }
}
