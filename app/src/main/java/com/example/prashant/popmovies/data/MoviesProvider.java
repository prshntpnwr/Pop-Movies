package com.example.prashant.popmovies.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;


import java.util.HashMap;

public class MoviesProvider extends ContentProvider {

    private MovieDbHelper mOpenHelper;

    @Override
    public boolean onCreate() {

        mOpenHelper = new MovieDbHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,String[] selectionArgs,
                        String sortOrder) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        Cursor retCursor = db.query(MovieContract.MovieEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);;

        retCursor.setNotificationUri(getContext().getContentResolver(), uri);

        return retCursor;

    }

    @Override
    public String getType(Uri uri) {

        return MovieContract.MovieEntry.CONTENT_ITEM_TYPE;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {

        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();

        Uri returnUri;

        long _id = db.insert(MovieContract.MovieEntry.TABLE_NAME, null, values);

        if (_id > 0) {
            returnUri = MovieContract.MovieEntry.buildMovieUri(_id);
        }
        else {
            throw new android.database.SQLException("Failed to insert row into " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);

        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int rowDeleted;

        if ( null == selection ) selection = "1";

        rowDeleted = db.delete(MovieContract.MovieEntry.TABLE_NAME, selection, selectionArgs);

        if (rowDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int rowUpdated = db.update(MovieContract.MovieEntry.TABLE_NAME, values, selection,
                selectionArgs);

        if (rowUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowUpdated;
    }

}
