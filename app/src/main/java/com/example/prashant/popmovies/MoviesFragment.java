package com.example.prashant.popmovies;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Point;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.prashant.popmovies.data.MovieContract;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;

import com.example.prashant.popmovies.DetailActivity;
public  class MoviesFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private ImageAdapter adapter;

    static GridView gridview;
    private int mPosition = GridView.INVALID_POSITION;

    private static final String SELECTED_KEY = "selected_position";

    static int width;
    static boolean sortByPop = true;
    static String api_key = "b7f57ee32644eb6ddfdca9ca38b5513e";

    static PreferenceChangeListener listener;
    static SharedPreferences prefs;
    static boolean sortByFavorites;
    static ArrayList<String> postersF;
    static ArrayList<String> titlesF;
    static ArrayList<String> datesF;
    static ArrayList<String> ratingsF;
    static ArrayList<String> youtubes1F;
    static ArrayList<String> youtubes2F;
    static ArrayList<ArrayList<String>> commentsF;
    static ArrayList<String> overviewsF;

    static ArrayList<String> overviews;
    static ArrayList<String> titles;
    static ArrayList<String> dates;
    static ArrayList<String> ratings;
    static ArrayList<String> youtubes1;
    static ArrayList<String> youtubes2;
    static ArrayList<String> ids;
    static ArrayList<String> posters;
    static ArrayList<Boolean> favorited;
    static ArrayList<ArrayList<String>> comments;

    private static final int MOVIE_LOADER_ID = 0;

    private static final String[] DETAIL_COLUMNS = {
            //MovieContract.MovieEntry.TABLE_NAME + "." + MovieContract.MovieEntry._ID,
            MovieContract.MovieEntry.COLUMN_MOVIE_ID,
            MovieContract.MovieEntry.COLUMN_POSTER_PATH,
            MovieContract.MovieEntry.COLUMN_RATING,
            MovieContract.MovieEntry.COLUMN_TITLE,
            MovieContract.MovieEntry.COLUMN_REVIEW,
            MovieContract.MovieEntry.COLUMN_YOUTUBE1,
            MovieContract.MovieEntry.COLUMN_YOUTUBE2,
            MovieContract.MovieEntry.COLUMN_DATE,
            MovieContract.MovieEntry.COLUMN_OVERVIEW

    };


    static final int COL_MOVIE_ID = 0;
    static final int COL_MOVIE_POSTER_PATH = 1;
    static final int COL_MOVIE_RATING = 2;
    static final int COL_MOVIE_TITLE = 3;
    static final int COL_MOVIE_REVIEW = 4;
    static final int COL_MOVIE_YOUTUBE1 = 5;
    static final int COL_MOVIE_YOUTUBE2 = 6;
    static final int COL_MOVIE_OVERVIEW = 7;
    static final int COL_MOVIE_DATE = 8;

    public interface Callback {
        /**
         * DetailFragmentCallback for when an item has been selected.
         */
        public void onItemSelected(Uri dateUri);
    }

    public MoviesFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        //It is a interface that apps use to talk or interact with the window manager
        WindowManager wm = (WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();    // Point hold two interger coordinates
        display.getSize(size);

        //to set the poster in gridview
        // 3 poster in each row if table and 2 for the mobile phone
        if (MainActivity.TABLET) {
            width = size.x / 3;
        } else width = size.x / 2;


        if (getActivity() != null) {
            ArrayList<String> array = new ArrayList<String>();
            ImageAdapter adapter = new ImageAdapter(getActivity(), array, width);
            gridview = (GridView) rootView.findViewById(R.id.gridview_poster);

            gridview.setColumnWidth(width);

            gridview.setAdapter(adapter);

        }

        gridview.setOnItemClickListener
                (new AdapterView.OnItemClickListener() {

                     @Override
                     public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                         if (!sortByFavorites) {
                             favorited = bindFavoritesToMovies();
                             Intent intent = new Intent(getActivity(), DetailActivity.class).
                                     putExtra("overview", overviews.get(position)).
                                     putExtra("poster", posters.get(position)).
                                     putExtra("title", titles.get(position)).
                                     putExtra("date", dates.get(position)).
                                     putExtra("rating", ratings.get(position)).
                                     putExtra("youtube", youtubes1.get(position)).
                                     putExtra("youtube2", youtubes2.get(position)).
                                     putExtra("comments", comments.get(position)).
                                     putExtra("favorite", favorited.get(position));

                             startActivity(intent);

                         }

                         else{
                             Intent intent = new Intent(getActivity(), DetailActivity.class).
                                     putExtra("overriew", overviewsF.get(position)).
                                     putExtra("poster", postersF.get(position)).
                                     putExtra("title", titlesF.get(position)).
                                     putExtra("date", datesF.get(position)).
                                     putExtra("rating", ratingsF.get(position)).
                                     putExtra("youtube", youtubes1F.get(position)).
                                     putExtra("youtube2", youtubes2F.get(position)).
                                     putExtra("comments", commentsF.get(position)).
                                     putExtra("favorite", favorited.get(position));

                             startActivity(intent);
                         }
                         mPosition = position;
                     }
                 }
                );

        if (savedInstanceState != null && savedInstanceState.containsKey(SELECTED_KEY)){
            mPosition = savedInstanceState.getInt(SELECTED_KEY);
        }

        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        // When tablets rotate, the currently selected list item needs to be saved.
        // When no item is selected, mPosition will be set to Listview.INVALID_POSITION,
        // so check for that before storing.
        if (mPosition != GridView.INVALID_POSITION) {
            outState.putInt(SELECTED_KEY, mPosition);
        }
        super.onSaveInstanceState(outState);
    }

    private class PreferenceChangeListener implements SharedPreferences.OnSharedPreferenceChangeListener{

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            gridview.setAdapter(null);
            onStart();
        }
    }

    public ArrayList<Boolean> bindFavoritesToMovies() {

        ArrayList<Boolean> result = new ArrayList<>();
        for(int i = 0; i < titles.size(); i++) {

            result.add(false);
        }
        for(String favoritedTitles: titlesF) {

            for(int x = 0; x < titles.size(); x++)
            {
                if(favoritedTitles.equals(titles.get(x)))
                {
                    result.set(x,true);
                }
            }
        }
        return result;
    }

    @Override
    public void onStart() {

        super.onStart();

        postersF = new ArrayList<String>();
        titlesF = new ArrayList<String>();
        datesF = new ArrayList<String>();
        overviewsF = new ArrayList<String>();
        favorited = new ArrayList<Boolean>();
        commentsF = new ArrayList<ArrayList<String>>();
        youtubes1F = new ArrayList<String>();
        youtubes2F = new ArrayList<String>();
        ratingsF = new ArrayList<String>();

        prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        listener = new PreferenceChangeListener();
        prefs.registerOnSharedPreferenceChangeListener(listener);


        if(prefs.getString("sortby","popularity").equals("popularity"))
        {
            getActivity().setTitle("Most Popular Movies");
            sortByPop = true;
            sortByFavorites=false;
        }
        else if(prefs.getString("sortby","popularity").equals("rating"))
        {
            getActivity().setTitle("Highest Rated Movies");
            sortByPop = false;
            sortByFavorites=false;
        }
        else if(prefs.getString("sortby","popularity").equals("favorites"))
        {
            getActivity().setTitle("Favorited Movies");
            sortByPop = false;
            sortByFavorites=true;
        }

        TextView textView = new TextView(getActivity());
        LinearLayout layout = (LinearLayout) getActivity().findViewById(R.id.linearlayout);

        if(sortByFavorites) {

            getLoaderManager().initLoader(MOVIE_LOADER_ID, null, this);
            getLoaderManager().restartLoader(MOVIE_LOADER_ID, null, this);

        }

        else {
            gridview.setVisibility(GridView.VISIBLE);
            layout.removeView(textView);

            if (isNetworkAvailable()) {
                new ImageLoadTask().execute();

            }

            else {
                TextView textview1 = new TextView(getActivity());
                LinearLayout layout1 = (LinearLayout) getActivity().findViewById(R.id.linearlayout);
                textview1.setText("You are not connected to the Internet");

                if (layout1.getChildCount() == 1) {
                    layout1.addView(textview1);
                }
                gridview.setVisibility(GridView.GONE);
            }
        }
    }

    public static ArrayList<String> convertStringToArrayList(String s) {

        ArrayList<String> result = new ArrayList<>(Arrays.asList(s.split("divider123")));
        return result;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        return new CursorLoader(getActivity(),
                MovieContract.BASE_CONTENT_URI,
                DETAIL_COLUMNS,
                null,
                null,
                "title"
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

        //postersF = new ArrayList<String>();

        if (cursor == null) return;

        while (cursor.moveToNext()) {
            postersF.add(cursor.getString(COL_MOVIE_POSTER_PATH));
            commentsF.add(convertStringToArrayList(cursor.getString(COL_MOVIE_REVIEW)));
            titlesF.add(cursor.getString(COL_MOVIE_TITLE));
            overviewsF.add(cursor.getString(COL_MOVIE_OVERVIEW));
            youtubes1F.add(cursor.getString(COL_MOVIE_YOUTUBE1));
            youtubes2F.add(cursor.getString(COL_MOVIE_YOUTUBE2));
            datesF.add(cursor.getString(COL_MOVIE_DATE));
            ratingsF.add(cursor.getString(COL_MOVIE_RATING));
            favorited.add(true);

        }

        TextView textView = new TextView(getActivity());
        LinearLayout layout = (LinearLayout) getActivity().findViewById(R.id.linearlayout);

        if (postersF.size() == 0) {
            textView.setText("You have no favorites movies.");
            if (layout.getChildCount() == 1)
                layout.addView(textView);
            gridview.setVisibility(GridView.GONE);
        } else {
            gridview.setVisibility(GridView.VISIBLE);
            layout.removeView(textView);
        }
        if (postersF != null && getActivity() != null) {
            ImageAdapter adapter = new ImageAdapter(getActivity(), postersF, width);
            gridview.setAdapter(adapter);
        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }


    public boolean isNetworkAvailable() {
        //It is a class that answer all the queries about the os network connectivity.
        //also notifies app when connection changes
        ConnectivityManager connectivityManager = (ConnectivityManager) getActivity()
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        //To get the instance of current network connection
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


    public class ImageLoadTask extends AsyncTask<Void, Void, ArrayList<String>> {

        private final String LOG_TAG = ImageLoadTask.class.getSimpleName();

        @Override
        protected ArrayList<String> doInBackground(Void... params) {
            while (true) {
                try {
                    //storing posters into an arraylist poster
                    posters = new ArrayList(Arrays.asList(getPathsFromAPI(sortByPop)));
                    return posters;
                } catch (Exception e) {
                    continue;
                }
            }

        }

        @Override
        protected void onPostExecute(ArrayList<String> result) {
            if (result != null && getActivity() != null) {
                ImageAdapter adapter = new ImageAdapter(getActivity(), result, width);
                gridview.setAdapter(adapter);

            }
        }

        public String[] getPathsFromAPI(boolean sortbypop) {

            while(true) {

                HttpURLConnection urlConnection = null;
                BufferedReader reader = null;
                String JSONResult;

                try {
                    String urlString = null;
                    if (sortbypop) {
                        urlString = "http://api.themoviedb.org/3/discover/movie?sort_by=popularity.desc&api_key=" + api_key;
                    } else {
                        urlString = "http://api.themoviedb.org/3/discover/movie?sort_by=vote_average.desc&vote_count.gte=500&api_key=" + api_key;
                    }
                    URL url = new URL(urlString);
                    urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setRequestMethod("GET");
                    urlConnection.connect();

                    //Read the input stream into a String
                    InputStream inputStream = urlConnection.getInputStream();
                    StringBuffer buffer = new StringBuffer();
                    if (inputStream == null) {
                        return null;
                    }
                    reader = new BufferedReader(new InputStreamReader(inputStream));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        buffer.append(line + "\n");
                    }
                    if (buffer.length() == 0) {
                        return null;
                    }
                    JSONResult = buffer.toString();

                    try {
                        overviews = new ArrayList<String>(Arrays.asList(getStringsFromJSON(JSONResult,"overview")));
                        titles = new ArrayList<String>(Arrays.asList(getStringsFromJSON(JSONResult,"original_title")));
                        ratings = new ArrayList<String>(Arrays.asList(getStringsFromJSON(JSONResult,"vote_average")));
                        dates = new ArrayList<String>(Arrays.asList(getStringsFromJSON(JSONResult,"release_date")));
                        ids = new ArrayList<String>(Arrays.asList(getStringsFromJSON(JSONResult,"id")));
                        while(true)
                        {
                            youtubes1 = new ArrayList<String>(Arrays.asList(getYoutubesFromIds(ids,0)));
                            youtubes2 = new ArrayList<String>(Arrays.asList(getYoutubesFromIds(ids,1)));
                            int nullCount = 0;
                            for(int i = 0; i< youtubes1.size(); i++)
                            {
                                if(youtubes1.get(i) == null)
                                {
                                    nullCount++;
                                    youtubes1.set(i, "no video found");
                                }
                            }
                            for(int i = 0; i < youtubes2.size(); i++)
                            {
                                if(youtubes2.get(i) == null)
                                {
                                    nullCount++;
                                    youtubes2.set(i, "no video found");
                                }
                            }
                            if(nullCount > 2) continue;
                            break;
                        }
                        comments = getReviewsFromIds(ids);
                        return getPathsFromJSON(JSONResult);

                    } catch (JSONException e) {
                        return null;
                    }
                }catch(Exception e)
                {
                    continue;
                }finally {
                    if(urlConnection != null)
                    {
                        urlConnection.disconnect();
                    }
                    if(reader != null)
                    {
                        try{
                            reader.close();
                        }catch(final IOException e)
                        {
                        }
                    }
                }
            }
        }

        public String[] getYoutubesFromIds(ArrayList<String> ids, int position) {

            String[] results = new String[ids.size()];
            for(int i =0; i<ids.size(); i++)
            {
                HttpURLConnection urlConnection = null;
                BufferedReader reader = null;
                String JSONResult;

                try {
                    String urlString = null;
                    urlString = "http://api.themoviedb.org/3/movie/" + ids.get(i) + "/videos?api_key=" + api_key;


                    URL url = new URL(urlString);
                    urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setRequestMethod("GET");
                    urlConnection.connect();

                    //Read the input stream into a String
                    InputStream inputStream = urlConnection.getInputStream();
                    StringBuffer buffer = new StringBuffer();
                    if (inputStream == null) {
                        return null;
                    }
                    reader = new BufferedReader(new InputStreamReader(inputStream));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        buffer.append(line + "\n");
                    }
                    if (buffer.length() == 0) {
                        return null;
                    }
                    JSONResult = buffer.toString();
                    try {
                        results[i] = getYoutubeFromJSON(JSONResult, position);
                    } catch (JSONException E) {
                        results[i] = "no video found";
                    }
                } catch (Exception e) {

                } finally {
                    if (urlConnection != null) {
                        urlConnection.disconnect();
                    }
                    if (reader != null) {
                        try {
                            reader.close();
                        } catch (final IOException e) {
                        }
                    }
                }
            }
            return results;

        }

        public ArrayList<ArrayList<String>> getReviewsFromIds(ArrayList<String> ids) {

            outerloop:
            while(true)
            {
                ArrayList<ArrayList<String>> results = new ArrayList<>();
                for(int i =0; i<ids.size(); i++)
                {
                    HttpURLConnection urlConnection = null;
                    BufferedReader reader = null;
                    String JSONResult;

                    try {
                        String urlString = null;
                        urlString = "http://api.themoviedb.org/3/movie/" + ids.get(i) + "/reviews?api_key=" + api_key;
                        URL url = new URL(urlString);
                        urlConnection = (HttpURLConnection) url.openConnection();
                        urlConnection.setRequestMethod("GET");
                        urlConnection.connect();

                        //Read the input stream into a String
                        InputStream inputStream = urlConnection.getInputStream();
                        StringBuffer buffer = new StringBuffer();
                        if (inputStream == null) {
                            return null;
                        }
                        reader = new BufferedReader(new InputStreamReader(inputStream));
                        String line;
                        while ((line = reader.readLine()) != null) {
                            buffer.append(line + "\n");
                        }
                        if (buffer.length() == 0) {
                            return null;
                        }
                        JSONResult = buffer.toString();
                        try {
                            results.add(getCommentsFromJSON(JSONResult));
                        } catch (JSONException E) {
                            return null;
                        }
                    } catch (Exception e) {
                        continue outerloop;

                    } finally {
                        if (urlConnection != null) {
                            urlConnection.disconnect();
                        }
                        if (reader != null) {
                            try {
                                reader.close();
                            } catch (final IOException e) {
                            }
                        }
                    }
                }
                return results;

            }
        }

        public ArrayList<String> getCommentsFromJSON(String JSONStringParam)throws JSONException{
            JSONObject JSONString = new JSONObject(JSONStringParam);
            JSONArray reviewsArray = JSONString.getJSONArray("results");
            ArrayList<String> results = new ArrayList<>();
            if(reviewsArray.length()==0)
            {
                results.add("No reviews found for this movie.");
                return results;
            }
            for(int i = 0; i<reviewsArray.length(); i++)
            {
                JSONObject result = reviewsArray.getJSONObject(i);
                results.add(result.getString("content"));
            }
            return results;

        }

        public String getYoutubeFromJSON(String JSONStringParam, int position) throws JSONException
        {
            JSONObject JSONString = new JSONObject(JSONStringParam);
            JSONArray youtubesArray = JSONString.getJSONArray("results");
            JSONObject youtube;
            String result = "no videos found";
            if(position ==0)
            {
                youtube = youtubesArray.getJSONObject(0);
                result = youtube.getString("key");
            }
            else if(position==1)
            {
                if(youtubesArray.length()>1)
                {
                    youtube = youtubesArray.getJSONObject(1);
                }
                else{
                    youtube = youtubesArray.getJSONObject(0);
                }
                result = youtube.getString("key");
            }
            return result;
        }
        public String[] getStringsFromJSON(String JSONStringParam, String param)  throws JSONException
        {
            JSONObject JSONString = new JSONObject(JSONStringParam);

            JSONArray moviesArray = JSONString.getJSONArray("results");
            String[] result = new String[moviesArray.length()];

            for(int i = 0; i<moviesArray.length();i++)
            {
                JSONObject movie = moviesArray.getJSONObject(i);
                if(param.equals("vote_average"))
                {
                    Double number = movie.getDouble("vote_average");
                    String rating =Double.toString(number)+"/10";
                    result[i]=rating;
                }
                else {
                    String data = movie.getString(param);
                    result[i] = data;
                }
            }
            return result;
        }
        public String[] getPathsFromJSON(String JSONStringParam) throws JSONException{

            JSONObject JSONString = new JSONObject(JSONStringParam);

            JSONArray moviesArray = JSONString.getJSONArray("results");
            String[] result = new String[moviesArray.length()];

            for(int i = 0; i<moviesArray.length();i++)
            {
                JSONObject movie = moviesArray.getJSONObject(i);
                String moviePath = movie.getString("poster_path");
                result[i] = moviePath;
            }
            return result;
        }
    }
}