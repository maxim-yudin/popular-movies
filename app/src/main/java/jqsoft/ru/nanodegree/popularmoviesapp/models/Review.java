package jqsoft.ru.nanodegree.popularmoviesapp.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

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

    protected Review(Parcel in) {
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
}
