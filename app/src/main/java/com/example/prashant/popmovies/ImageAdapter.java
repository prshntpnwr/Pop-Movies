package com.example.prashant.popmovies;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridLayout;
import android.widget.ImageView;

import com.example.prashant.popmovies.R;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;

public class ImageAdapter extends BaseAdapter {
    private Context mContext;
    private ArrayList<String> array;
    private int width;

    public ImageAdapter(Context c, ArrayList<String> paths)
    {
        mContext = c;
        array=paths;
        //width = x;
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

        imageView.setLayoutParams(
                new ViewGroup.LayoutParams(
                        GridLayout.LayoutParams.MATCH_PARENT,
                        GridLayout.LayoutParams.MATCH_PARENT ) );

        //Drawable d = resizeDrawable(mContext.getResources().getDrawable(R.drawable.placeholder));
        Drawable d = mContext.getResources().getDrawable(R.drawable.placeholder);
        Picasso.with(mContext).load("http://image.tmdb.org/t/p/w185/" + array.get(position)).
                placeholder(d).into(imageView);

        return imageView;

    }
   /* private Drawable resizeDrawable(Drawable image)
    {
        Bitmap b = ((BitmapDrawable)image).getBitmap();
        Bitmap bitmapResized = Bitmap.createScaledBitmap(b,width, (int)(width*1.5),false);
        return new BitmapDrawable(mContext.getResources(),bitmapResized);
    }*/
}