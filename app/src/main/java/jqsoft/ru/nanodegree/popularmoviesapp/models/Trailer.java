package jqsoft.ru.nanodegree.popularmoviesapp.models;

import android.net.Uri;

import com.google.gson.annotations.SerializedName;

/**
 * Created by maximyudin on 06.07.15.
 */
public class Trailer {
    private static final String BASE_YOUTUBE_URL = "http://www.youtube.com/watch";
    private static final String PARAM_YOUTUBE_VIDEO = "v";

    @SerializedName("key")
    private String Key;

    @SerializedName("name")
    public String Name;

    public String getYoutubeLink() {
        return getYoutubeBaseUrlBuilder().appendQueryParameter(PARAM_YOUTUBE_VIDEO, Key)
                .toString();
    }

    private static Uri.Builder getYoutubeBaseUrlBuilder() {
        return Uri.parse(BASE_YOUTUBE_URL).buildUpon();
    }
}
