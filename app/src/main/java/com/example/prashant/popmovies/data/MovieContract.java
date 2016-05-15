package com.example.prashant.popmovies.data;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

import com.example.prashant.popmovies.BuildConfig;

public class MovieContract {

    // The "Content authority" is a name for the entire content provider, similar to the
    // relationship between a domain name and its website.  A convenient string to use for the
    // content authority is the package name for the app, which is guaranteed to be unique on the
    // device.
    public static final String CONTENT_AUTHORITY = "com.example.prashant.popmovies";

    // Use CONTENT_AUTHORITY to create the base of all URI's which apps will use to contact
    // the content provider.
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY + "/movies");

    public static final String PATH_MOVIE = "movies";

    public static final class MovieEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
               BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIE).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + PATH_MOVIE;

        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + PATH_MOVIE;

        public static final String TABLE_NAME = "movies";

        public static final String COLUMN_POSTER_PATH = "poster";

        public static final String COLUMN_RATING = "ratings";

        public static final String COLUMN_TITLE = "title";

        public static final String COLUMN_REVIEW = "review";

        public static final String COLUMN_YOUTUBE1 = "youtube1";

        public static final String COLUMN_YOUTUBE2 = "youtube2";

        public static final String COLUMN_OVERVIEW = "overview";

        public static final String COLUMN_DATE = "date";

        public static Uri buildMovieUri(long id) {
            //ContentUris.withAppendedId() is a helper method to create an id-based URI
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }


    }

}
