package com.example.prashant.popmovies.data;

import com.example.prashant.popmovies.data.MovieContract.MovieEntry;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class MovieDbHelper extends SQLiteOpenHelper{

    private static final int DATABASE_VERSION = 1;

    static final String DATABASE_NAME = "movies.db";


    public MovieDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        final String SQL_CREATE_MOVIE_TABLE = "CREATE TABLE " + MovieEntry.TABLE_NAME + " (" +
                MovieEntry.COLUMN_RATING +
                MovieEntry.COLUMN_TITLE +
                MovieEntry.COLUMN_REVIEW  +
                MovieEntry.COLUMN_YOUTUBE1 +
                MovieEntry.COLUMN_DATE +
                MovieEntry.COLUMN_POSTER_PATH +
                MovieEntry.COLUMN_YOUTUBE2 +
                MovieEntry.COLUMN_OVERVIEW + ");";

        sqLiteDatabase.execSQL(SQL_CREATE_MOVIE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {

        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MovieEntry.TABLE_NAME);

        onCreate(sqLiteDatabase);

    }
}
