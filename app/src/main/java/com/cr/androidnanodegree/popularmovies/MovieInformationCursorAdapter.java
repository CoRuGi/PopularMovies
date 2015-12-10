package com.cr.androidnanodegree.popularmovies;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

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

        // Read poster from cursor
        byte[] blob = cursor.getBlob(MainActivityFragment.COL_POSTER);
        Bitmap bitmap = BitmapFactory.decodeByteArray(blob, 0, blob.length);
        viewHolder.poster.setImageBitmap(bitmap);

        // Read year from cursor
        String releaseDate = cursor.getString(MainActivityFragment.COL_RELEASE_DATE);
        String movieYear = Utility.getYearFromReleaseDate(releaseDate);
        viewHolder.movieYear.setText(movieYear);

        // Read rating from cursor
        String rating = cursor.getString(MainActivityFragment.COL_VOTE_AVERAGE);
        viewHolder.movieRating.setText(rating);

        // Show extra information if set to true
        if (Utility.getExtraInformationPreference(context)) {
            viewHolder.movieYear.setVisibility(View.VISIBLE);
            viewHolder.movieRating.setVisibility(View.VISIBLE);
        } else {
            viewHolder.movieYear.setVisibility(View.VISIBLE);
            viewHolder.movieRating.setVisibility(View.INVISIBLE);
        }
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
