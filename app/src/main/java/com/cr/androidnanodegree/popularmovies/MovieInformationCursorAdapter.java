package com.cr.androidnanodegree.popularmovies;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.net.URL;
import java.util.concurrent.ExecutionException;

/**
 * Cursor adapter for when the favorites option is selected
 */
public class MovieInformationCursorAdapter extends CursorAdapter {
    protected static final String LOG_TAG = MovieInformationCursorAdapter.class.getSimpleName();

    public MovieInformationCursorAdapter(Context context, Cursor cursor, int flags) {
        super(context, cursor, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {

        View view = LayoutInflater.from(context).inflate(R.layout.grid_item_movie, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);

        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder viewHolder = (ViewHolder) view.getTag();
        Bitmap bitmap;

        // Read poster from cursor
        URL posterUrl = new ImageUrlProvider().getImageUrl(
                cursor.getString(MainActivityFragment.COL_POSTER_PATH)
        );

        try {
            bitmap = Glide.with(context)
                    .load(posterUrl)
                    .asBitmap()
                    .into(550, 825)
                    .get();
        } catch (InterruptedException e) {
            Log.e(LOG_TAG, "InterruptedException: " + e);
            bitmap = null;
        } catch (ExecutionException e) {
            Log.e(LOG_TAG, "ExecutionException: " + e);
            bitmap = null;
        }

        viewHolder.poster.setImageBitmap(bitmap);

        // Read year from cursor
        String releaseDate = cursor.getString(MainActivityFragment.COL_RELEASE_DATE);
        String movieYear = Utility.getYearFromReleaseDate(releaseDate);
        viewHolder.movieYear.setText(movieYear);

        // Read rating from cursor
        String rating = cursor.getString(MainActivityFragment.COL_VOTE_AVERAGE);
        viewHolder.movieRating.setText(rating);
    }

    public static class ViewHolder {
        public final ImageView poster;
        public final TextView movieYear;
        public final TextView movieRating;

        public ViewHolder(View view) {
            poster = (ImageView) view.findViewById(R.id.grid_item_movie_image);
            movieYear = (TextView) view.findViewById(R.id.grid_item_movie_year);
            movieRating = (TextView) view.findViewById(R.id.grid_item_movie_rating);
        }
    }
}
