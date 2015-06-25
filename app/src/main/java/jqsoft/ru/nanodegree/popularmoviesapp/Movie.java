package jqsoft.ru.nanodegree.popularmoviesapp;

import android.net.Uri;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Movie implements Serializable {
    private static final String BASE_IMAGE_URL = "http://image.tmdb.org/t/p/";
    private static final String PATH_POSTER_SIZE = "w342";
    private static final String PATH_BACKDROP_IMAGE_SIZE = "original";

    @SerializedName("id")
    public String Id;
    @SerializedName("original_title")
    public String OriginalTitle;
    @SerializedName("overview")
    public String Overview;
    @SerializedName("vote_average")
    public String VoteAverage;
    @SerializedName("release_date")
    public String ReleaseDate;
    @SerializedName("backdrop_path")
    private String backdropPath;
    @SerializedName("poster_path")
    private String posterPath;

    public String getPosterUrl() {
        return getImageBaseUrlBuilder().appendEncodedPath(PATH_POSTER_SIZE)
                .appendEncodedPath(posterPath)
                .toString();
    }

    public String getBackdropUrl() {
        return getImageBaseUrlBuilder().appendEncodedPath(PATH_BACKDROP_IMAGE_SIZE)
                .appendEncodedPath(backdropPath)
                .toString();
    }

    private static Uri.Builder getImageBaseUrlBuilder() {
        return Uri.parse(BASE_IMAGE_URL).buildUpon();
    }
}
