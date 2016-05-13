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

    private SQLiteDatabase db;
    private MovieDbHelper mOpenHelper;

    SQLiteDatabase mWritableDataBase;
    SQLiteDatabase mReadableDataBase;

    @Override
    public boolean onCreate() {

        mOpenHelper = new MovieDbHelper(getContext());
        mWritableDataBase = mOpenHelper.getWritableDatabase();
        mReadableDataBase = mOpenHelper.getReadableDatabase();
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,String[] selectionArgs,
                        String sortOrder) {
        return  mReadableDataBase.query(MovieContract.MovieEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
    }

    @Override
    public String getType(Uri uri) {

        return MovieContract.MovieEntry.CONTENT_ITEM_TYPE;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {

        Uri returnUri;

        long _id = mWritableDataBase.insert(MovieContract.MovieEntry.TABLE_NAME, null, values);

        if (_id > 0) {
            returnUri = MovieContract.MovieEntry.buildMovieUri(_id);
        }
        else {
            throw new SQLException("Failed to add a record into " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        db.close();
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return mWritableDataBase.delete(MovieContract.MovieEntry.TABLE_NAME, selection, selectionArgs);
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return mWritableDataBase.update(MovieContract.MovieEntry.TABLE_NAME, values, selection,
                selectionArgs);
    }
}
