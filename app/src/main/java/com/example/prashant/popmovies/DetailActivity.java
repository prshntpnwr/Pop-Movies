package com.example.prashant.popmovies;

import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.media.Image;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroupOverlay;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collection;


public class DetailActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        if(savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.containerDetail, new DetailFragment())
                    .commit();
        }
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
            //code to store data of movies into the db
            b.setText("UNFAVORITE");
            b.getBackground().setColorFilter(Color.CYAN, PorterDuff.Mode.MULTIPLY);

            ContentValues values = new ContentValues();

            values.put(MovieProvider.NAME,DetailFragment.poster);
            values.put(MovieProvider.OVERVIEW,DetailFragment.overview);
            values.put(MovieProvider.RATING,DetailFragment.rating);
            values.put(MovieProvider.DATE,DetailFragment.date);
            values.put(MovieProvider.REVIEW,DetailFragment.review);
            values.put(MovieProvider.YOUTUBE,DetailFragment.youtube);
            values.put(MovieProvider.YOUTUBE1,DetailFragment.youtube1);
            values.put(MovieProvider.TITLE,DetailFragment.title);

            getContentResolver().insert(MovieProvider.CONTENT_URI,values);

        }

        else {
            //code to store data of movies into the db
            b.setText("FAVORITE");
            b.getBackground().setColorFilter(Color.GRAY, PorterDuff.Mode.MULTIPLY);

            getContentResolver().delete(Uri.parse("content://com.example.prashant.provider.Movies/movies"),
                    "title=?",new String[]{DetailFragment.title});
        }
    }


    public void trailer1(View v)
    {
        //launch activity with first youtube video
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.youtube.com/watch?v=" + DetailFragment.youtube));
        startActivity(browserIntent);
        Toast.makeText(this,"Launching Trailer", Toast.LENGTH_SHORT).show();
    }
    public void trailer2(View v)
    {
        //launch activity with second youtube video
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.youtube.com/watch?v=" + DetailFragment.youtube1));
        startActivity(browserIntent);
        Toast.makeText(this,"Launching Trailer", Toast.LENGTH_SHORT).show();

    }

        /**
         * A placeholder fragment containing a simple view.
         */
        public static class DetailFragment extends Fragment {

            public static String youtube;
            public static String youtube1;
            public static String overview;
            public static String rating;
            public static String date;
            public static String review;
            public static String title;
            public static String poster;
            public static boolean favorite;
            public static ArrayList<String> comments;
            public static Button b;


            private static final String LOG_TAG = DetailFragment.class.getSimpleName();
            private static final String MOVIE_SHARE_HASHTAG = "#PopMovieApp : Checkout This Trailer ";

            public DetailFragment() {
                setHasOptionsMenu(true);
            }

            @Override
            public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                     Bundle savedInstanceState) {

                Intent intent = getActivity().getIntent();
                View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
                getActivity().setTitle("Movie Details");

                if (intent != null && intent.hasExtra("overview")) {

                    overview = intent.getStringExtra("overview");
                    TextView tv = (TextView) rootView.findViewById(R.id.overview);
                    tv.setText(overview);

                }

                if (intent != null && intent.hasExtra("title")) {

                    title = intent.getStringExtra("title");
                    TextView tv = (TextView) rootView.findViewById(R.id.title);
                    tv.setText(title);
                }

                if (intent != null && intent.hasExtra("rating")) {

                    rating = intent.getStringExtra("rating");
                    TextView tv = (TextView) rootView.findViewById(R.id.rating);
                    tv.setText(rating);
                }


                if (intent != null && intent.hasExtra("date")) {

                    date= intent.getStringExtra("date");
                    TextView tv = (TextView) rootView.findViewById(R.id.date);
                    tv.setText(date);
                }

                if(intent !=null && intent.hasExtra("poster"))
                {
                    poster = intent.getStringExtra("poster");
                    ImageView iv = (ImageView) rootView.findViewById(R.id.poster);

                    Picasso.with(getActivity()).load("http://image.tmdb.org/t/p/w185/" + poster).resize(
                            MoviesFragment.width, (int)(MoviesFragment.width*1.5)).into(iv);

                }

                if(intent !=null && intent.hasExtra("youtube"))
                {
                    youtube = intent.getStringExtra("youtube");

                }

                if(intent !=null && intent.hasExtra("youtube1"))
                {
                    youtube1 = intent.getStringExtra("youtube1");
                }

                if(intent !=null && intent.hasExtra("comments"))
                {
                    comments = intent.getStringArrayListExtra("comments");

                    for (int i = 0; i < comments.size(); i++){
                        LinearLayout layout = (LinearLayout) rootView.findViewById(R.id.linear);
                        View divider = new View(getActivity());
                        TextView tv = new TextView(getActivity());
                        RelativeLayout.LayoutParams p = new RelativeLayout.LayoutParams
                                (ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
                        tv.setLayoutParams(p);
                        int paddingPixel = 10;
                        float density = getActivity().getResources().getDisplayMetrics().density;
                        int paddingDP = (int) (paddingPixel *density);
                        tv.setPadding(0, paddingDP, 0, paddingDP);

                        RelativeLayout.LayoutParams x = new RelativeLayout.LayoutParams
                                (ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);

                        x.height = 1;

                        divider.setLayoutParams(x);
                        divider.setBackgroundColor(Color.BLACK);

                        tv.setText(comments.get(i));
                        layout.addView(divider);
                        layout.addView(tv);

                        if(review == null){
                            review = comments.get(i);
                        }

                        else{
                            review += "@divider@" + comments.get(i);
                        }
                    }
                }

                b = (Button)rootView.findViewById(R.id.favorite);
                if(intent !=null && intent.hasExtra("favorite"))
                {
                    favorite = intent.getBooleanExtra("favorite", false);
                    if(!favorite)
                    {
                        b.setText("FAVORITE");
                        b.getBackground().setColorFilter(Color.GRAY, PorterDuff.Mode.MULTIPLY);
                    }
                    else
                    {
                        b.setText("UNFAVORITE");
                        b.getBackground().setColorFilter(Color.BLUE, PorterDuff.Mode.MULTIPLY);
                    }
                }



                return rootView;
            }

            @Override
            public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

                inflater.inflate(R.menu.detail, menu);

                MenuItem menuItem = menu.findItem(R.id.action_share);

                ShareActionProvider mShareActionProvider =
                        (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);

                if (mShareActionProvider != null) {
                    mShareActionProvider.setShareIntent(createShareMovieIntent());
                } else {
                    Log.d(LOG_TAG, "Share Action Provider is null?");
                }
            }


            private Intent createShareMovieIntent() {
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_TEXT, MOVIE_SHARE_HASHTAG + ": " + title + ":" +
                        "http://www.youtube.com/watch?v=" + youtube);
                return shareIntent;
            }
        }

}
