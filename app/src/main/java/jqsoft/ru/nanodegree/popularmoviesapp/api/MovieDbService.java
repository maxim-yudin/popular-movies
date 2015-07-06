package jqsoft.ru.nanodegree.popularmoviesapp.api;

import jqsoft.ru.nanodegree.popularmoviesapp.models.MovieListResult;
import jqsoft.ru.nanodegree.popularmoviesapp.models.ReviewListResult;
import jqsoft.ru.nanodegree.popularmoviesapp.models.TrailerListResult;
import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;

/**
 * Created by maximyudin on 25.06.15.
 */
public interface MovieDbService {
    String API_KEY_PAIR = "api_key=90c898e67646b4d759c26be76c99b3a8";
    String PARAM_SORT_BY = "sort_by";

    @GET("/discover/movie?primary_release_year=2015&vote_count.gte=50&" + API_KEY_PAIR)
    MovieListResult getMovieList(@Query(PARAM_SORT_BY) String sortBy);

    @GET("/movie/{id}/videos?" + API_KEY_PAIR)
    TrailerListResult getTrailerList(@Path("id") String movieId);

    @GET("/movie/{id}/reviews?" + API_KEY_PAIR)
    ReviewListResult getReviewList(@Path("id") String movieId);
}
