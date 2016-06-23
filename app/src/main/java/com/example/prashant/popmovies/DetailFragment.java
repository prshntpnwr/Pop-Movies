package com.example.prashant.popmovies;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.support.v7.widget.ShareActionProvider;
import android.widget.TextView;

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

    private ShareActionProvider mShareActionProvider;

    public DetailFragment() {
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
        review = null;

        if (savedInstanceState == null) {
            Bundle bundle = getArguments();

            if (bundle!=null){

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
                ImageView iv = (ImageView) rootView.findViewById(R.id.poster);
                Picasso.with(getActivity()).load("http://image.tmdb.org/t/p/w185/" + poster).resize(
                        MoviesFragment.width, (int)(MoviesFragment.width*1.5)).into(iv);

                youtube1 = bundle.getString("youtube");

                youtube2 = bundle.getString("youtube2");

                b = (Button)rootView.findViewById(R.id.favorite);

                favorite = bundle.getBoolean("favorite", false);
                if (!favorite) {
                    b.setText("FAVORITE");
                    b.getBackground().setColorFilter(Color.GRAY, PorterDuff.Mode.MULTIPLY);
                } else {
                    b.setText("UNFAVORITE");
                    b.getBackground().setColorFilter(Color.CYAN, PorterDuff.Mode.MULTIPLY);
                }

            }
        }

        getActivity().setTitle("Movie Details");
        Intent intent = getActivity().getIntent();
        if(intent !=null && intent.hasExtra("overview"))
        {
            overview = intent.getStringExtra("overview");
            TextView tv = (TextView) rootView.findViewById(R.id.overview);
            tv.setText(overview);

        }
        if(intent !=null && intent.hasExtra("title"))
        {
            title = intent.getStringExtra("title");
            TextView tv = (TextView) rootView.findViewById(R.id.title);
            tv.setText(title);

        }
        if(intent !=null && intent.hasExtra("rating"))
        {
            rating = intent.getStringExtra("rating");
            TextView tv = (TextView) rootView.findViewById(R.id.rating);
            tv.setText(rating);
        }
        if(intent !=null && intent.hasExtra("date"))
        {
            date = intent.getStringExtra("date");
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
            youtube1 = intent.getStringExtra("youtube");

        }
        if(intent !=null && intent.hasExtra("youtube2"))
        {
            youtube2 = intent.getStringExtra("youtube2");

        }
        if(intent !=null && intent.hasExtra("comments"))
        {
            comments = intent.getStringArrayListExtra("comments");
            for(int i = 0; i < comments.size();i++)
            {
                LinearLayout layout = (LinearLayout) rootView.findViewById(R.id.linear);
                View divider = new View(getActivity());
                TextView tv = new TextView(getActivity());
                RelativeLayout.LayoutParams p = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
                tv.setLayoutParams(p);

                int paddingPixel = 10;

                float density = getActivity().getResources().getDisplayMetrics().density;
                int paddingDP = (int) (paddingPixel * density);
                tv.setPadding(0, paddingDP, 0, paddingDP);
                RelativeLayout.LayoutParams x = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
                x.height = 1;
                divider.setLayoutParams(x);
                divider.setBackgroundColor(Color.BLACK);

                tv.setText(comments.get(i));
                layout.addView(divider);
                layout.addView(tv);

                if(review == null)
                {
                    review = comments.get(i);
                }
                else{
                    review+="divider123" + comments.get(i);
                }
            }

        }

        b = (Button)rootView.findViewById(R.id.favorite);
        if(intent !=null && intent.hasExtra("favorite")) {
            favorite = intent.getBooleanExtra("favorite", false);
            if (!favorite) {
                b.setText("FAVORITE");
                b.getBackground().setColorFilter(Color.GRAY, PorterDuff.Mode.MULTIPLY);
            } else {
                b.setText("UNFAVORITE");
                b.getBackground().setColorFilter(Color.CYAN, PorterDuff.Mode.MULTIPLY);
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