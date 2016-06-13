package com.example.prashant.popmovies;


import android.content.Context;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.prashant.popmovies.data.MovieContract;

import static com.example.prashant.popmovies.R.drawable.placeholder;

public class MoviesAdapter extends CursorAdapter {

    public MoviesAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        View view = LayoutInflater.from(context).inflate(R.layout.fragment_detail, viewGroup, false);
        return null;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        ViewHolder viewHolder = (ViewHolder) view.getTag();

       // int viewType = getItemViewType(cursor.getPosition());

        long date = cursor.getLong(MoviesFragment.COL_MOVIE_DATE);
        viewHolder.dateView.setText(MovieContract.MovieEntry.COLUMN_DATE);

        String title = cursor.getString(MoviesFragment.COL_MOVIE_TITLE);
        viewHolder.titleView.setText(title);

        long rating = cursor.getLong(MoviesFragment.COL_MOVIE_RATING);
        viewHolder.ratingView.setText(MovieContract.MovieEntry.COLUMN_RATING);

        String overview = cursor.getString(MoviesFragment.COL_MOVIE_OVERVIEW);
        viewHolder.overviewView.setText(overview);

        String review = cursor.getString(MoviesFragment.COL_MOVIE_REVIEW);
        viewHolder.reviewView.setText(review);

        long poster = cursor.getLong(MoviesFragment.COL_MOVIE_POSTER_PATH);
        viewHolder.posterView.setImageResource(MoviesFragment.COL_MOVIE_ID);

        long trailer1 = cursor.getLong(MoviesFragment.COL_MOVIE_YOUTUBE1);
        viewHolder.trailerView1.setText(MovieContract.MovieEntry.COLUMN_YOUTUBE1);

        long trailer2 = cursor.getLong(MoviesFragment.COL_MOVIE_YOUTUBE2);
        viewHolder.dateView.setText(MovieContract.MovieEntry.COLUMN_YOUTUBE2);


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
