package jqsoft.ru.nanodegree.popularmoviesapp.providers;

import android.content.Context;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;

import com.tjeannin.provigen.ProviGenOpenHelper;
import com.tjeannin.provigen.ProviGenProvider;

import jqsoft.ru.nanodegree.popularmoviesapp.models.Movie;
import jqsoft.ru.nanodegree.popularmoviesapp.models.Review;
import jqsoft.ru.nanodegree.popularmoviesapp.models.Trailer;

/**
 * Created by tum0rc0re on 10.08.15.
 */
public class MovieDbProvider extends ProviGenProvider {
    private static final String DB_NAME = "MovieDb";
    private static final int DB_VERSION = 1;

    private static final String AUTHORITY = "content://jqsoft.ru.nanodegree.popularmoviesapp";

    private static final Class[] contracts = new Class[]{
            Movie.Contract.class, Trailer.Contract.class, Review.Contract.class};

    @Override
    public SQLiteOpenHelper openHelper(Context context) {
        return new ProviGenOpenHelper(context, DB_NAME, null, DB_VERSION, contracts);
    }

    @Override
    public Class[] contractClasses() {
        return contracts;
    }

    public static Uri getContentUri(String path) {
        return Uri.parse(AUTHORITY).buildUpon().appendPath(path).build();
    }
}
