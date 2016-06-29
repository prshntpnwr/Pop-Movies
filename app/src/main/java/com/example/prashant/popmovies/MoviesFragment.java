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
import android.view.Display;
import android.view.LayoutInflater;
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

public  class MoviesFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private ImageAdapter adapter;
    LoaderManager.LoaderCallbacks callbacks;

    static GridView gridview;
    LinearLayout layout;
    private int mPosition = GridView.INVALID_POSITION;

    private static final String SELECTED_KEY = "selected_position";

    static int width;
    static boolean sortByPop = true;

    static PreferenceChangeListener listener;
    static SharedPreferences prefs;
    static boolean sortByFavorites;


    private static final int MOVIE_LOADER_ID = 0;

    private static final String[] DETAIL_COLUMNS = {
            MovieContract.MovieEntry.COLUMN_MOVIE_ID,
            MovieContract.MovieEntry.COLUMN_POSTER_PATH,
            MovieContract.MovieEntry.COLUMN_RATING,
            MovieContract.MovieEntry.COLUMN_TITLE,
            MovieContract.MovieEntry.COLUMN_REVIEW,
            MovieContract.MovieEntry.COLUMN_YOUTUBE1,
            MovieContract.MovieEntry.COLUMN_YOUTUBE2,
            MovieContract.MovieEntry.COLUMN_OVERVIEW,
            MovieContract.MovieEntry.COLUMN_DATE
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
        layout = (LinearLayout) rootView.findViewById(R.id.linearlayout);
        callbacks = this;

        //It is a interface that apps use to talk or interact with the window manager
        WindowManager wm = (WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();    // Point hold two integer coordinates
        display.getSize(size);

        if (MainActivity.mTwoPane) {
            width = size.x / 6;
        } else width = size.x / 4;

        if (getActivity() != null) {
            ArrayList<String> array = new ArrayList<String>();
            ImageAdapter adapter = new ImageAdapter(getActivity(), array);

            gridview = (GridView) rootView.findViewById(R.id.gridview_poster);
            int mNumColumns = getContext().getResources().getInteger(R.integer.num_columns);
            gridview.setNumColumns(mNumColumns);
            gridview.setAdapter(adapter);
        }

        gridview.setOnItemClickListener
                (new AdapterView.OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        if (!MainActivity.mTwoPane){
                            if (!sortByFavorites) {
                                MoviesItem.favorited = bindFavoritesToMovies();
                                Intent intent = new Intent(getActivity(), DetailActivity.class).
                                        putExtra("overview", MoviesItem.overviews.get(position)).
                                        putExtra("poster", MoviesItem.posters.get(position)).
                                        putExtra("title", MoviesItem.titles.get(position)).
                                        putExtra("date", MoviesItem.dates.get(position)).
                                        putExtra("rating", MoviesItem.ratings.get(position)).
                                        putExtra("youtube", MoviesItem.youtubes1.get(position)).
                                        putExtra("youtube2", MoviesItem.youtubes2.get(position)).
                                        putExtra("comments", MoviesItem.comments.get(position)).
                                        putExtra("favorite", MoviesItem.favorited.get(position));
                                startActivity(intent);
                            }
                            else{
                                Intent intent = new Intent(getActivity(), DetailActivity.class).
                                        putExtra("overview", MoviesItem.overviewsF.get(position)).
                                        putExtra("poster", MoviesItem.postersF.get(position)).
                                        putExtra("title", MoviesItem.titlesF.get(position)).
                                        putExtra("date", MoviesItem.datesF.get(position)).
                                        putExtra("rating", MoviesItem.ratingsF.get(position)).
                                        putExtra("youtube", MoviesItem.youtubes1F.get(position)).
                                        putExtra("youtube2", MoviesItem.youtubes2F.get(position)).
                                        putExtra("comments", MoviesItem.commentsF.get(position)).
                                        putExtra("favorite", MoviesItem.favorited.get(position));

                                startActivity(intent);
                            }
                        }

                        else {
                            if (!sortByFavorites) {
                                MoviesItem.favorited = bindFavoritesToMovies();
                                Bundle bundle = new Bundle();
                                bundle.putString("overview", MoviesItem.overviews.get(position));
                                bundle.putString("poster", MoviesItem.posters.get(position));
                                bundle.putString("title", MoviesItem.titles.get(position));
                                bundle.putString("date", MoviesItem.dates.get(position));
                                bundle.putString("rating", MoviesItem.ratings.get(position));
                                bundle.putString("youtube", MoviesItem.youtubes1.get(position));
                                bundle.putString("youtube2", MoviesItem.youtubes2.get(position));
                                bundle.putStringArrayList("comments", MoviesItem.comments.get(position));
                                bundle.putBoolean("favorite", MoviesItem.favorited.get(position));

                                Fragment fragment = new DetailFragment();
                                fragment.setArguments(bundle);
                                getActivity().getSupportFragmentManager().beginTransaction().
                                        replace(R.id.movie_detail_container, fragment)
                                        .commit();
                            }
                            else{
                                Bundle bundle = new Bundle();
                                bundle.putString("overview", MoviesItem.overviewsF.get(position));
                                bundle.putString("poster", MoviesItem.postersF.get(position));
                                bundle.putString("title", MoviesItem.titlesF.get(position));
                                bundle.putString("date", MoviesItem.datesF.get(position));
                                bundle.putString("rating", MoviesItem.ratingsF.get(position));
                                bundle.putString("youtube", MoviesItem.youtubes1F.get(position));
                                bundle.putString("youtube2", MoviesItem.youtubes2F.get(position));
                                bundle.putStringArrayList("comments", MoviesItem.commentsF.get(position));
                                bundle.putBoolean("favorite", MoviesItem.favorited.get(position));

                                Fragment fragment = new DetailFragment();
                                fragment.setArguments(bundle);
                                getActivity().getSupportFragmentManager().beginTransaction().
                                        replace(R.id.movie_detail_container, fragment)
                                        .commit();
                            }
                        }

                        mPosition = position;
                    }
                });

        if (savedInstanceState != null && savedInstanceState.containsKey(SELECTED_KEY)){
            mPosition = savedInstanceState.getInt(SELECTED_KEY);
        }
        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        // When tablets rotate, the currently selected list item needs to be saved.
        // When no item is selected, mPosition will be set to gridview.INVALID_POSITION,
        // so check for that before storing.
        if (mPosition != GridView.INVALID_POSITION) {
            outState.putInt(SELECTED_KEY, mPosition);
        }
        super.onSaveInstanceState(outState);
    }

    private class PreferenceChangeListener implements SharedPreferences.OnSharedPreferenceChangeListener {
        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            gridview.setAdapter(null);
            if (isAdded()) {
                getLoaderManager().restartLoader(MOVIE_LOADER_ID, null, callbacks);
            }
        }
    }

    public ArrayList<Boolean> bindFavoritesToMovies() {

        ArrayList<Boolean> result = new ArrayList<>();
        for(int i = 0; i < MoviesItem.titles.size(); i++) {
            result.add(false);
        }
        for(String favoritedTitles: MoviesItem.titlesF) {

            for(int x = 0; x < MoviesItem.titles.size(); x++)
            {
                if(favoritedTitles.equals(MoviesItem.titles.get(x)))
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

        MoviesItem.postersF = new ArrayList<String>();
        MoviesItem.titlesF = new ArrayList<String>();
        MoviesItem.datesF = new ArrayList<String>();
        MoviesItem.overviewsF = new ArrayList<String>();
        MoviesItem.favorited = new ArrayList<Boolean>();
        MoviesItem.commentsF = new ArrayList<ArrayList<String>>();
        MoviesItem.youtubes1F = new ArrayList<String>();
        MoviesItem.youtubes2F = new ArrayList<String>();
        MoviesItem.ratingsF = new ArrayList<String>();

        getLoaderManager().initLoader(MOVIE_LOADER_ID, null, this);
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

        if (cursor == null) return;

        clearArrayLists();
        cursor.moveToPosition(-1);
        while (cursor.moveToNext()) {
            MoviesItem.postersF.add(cursor.getString(COL_MOVIE_POSTER_PATH));
            MoviesItem.commentsF.add(convertStringToArrayList(cursor.getString(COL_MOVIE_REVIEW)));
            MoviesItem.titlesF.add(cursor.getString(COL_MOVIE_TITLE));
            MoviesItem.overviewsF.add(cursor.getString(COL_MOVIE_OVERVIEW));
            MoviesItem.youtubes1F.add(cursor.getString(COL_MOVIE_YOUTUBE1));
            MoviesItem.youtubes2F.add(cursor.getString(COL_MOVIE_YOUTUBE2));
            MoviesItem.datesF.add(cursor.getString(COL_MOVIE_DATE));
            MoviesItem.ratingsF.add(cursor.getString(COL_MOVIE_RATING));
            MoviesItem.favorited.add(true);
        }

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

        if(sortByFavorites) {
            if (MoviesItem.postersF.size() == 0) {
                textView.setText("You have no favorites movies.");
                if (layout.getChildCount() == 1)
                    layout.addView(textView);
                gridview.setVisibility(GridView.GONE);
            } else {
                gridview.setVisibility(GridView.VISIBLE);
                layout.removeView(textView);
            }

            if (MoviesItem.postersF != null && getActivity() != null) {
                ImageAdapter adapter = new ImageAdapter(getActivity(), MoviesItem.postersF);
                gridview.setAdapter(adapter);
            }
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

        if (mPosition != GridView.INVALID_POSITION) {
            gridview.smoothScrollToPosition(mPosition);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }

    public void clearArrayLists() {
        MoviesItem.postersF.clear();
        MoviesItem.commentsF.clear();
        MoviesItem.titlesF.clear();
        MoviesItem.overviewsF.clear();
        MoviesItem.youtubes1F.clear();
        MoviesItem.youtubes2F.clear();
        MoviesItem.datesF.clear();
        MoviesItem.ratingsF.clear();
        MoviesItem.favorited.clear();
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
                    MoviesItem.posters = new ArrayList(Arrays.asList(getPathsFromAPI(sortByPop)));
                    return MoviesItem.posters;
                } catch (Exception e) {
                    continue;
                }
            }
        }

        @Override
        protected void onPostExecute(ArrayList<String> result) {
            if (result != null && getActivity() != null && !sortByFavorites) {
                ImageAdapter adapter = new ImageAdapter(getActivity(), result);
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
                        urlString = "http://api.themoviedb.org/3/discover/movie?sort_by=popularity.desc&api_key=" + MoviesItem.api_key;
                    } else {
                        urlString = "http://api.themoviedb.org/3/discover/movie?sort_by=vote_average.desc&vote_count.gte=500&api_key=" + MoviesItem.api_key;
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
                        MoviesItem.overviews = new ArrayList<String>(Arrays.asList(getStringsFromJSON(JSONResult,"overview")));
                        MoviesItem.titles = new ArrayList<String>(Arrays.asList(getStringsFromJSON(JSONResult,"original_title")));
                        MoviesItem.ratings = new ArrayList<String>(Arrays.asList(getStringsFromJSON(JSONResult,"vote_average")));
                        MoviesItem.dates = new ArrayList<String>(Arrays.asList(getStringsFromJSON(JSONResult,"release_date")));
                        MoviesItem.ids = new ArrayList<String>(Arrays.asList(getStringsFromJSON(JSONResult,"id")));
                        while(true)
                        {
                            MoviesItem.youtubes1 = new ArrayList<String>(Arrays.asList(getYoutubesFromIds(MoviesItem.ids,0)));
                            MoviesItem.youtubes2 = new ArrayList<String>(Arrays.asList(getYoutubesFromIds(MoviesItem.ids,1)));
                            int nullCount = 0;
                            for(int i = 0; i< MoviesItem.youtubes1.size(); i++)
                            {
                                if(MoviesItem.youtubes1.get(i) == null)
                                {
                                    nullCount++;
                                    MoviesItem.youtubes1.set(i, "no video found");
                                }
                            }
                            for(int i = 0; i < MoviesItem.youtubes2.size(); i++)
                            {
                                if(MoviesItem.youtubes2.get(i) == null)
                                {
                                    nullCount++;
                                    MoviesItem.youtubes2.set(i, "no video found");
                                }
                            }
                            if(nullCount > 2) continue;
                            break;
                        }
                        MoviesItem.comments = getReviewsFromIds(MoviesItem.ids);
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
                        }catch(final IOException e){
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
                    urlString = "http://api.themoviedb.org/3/movie/" + ids.get(i) + "/videos?api_key=" + MoviesItem.api_key;

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
                        urlString = "http://api.themoviedb.org/3/movie/" + ids.get(i) + "/reviews?api_key=" + MoviesItem
                                .api_key;
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