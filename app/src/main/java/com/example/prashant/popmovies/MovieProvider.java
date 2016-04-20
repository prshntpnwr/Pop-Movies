package com.example.prashant.popmovies;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

import java.util.HashMap;


public class MovieProvider extends ContentProvider {

    static final String PROVIDER_NAME = "com.example.prashant.provider.Movies";
    static final String URL = "content://" + PROVIDER_NAME + "/movies";
    static final Uri CONTENT_URI = Uri.parse(URL);


    static final String NAME = "name";
    static final String OVERVIEW = "overview";
    static final String DATE = "date";
    static final String REVIEW = "review";
    static final String RATING = "rating";
    static final String YOUTUBE1= "youtube1";
    static final String YOUTUBE2 = "youtube2";
    static final String TITLE = "title";



    private static HashMap<String, String> MOVIES_PROJECTION_MAP;

    static final int MOVIES = 1;
    static final int MOVIES_ID = 2;

    static final UriMatcher uriMatcher;
    static{
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(PROVIDER_NAME, "movies", MOVIES);
        uriMatcher.addURI(PROVIDER_NAME, "movies/#", MOVIES_ID);
    }

    /**
     * Database specific constant declarations
     */
    private SQLiteDatabase db;
    static final String DATABASE_NAME = "Movies";
    static final String MOVIES_TABLE_NAME = "Movies";
    static final int DATABASE_VERSION = 1;
    static final String CREATE_DB_TABLE =
            " CREATE TABLE " + MOVIES_TABLE_NAME +
                    " (name, " +
                    " overview, " +
                    " title, " +
                    " review, " +
                    " rating, " +
                    " youtube1, " +
                    " youtube2, " +
                    " date);";

    /**
     * Helper class that actually creates and manages
     * the provider's underlying data repository.
     */
    private static class DatabaseHelper extends SQLiteOpenHelper {
        DatabaseHelper(Context context){
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db)
        {
            db.execSQL(CREATE_DB_TABLE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " +  MOVIES_TABLE_NAME);
            onCreate(db);
        }
    }

    @Override
    public boolean onCreate() {
        Context context = getContext();
        DatabaseHelper dbHelper = new DatabaseHelper(context);

        /**
         * Create a write able database which will trigger its
         * creation if it doesn't already exist.
         */
        db = dbHelper.getWritableDatabase();
        return (db == null)? false:true;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        /**
         * Add a new record
         */
        long rowID = db.insert(MOVIES_TABLE_NAME, "", values);

        /**
         * If record is added successfully
         */

        if (rowID > 0)
        {
            Uri _uri = ContentUris.withAppendedId(CONTENT_URI, rowID);
            getContext().getContentResolver().notifyChange(_uri, null);
            return _uri;
        }
        throw new SQLException("Failed to add a record into " + uri);
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        qb.setTables(MOVIES_TABLE_NAME);

        switch (uriMatcher.match(uri)) {
            case MOVIES:
                qb.setProjectionMap(MOVIES_PROJECTION_MAP);
                break;

            case MOVIES_ID:
                qb.appendWhere( NAME + "=" + uri.getPathSegments().get(1));
                break;

            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        if (sortOrder == null || sortOrder == ""){
            //By default sort on movie titles
            sortOrder = NAME;
        }
        Cursor c = qb.query(db,	projection,	selection, selectionArgs,null, null, sortOrder);


         // register to watch a content URI for changes

        c.setNotificationUri(getContext().getContentResolver(), uri);
        return c;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int count = 0;

        switch (uriMatcher.match(uri)){
            case MOVIES:
                count = db.delete(MOVIES_TABLE_NAME, selection, selectionArgs);
                break;

            case MOVIES_ID:
                String id = uri.getPathSegments().get(1);
                count = db.delete( MOVIES_TABLE_NAME, NAME +  " = " + id +
                        (!TextUtils.isEmpty(selection) ? " AND (" + selection + ')' : ""), selectionArgs);
                break;

            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        int count = 0;

        switch (uriMatcher.match(uri)){
            case MOVIES:
                count = db.update(MOVIES_TABLE_NAME, values, selection, selectionArgs);
                break;

            case MOVIES_ID:
                count = db.update(MOVIES_TABLE_NAME, values, NAME + " = " + uri.getPathSegments().get(1) +
                        (!TextUtils.isEmpty(selection) ? " AND (" +selection + ')' : ""), selectionArgs);
                break;

            default:
                throw new IllegalArgumentException("Unknown URI " + uri );
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    @Override
    public String getType(Uri uri) {
        switch (uriMatcher.match(uri)){

            case MOVIES:
                return "vnd.android.cursor.dir/vnd.example.movies";


            case MOVIES_ID:
                return "vnd.android.cursor.item/vnd.example.movies";

            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
    }
    public void clearTable()
    {
        db.execSQL("DELETE FROM Movies");
    }

}