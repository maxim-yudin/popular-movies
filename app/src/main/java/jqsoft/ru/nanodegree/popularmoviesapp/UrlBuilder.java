package jqsoft.ru.nanodegree.popularmoviesapp;

import android.net.Uri;

public class UrlBuilder {
    private static Uri.Builder getImageBaseUrlBuilder() {
        return Uri.parse(UrlConstants.BASE_IMAGE_URL).buildUpon();
    }

    private static Uri.Builder getBaseUrlBuilder() {
        return Uri.parse(UrlConstants.BASE_URL).buildUpon();
    }

    public static String getMovieList(String sortOrder) {
        return getBaseUrlBuilder().appendEncodedPath(UrlConstants.PATH_DISCOVER_MOVIE)
                .appendQueryParameter(UrlConstants.PARAM_API_KEY, UrlConstants.API_KEY_VALUE)
                .appendQueryParameter(UrlConstants.PARAM_SORT_BY, sortOrder)
                .toString();
    }

    public static String getPosterUrl(String imagePath) {
        return getImageBaseUrlBuilder().appendEncodedPath(UrlConstants.PATH_IMAGE_SIZE)
                .appendEncodedPath(imagePath)
                .toString();
    }

    public static String getBackdropUrl(String imagePath) {
        return getImageBaseUrlBuilder().appendEncodedPath(UrlConstants.PATH_BACKDROP_IMAGE_SIZE)
                .appendEncodedPath(imagePath)
                .toString();
    }
}
