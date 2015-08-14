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
public class Review implements Parcelable {
    @SerializedName("author")
    public String Author;

    @SerializedName("content")
    public String Content;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.Author);
        dest.writeString(this.Content);
    }

    public Review() {
    }

    private Review(Parcel in) {
        this.Author = in.readString();
        this.Content = in.readString();
    }

    public static final Parcelable.Creator<Review> CREATOR = new Parcelable.Creator<Review>() {
        public Review createFromParcel(Parcel source) {
            return new Review(source);
        }

        public Review[] newArray(int size) {
            return new Review[size];
        }
    };

    /**
     * Contract for DB and content provider
     */
    public interface Contract extends ProviGenBaseContract {
        @Column(Column.Type.TEXT)
        String MOVIE_ID = "movie_id";

        @Column(Column.Type.TEXT)
        String AUTHOR = "author";

        @Column(Column.Type.TEXT)
        String CONTENT = "content";

        @ContentUri
        Uri CONTENT_URI = MovieDbProvider.getContentUri("reviews");
    }
}
