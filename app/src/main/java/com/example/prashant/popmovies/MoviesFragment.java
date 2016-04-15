package com.example.prashant.popmovies;

import android.content.Context;
import android.graphics.Point;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.prashant.popmovies.ImageAdapter;
import com.example.prashant.popmovies.MainActivity;
import com.example.prashant.popmovies.R;

import java.util.ArrayList;
import java.util.Arrays;

//sample movie path: "/kqjL17yufvn9OVLyXYpvtyrFfak.jpg"
public  class MoviesFragment extends Fragment {

    static GridView gridview;
    static int width;
    static ArrayList<String> posters;
    static boolean sortByPop;
    public MoviesFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        WindowManager wm =(WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        if(MainActivity.TABLET)
        {
            width = size.x/3;
        }
        else width=size.x/2;
        if(getActivity()!=null)
        {
            ArrayList<String> array = new ArrayList<String>();
            ImageAdapter adapter = new ImageAdapter(getActivity(),array,width);
            gridview = (GridView)rootView.findViewById(R.id.gridview_movie);

            gridview.setColumnWidth(width);
            gridview.setAdapter(adapter);
        }
        //listen for presses on gridview items
        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.v("THERE IS THE POSITION :" , position + "");
            }
        });


        return rootView;
    }
    @Override
    public void onStart()
    {
        super.onStart();
        getActivity().setTitle("Most Popular Movies");

        if(isNetworkAvailable())
        {
            gridview.setVisibility(GridView.VISIBLE);
            new ImageLoadTask().execute();
        }
        else{
            TextView textview1 = new TextView(getActivity());
            RelativeLayout layout1 = (RelativeLayout)getActivity().findViewById(R.id.relativelayout);
            textview1.setText("You are not connected to the Internet");
            if(layout1.getChildCount()==1)
            {
                layout1.addView(textview1);
            }
            gridview.setVisibility(GridView.GONE);
        }
    }

    public boolean isNetworkAvailable()
    {
        ConnectivityManager connectivityManager = (ConnectivityManager)getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo !=null &&activeNetworkInfo.isConnected();
    }
    public class ImageLoadTask extends AsyncTask<Void, Void, ArrayList<String>>{

        private final String LOG_TAG = ImageLoadTask.class.getSimpleName();

        @Override
        protected ArrayList<String> doInBackground(Void... params) {
            while(true){
                try{
                    posters = new ArrayList(Arrays.asList(getPathsFromAPI(sortByPop)));
                    return posters;
                }
                catch(Exception e)
                {
                    continue;
                }
            }

        }
        @Override
        protected void onPostExecute(ArrayList<String>result)
        {
            if(result!=null && getActivity()!=null)
            {
                ImageAdapter adapter = new ImageAdapter(getActivity(),result, width);
                gridview.setAdapter(adapter);

            }
        }
        public String[] getPathsFromAPI(boolean sort)
        {
            String[] array = new String[15];
            for(int i = 0; i<array.length;i++)
            {
                array[i] = "/kqjL17yufvn9OVLyXYpvtyrFfak.jpg";
            }
            return array;

        }
    }
}