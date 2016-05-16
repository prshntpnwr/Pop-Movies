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

import com.example.prashant.popmovies.Sync.FetchMovieTask;
import com.example.prashant.popmovies.Sync.ImageAdapter;

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


public  class MoviesFragment extends Fragment{

    static GridView gridview;
    static int width;
    static boolean sortByPop = true;
    static String api_key = "b7f57ee32644eb6ddfdca9ca38b5513e";
    static ArrayList<String> fposters;


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
                                     putExtra("overview", overviewsF.get(position)).
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
                     }
                 }

        );


        return rootView;
    }

    private class PreferenceChangeListener implements SharedPreferences.OnSharedPreferenceChangeListener{


        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            gridview.setAdapter(null);
            onStart();
        }
    }
    public ArrayList<Boolean> bindFavoritesToMovies()
    {
        ArrayList<Boolean> result = new ArrayList<>();
        for(int i =0; i<titles.size();i++)
        {
            result.add(false);
        }
        for(String favoritedTitles: titlesF)
        {
            for(int x = 0; x<titles.size(); x++)
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
    public void onStart()
    {
        super.onStart();
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
        LinearLayout layout = (LinearLayout)getActivity().findViewById(R.id.linearlayout);

        loadFavoritesData();

        if(sortByFavorites)
        {
            if(postersF.size()==0)
            {
                textView.setText("You have no favorites movies.");
                if(layout.getChildCount()==1)
                    layout.addView(textView);
                gridview.setVisibility(GridView.GONE);
            }
            else{
                gridview.setVisibility(GridView.VISIBLE);
                layout.removeView(textView);
            }
            if(postersF!=null&&getActivity()!=null)
            {
                ImageAdapter adapter = new ImageAdapter(getActivity(),postersF,width);
                gridview.setAdapter(adapter);
            }
        }
        else {
            gridview.setVisibility(GridView.VISIBLE);
            layout.removeView(textView);

            if (isNetworkAvailable()) {
                new FetchMovieTask.ImageLoadTask(getActivity()).execute();
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


    public static ArrayList<String> convertStringToArrayList(String s)
    {
        ArrayList<String> result = new ArrayList<>(Arrays.asList(s.split("divider123")));
        return result;
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

    public void loadFavoritesData()
    {
        Uri uri = MovieContract.BASE_CONTENT_URI;
        Cursor c = getActivity().getContentResolver().query(uri,null,null,null,"title");
        postersF = new ArrayList<String>();
        titlesF = new ArrayList<String>();
        datesF = new ArrayList<String>();
        overviewsF = new ArrayList<String>();
        favorited = new ArrayList<Boolean>();
        commentsF = new ArrayList<ArrayList<String>>();
        youtubes1F = new ArrayList<String>();
        youtubes2F = new ArrayList<String>();
        ratingsF = new ArrayList<String>();

        if(c==null) return;

        while(c.moveToNext())
        {
            postersF.add(c.getString(c.getColumnIndex(MovieContract.MovieEntry.COLUMN_POSTER_PATH)));
            commentsF.add(MoviesFragment.convertStringToArrayList(c.getString(c.getColumnIndex(MovieContract.MovieEntry.COLUMN_REVIEW))));
            titlesF.add(c.getString(c.getColumnIndex(MovieContract.MovieEntry.COLUMN_TITLE)));
            overviewsF.add(c.getString(c.getColumnIndex(MovieContract.MovieEntry.COLUMN_OVERVIEW)));
            youtubes1F.add(c.getString(c.getColumnIndex(MovieContract.MovieEntry.COLUMN_YOUTUBE1)));
            youtubes2F.add(c.getString(c.getColumnIndex(MovieContract.MovieEntry.COLUMN_YOUTUBE2)));
            datesF.add(c.getString(c.getColumnIndex(MovieContract.MovieEntry.COLUMN_DATE)));
            ratingsF.add(c.getString(c.getColumnIndex(MovieContract.MovieEntry.COLUMN_RATING)));
            favorited.add(true);

        }

        c.close();
    }


}