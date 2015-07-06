package jqsoft.ru.nanodegree.popularmoviesapp.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by maximyudin on 06.07.15.
 */
public class Review {
    @SerializedName("author")
    public String Author;

    @SerializedName("content")
    public String Content;
}
