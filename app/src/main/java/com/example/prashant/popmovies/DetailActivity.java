package com.example.prashant.popmovies;

import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.prashant.popmovies.data.MovieContract;
import com.example.prashant.popmovies.data.MoviesProvider;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class DetailActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.movie_detail_container, new DetailFragment())
                .commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //no inspection Simplifiable If Statement
        if(id == R.id.action_settings){
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void favorite(View v){
        Button b = (Button) findViewById(R.id.favorite);
        if (b.getText().equals("FAVORITE")){

            b.setText("UNFAVORITE");
            b.getBackground().setColorFilter(Color.CYAN, PorterDuff.Mode.MULTIPLY);

            ContentValues values = new ContentValues();
            values.put(MovieContract.MovieEntry.COLUMN_POSTER_PATH,DetailFragment.poster);
            values.put(MovieContract.MovieEntry.COLUMN_OVERVIEW,DetailFragment.overview);
            values.put(MovieContract.MovieEntry.COLUMN_RATING,DetailFragment.rating);
            values.put(MovieContract.MovieEntry.COLUMN_DATE,DetailFragment.date);
            values.put(MovieContract.MovieEntry.COLUMN_REVIEW,DetailFragment.review);
            values.put(MovieContract.MovieEntry.COLUMN_YOUTUBE1,DetailFragment.youtube1);
            values.put(MovieContract.MovieEntry.COLUMN_YOUTUBE2, DetailFragment.youtube2);
            values.put(MovieContract.MovieEntry.COLUMN_TITLE, DetailFragment.title);

            getContentResolver().insert(MovieContract.BASE_CONTENT_URI, values);

        }

        else if(b.getText().equals("UNFAVORITE")) {
            b.setText("FAVORITE");
            b.getBackground().setColorFilter(Color.GRAY, PorterDuff.Mode.MULTIPLY);
            getContentResolver().delete(MovieContract.BASE_CONTENT_URI,
                    "title=?", new String[]{DetailFragment.title});
        }
    }


    public void trailer1(View v)
    {
        //launch activity with first youtube video
        Intent browserIntent = new Intent(Intent.ACTION_VIEW,
                Uri.parse("http://www.youtube.com/watch?v=" + DetailFragment.youtube1));
        startActivity(browserIntent);
        Toast.makeText(this, "Launching Trailer", Toast.LENGTH_SHORT).show();
    }
    public void trailer2(View v)
    {
        //launch activity with second youtube video
        Intent browserIntent = new Intent(Intent.ACTION_VIEW,
                Uri.parse("http://www.youtube.com/watch?v=" + DetailFragment.youtube2));
        startActivity(browserIntent);
        Toast.makeText(this, "Launching Trailer", Toast.LENGTH_SHORT).show();

    }

}