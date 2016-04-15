package com.example.prashant.popmovies;

import android.content.Context;
import android.content.res.Configuration;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

public class MainActivity extends ActionBarActivity {

    public static boolean TABLET = false;

    private final String LOG_TAG = MainActivity.class.getSimpleName();

    public boolean isTABLET(Context context){
        boolean xlarge = ((context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK)==4);
        boolean large = ((context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK)==Configuration.SCREENLAYOUT_SIZE_LARGE);
        return (xlarge || large);
    }


        @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
            TABLET = isTABLET(this);
            if(savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container, new MoviesFragment())
                        .commit();
            }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    public boolean onOptionItemSelected(MenuItem item){
        int id = item.getItemId();


        return super.onOptionsItemSelected(item);
    }

    public static class PlaceholderFragment extends Fragment {
        public PlaceholderFragment () {

        }

        @Override
        public View onCreateView(LayoutInflater inflater,ViewGroup container,
                                 Bundle savedInsatanceState){
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            return rootView;
        }
    }
}
