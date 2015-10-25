package com.cr.androidnanodegree.popularmovies;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Custom ArrayAdapter to hold and return the movie information
 */
public class MovieInformationAdapter extends ArrayAdapter<MovieInformation> {

    Context context;

    public MovieInformationAdapter(Context context, int resource, List<MovieInformation> objects) {
        super(context, resource, objects);

        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        TextView ratingTextView;

        LayoutInflater inflater = LayoutInflater.from(context);
        MovieInformation movieInformation = getItem(position);

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.grid_item_movie, parent, false);
        }

        imageView = (ImageView) convertView.findViewById(R.id.grid_item_movie_image);
        ratingTextView = (TextView) convertView.findViewById(R.id.grid_item_movie_rating);
        TextView yearTextView = (TextView) convertView.findViewById(R.id.grid_item_movie_year);

        convertView.setTag(imageView);
        convertView.setTag(ratingTextView);
        convertView.setTag(yearTextView);

        /* TODO remove if getPoster works better
        URL posterUrl = new ImageUrlProvider().getImageUrl(movieInformation.getPosterPath());

        Glide.with(context)
                .load(posterUrl.toString())
                .override(550, 825)
                .into(imageView);
        */
        imageView.setImageBitmap(movieInformation.getPoster());
        ratingTextView.setText(movieInformation.getVoteAverage());
        yearTextView.setText(movieInformation.getYearFromReleaseDate());

        // Read the ExtraInformation setting from the preferences
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        if (sharedPreferences.getBoolean(
                context.getString(R.string.pref_extrainformation_key), true)
                ) {
            yearTextView.setVisibility(View.VISIBLE);
            ratingTextView.setVisibility(View.VISIBLE);
        } else {
            yearTextView.setVisibility(View.INVISIBLE);
            ratingTextView.setVisibility(View.INVISIBLE);
        }

        return convertView;
    }
}
