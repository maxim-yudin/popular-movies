package jqsoft.ru.nanodegree.popularmoviesapp.models;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.tjeannin.provigen.ProviGenBaseContract;
import com.tjeannin.provigen.annotation.Column;
import com.tjeannin.provigen.annotation.ContentUri;

import jqsoft.ru.nanodegree.popularmoviesapp.providers.MovieDbProvider;

/**
 * Created by maximyudin on 06.07.15.
 */
public class Trailer implements Parcelable {
    private static final String BASE_YOUTUBE_URL = "http://www.youtube.com/watch";
    private static final String PARAM_YOUTUBE_VIDEO = "v";

    @SerializedName("key")
    public String Key;

    @SerializedName("name")
    public String Name;

    public static String getYoutubeLinkByKey(String videoKey) {
        return getYoutubeBaseUrlBuilder().appendQueryParameter(PARAM_YOUTUBE_VIDEO, videoKey)
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

    private Trailer(Parcel in) {
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

    /**
     * Contract for DB and content provider
     */
    public interface Contract extends ProviGenBaseContract {
        @Column(Column.Type.TEXT)
        String MOVIE_ID = "movie_id";

        @Column(Column.Type.TEXT)
        String KEY = "key";

        @Column(Column.Type.TEXT)
        String NAME = "name";

        @ContentUri
        Uri CONTENT_URI = MovieDbProvider.getContentUri("trailers");
    }
}
