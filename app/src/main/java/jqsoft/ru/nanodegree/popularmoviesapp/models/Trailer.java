package jqsoft.ru.nanodegree.popularmoviesapp.models;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by maximyudin on 06.07.15.
 */
public class Trailer implements Parcelable {
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.Key);
        dest.writeString(this.Name);
    }

    public Trailer() {
    }

    protected Trailer(Parcel in) {
        this.Key = in.readString();
        this.Name = in.readString();
    }

    public static final Parcelable.Creator<Trailer> CREATOR = new Parcelable.Creator<Trailer>() {
        public Trailer createFromParcel(Parcel source) {
            return new Trailer(source);
        }

        public Trailer[] newArray(int size) {
            return new Trailer[size];
        }
    };
}
