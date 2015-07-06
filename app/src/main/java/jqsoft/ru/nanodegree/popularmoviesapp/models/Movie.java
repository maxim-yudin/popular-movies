package jqsoft.ru.nanodegree.popularmoviesapp.models;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class Movie implements Parcelable {
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.Id);
        dest.writeString(this.OriginalTitle);
        dest.writeString(this.Overview);
        dest.writeString(this.VoteAverage);
        dest.writeString(this.ReleaseDate);
        dest.writeString(this.backdropPath);
        dest.writeString(this.posterPath);
    }

    public Movie() {
    }

    protected Movie(Parcel in) {
        this.Id = in.readString();
        this.OriginalTitle = in.readString();
        this.Overview = in.readString();
        this.VoteAverage = in.readString();
        this.ReleaseDate = in.readString();
        this.backdropPath = in.readString();
        this.posterPath = in.readString();
    }

    public static final Parcelable.Creator<Movie> CREATOR = new Parcelable.Creator<Movie>() {
        public Movie createFromParcel(Parcel source) {
            return new Movie(source);
        }

        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };
}
