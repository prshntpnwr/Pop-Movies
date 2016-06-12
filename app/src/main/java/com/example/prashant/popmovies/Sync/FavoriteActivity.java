package com.example.prashant.popmovies.Sync;

import android.app.Application;

import com.squareup.picasso.OkHttpDownloader;
import com.squareup.picasso.Picasso;


public class FavoriteActivity extends Application {

    @Override
    public void onCreate()
    {
        super.onCreate();

        Picasso.Builder builder = new Picasso.Builder(this);
        builder.downloader(new OkHttpDownloader(this, Integer.MAX_VALUE));
        Picasso built = builder.build();
        Picasso.setSingletonInstance(built);
    }
}

