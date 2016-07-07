package com.example.prashant.popmovies;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;

public class ImageAdapter extends BaseAdapter {
    private Context mContext;
    private ArrayList<String> array;

    public ImageAdapter(Context c, ArrayList<String> paths)
    {
        mContext = c;
        array=paths;
    }

    @Override
    public int getCount() {
        return array.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        if(convertView == null)
        {
            imageView = new ImageView(mContext);
        }
        else{
            imageView = (ImageView) convertView;
        }

        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imageView.setAdjustViewBounds(true);
        imageView.setLayoutParams(
                new LinearLayout.LayoutParams(
                        GridLayout.LayoutParams.MATCH_PARENT,
                        GridLayout.LayoutParams.MATCH_PARENT ) );

        Picasso.with(mContext)
                .load("http://image.tmdb.org/t/p/w185/" + array.get(position))
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.error)
                .into(imageView);

        return imageView;
    }
}