package com.example.prashant.popmovies;


import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.prashant.popmovies.data.MovieContract;

public class MoviesAdapter extends CursorAdapter {

    public MoviesAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {

        View view = LayoutInflater.from(context).inflate(R.layout.fragment_detail, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        ViewHolder viewHolder = (ViewHolder) view.getTag();

        String date = cursor.getString(MoviesFragment.COL_MOVIE_DATE);
        viewHolder.dateView.setText(date);

        String title = cursor.getString(MoviesFragment.COL_MOVIE_TITLE);
        viewHolder.titleView.setText(title);

        String rating = cursor.getString(MoviesFragment.COL_MOVIE_RATING);
        viewHolder.ratingView.setText(rating);

        String overview = cursor.getString(MoviesFragment.COL_MOVIE_OVERVIEW);
        viewHolder.overviewView.setText(overview);

        String review = cursor.getString(MoviesFragment.COL_MOVIE_REVIEW);
        viewHolder.reviewView.setText(review);

        String poster = cursor.getString(MoviesFragment.COL_MOVIE_POSTER_PATH);
        viewHolder.posterView.setImageResource(poster);

        String trailer1 = cursor.getString(MoviesFragment.COL_MOVIE_YOUTUBE1);
        viewHolder.trailerView1.setText(trailer1);

        String trailer2 = cursor.getString(MoviesFragment.COL_MOVIE_YOUTUBE2);
        viewHolder.dateView.setText(trailer2);


    }

    public static class ViewHolder {
        public final ImageView posterView;
        public final TextView titleView;
        public final TextView dateView;
        public final TextView ratingView;
        public final TextView overviewView;
        public final TextView reviewView;
        public final TextView trailerView1;
        public final TextView trailerView2;

        public ViewHolder (View view) {
            posterView = (ImageView) view.findViewById(R.id.poster);
            titleView = (TextView) view.findViewById(R.id.title);
            reviewView = (TextView) view.findViewById(R.id.reviews);
            ratingView = (TextView) view.findViewById(R.id.rating);
            overviewView = (TextView) view.findViewById(R.id.overview);
            dateView = (TextView) view.findViewById(R.id.date);
            trailerView1 = (TextView) view.findViewById(R.id.trailer1);
            trailerView2 = (TextView) view.findViewById(R.id.trailer2);

        }
    }
}
