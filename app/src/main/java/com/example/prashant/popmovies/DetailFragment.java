package com.example.prashant.popmovies;

import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
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
import android.support.v7.widget.ShareActionProvider;
import android.widget.TextView;

import com.example.prashant.popmovies.data.MovieContract;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class DetailFragment extends Fragment  {

    public static String youtube1;
    public static String youtube2;
    public static String overview;
    public static String rating;
    public static String date;
    public static String review;
    public static String title;
    public static String poster;
    public static boolean favorite;
    public static ArrayList<String> comments;
    public static Button b;
    public static Button b1;
    public static Button b2;

    private ShareActionProvider mShareActionProvider;

    public DetailFragment() {
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
        review = null;

        rootView.setVisibility(View.INVISIBLE);

        if (savedInstanceState == null) {
            final Bundle bundle = getArguments();

            if (bundle != null) {
                rootView.setVisibility(View.VISIBLE);

                title = bundle.getString("title");
                TextView tv = (TextView) rootView.findViewById(R.id.title);
                tv.setText(title);

                overview = bundle.getString("overview");
                TextView tv1 = (TextView) rootView.findViewById(R.id.overview);
                tv1.setText(overview);

                rating = bundle.getString("rating");
                TextView tv2 = (TextView) rootView.findViewById(R.id.rating);
                tv2.setText(rating);

                date = bundle.getString("date");
                TextView tv3 = (TextView) rootView.findViewById(R.id.date);
                tv3.setText(date);

                poster = bundle.getString("poster");
                ImageView imageView = (ImageView) rootView.findViewById(R.id.poster);
                Picasso.with(getActivity()).load("http://image.tmdb.org/t/p/w185/" + poster).resize(
                        MoviesFragment.width, (int) (MoviesFragment.width * 1.5)).into(imageView);

                youtube1 = bundle.getString("youtube");

                youtube2 = bundle.getString("youtube2");

                b = (Button) rootView.findViewById(R.id.favorite);

                comments = bundle.getStringArrayList("comments");
                for (int i = 0; i < comments.size(); i++) {
                    LinearLayout layout = (LinearLayout) rootView.findViewById(R.id.linear);
                    View divider = new View(getActivity());
                    TextView tv4 = new TextView(getActivity());
                    RelativeLayout.LayoutParams p = new RelativeLayout.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT);
                    tv4.setLayoutParams(p);

                    int paddingPixel = 10;

                    float density = getActivity().getResources().getDisplayMetrics().density;
                    int paddingDP = (int) (paddingPixel * density);
                    tv4.setPadding(0, paddingDP, 0, paddingDP);
                    RelativeLayout.LayoutParams x = new RelativeLayout.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT);
                    x.height = 1;
                    divider.setLayoutParams(x);
                    divider.setBackgroundColor(Color.BLACK);

                    tv4.setText(comments.get(i));
                    layout.addView(divider);
                    layout.addView(tv4);

                    if (review == null) {
                        review = comments.get(i);
                    } else {
                        review += "divider123" + comments.get(i);
                    }
                }

                b = (Button) rootView.findViewById(R.id.favorite);
                favorite = bundle.getBoolean("favorite", false);
                if (!favorite) {
                    b.setText("FAVORITE");
                    b.getBackground().setColorFilter(Color.GRAY, PorterDuff.Mode.MULTIPLY);
                } else {
                    b.setText("UNFAVORITE");
                    b.getBackground().setColorFilter(Color.CYAN, PorterDuff.Mode.MULTIPLY);
                }

                b.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View view) {

                        if (b.getText().equals("FAVORITE")) {

                            b.setText("UNFAVORITE");
                            b.getBackground().setColorFilter(Color.CYAN, PorterDuff.Mode.MULTIPLY);

                            ContentValues values = new ContentValues();
                            values.put(MovieContract.MovieEntry.COLUMN_POSTER_PATH, DetailFragment.poster);
                            values.put(MovieContract.MovieEntry.COLUMN_OVERVIEW, DetailFragment.overview);
                            values.put(MovieContract.MovieEntry.COLUMN_RATING, DetailFragment.rating);
                            values.put(MovieContract.MovieEntry.COLUMN_DATE, DetailFragment.date);
                            values.put(MovieContract.MovieEntry.COLUMN_REVIEW, DetailFragment.review);
                            values.put(MovieContract.MovieEntry.COLUMN_YOUTUBE1, DetailFragment.youtube1);
                            values.put(MovieContract.MovieEntry.COLUMN_YOUTUBE2, DetailFragment.youtube2);
                            values.put(MovieContract.MovieEntry.COLUMN_TITLE, DetailFragment.title);

                            getContext().getContentResolver().insert(MovieContract.BASE_CONTENT_URI, values);

                        } else if (b.getText().equals("UNFAVORITE")) {
                            b.setText("FAVORITE");
                            b.getBackground().setColorFilter(Color.GRAY, PorterDuff.Mode.MULTIPLY);
                            getContext().getContentResolver().delete(MovieContract.BASE_CONTENT_URI,
                                    "title=?", new String[]{DetailFragment.title});
                            bundle.clear();
                        }
                    }
                });

                b1 = (Button) rootView.findViewById(R.id.trailer1);
                b1.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View view) {
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW,
                                Uri.parse("http://youtube.com" + "/watch?v=" + youtube1));
                        startActivity(browserIntent);
                    }
                });

                b2 = (Button) rootView.findViewById(R.id.trailer2);
                b2.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View view) {
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW,
                                Uri.parse("http://youtube.com" + "/watch?v=" + youtube2));
                        startActivity(browserIntent);
                    }
                });
            }
        }

        Intent intent = getActivity().getIntent();
        if (intent != null) {

            //rootView.setVisibility(View.VISIBLE);
            if (intent.hasExtra("overview")) {
                rootView.setVisibility(View.VISIBLE);
                overview = intent.getStringExtra("overview");
                TextView tv = (TextView) rootView.findViewById(R.id.overview);
                tv.setText(overview);

            }
            if (intent.hasExtra("title")) {
                rootView.setVisibility(View.VISIBLE);
                title = intent.getStringExtra("title");
                TextView tv = (TextView) rootView.findViewById(R.id.title);
                tv.setText(title);

            }
            if (intent.hasExtra("rating")) {
                rootView.setVisibility(View.VISIBLE);
                rating = intent.getStringExtra("rating");
                TextView tv = (TextView) rootView.findViewById(R.id.rating);
                tv.setText(rating);
            }
            if (intent.hasExtra("date")) {
                rootView.setVisibility(View.VISIBLE);
                date = intent.getStringExtra("date");
                TextView tv = (TextView) rootView.findViewById(R.id.date);
                tv.setText(date);

            }
            if (intent.hasExtra("poster")) {
                rootView.setVisibility(View.VISIBLE);
                poster = intent.getStringExtra("poster");
                ImageView imageView = (ImageView) rootView.findViewById(R.id.poster);
                Picasso.with(getActivity()).load("http://image.tmdb.org/t/p/w185/" + poster).resize(
                        MoviesFragment.width, (int) (MoviesFragment.width * 1.5)).into(imageView);

            }
            if (intent.hasExtra("youtube")) {
                rootView.setVisibility(View.VISIBLE);
                youtube1 = intent.getStringExtra("youtube");

            }
            if (intent.hasExtra("youtube2")) {
                rootView.setVisibility(View.VISIBLE);
                youtube2 = intent.getStringExtra("youtube2");

            }
            if (intent.hasExtra("comments")) {
                rootView.setVisibility(View.VISIBLE);
                comments = intent.getStringArrayListExtra("comments");
                for (int i = 0; i < comments.size(); i++) {
                    LinearLayout layout = (LinearLayout) rootView.findViewById(R.id.linear);
                    View divider = new View(getActivity());
                    TextView tv = new TextView(getActivity());
                    RelativeLayout.LayoutParams p = new RelativeLayout.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT);
                    tv.setLayoutParams(p);

                    int paddingPixel = 10;

                    float density = getActivity().getResources().getDisplayMetrics().density;
                    int paddingDP = (int) (paddingPixel * density);
                    tv.setPadding(0, paddingDP, 0, paddingDP);
                    RelativeLayout.LayoutParams x = new RelativeLayout.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT);
                    x.height = 1;
                    divider.setLayoutParams(x);
                    divider.setBackgroundColor(Color.BLACK);

                    tv.setText(comments.get(i));
                    layout.addView(divider);
                    layout.addView(tv);

                    if (review == null) {
                        review = comments.get(i);
                    } else {
                        review += "divider123" + comments.get(i);
                    }
                }
            }

            b = (Button) rootView.findViewById(R.id.favorite);
            if (intent.hasExtra("favorite")) {
                rootView.setVisibility(View.VISIBLE);
                favorite = intent.getBooleanExtra("favorite", false);
                if (!favorite) {
                    b.setText("FAVORITE");
                    b.getBackground().setColorFilter(Color.GRAY, PorterDuff.Mode.MULTIPLY);
                } else {
                    b.setText("UNFAVORITE");
                    b.getBackground().setColorFilter(Color.CYAN, PorterDuff.Mode.MULTIPLY);
                }
            }
        }
        return rootView;
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        inflater.inflate(R.menu.detail, menu);
        MenuItem item = menu.findItem(R.id.action_share);

        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(item);
        if(mShareActionProvider !=null)
        {
            mShareActionProvider.setShareIntent(createShareIntent());
        }
    }

    private Intent createShareIntent()
    {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, "Check out this trailer for " + title + ": " +
                "https://www.youtube.com/watch?v=" + youtube1);
        return shareIntent;
    }
}