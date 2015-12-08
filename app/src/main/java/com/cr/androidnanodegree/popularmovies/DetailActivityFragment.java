package com.cr.androidnanodegree.popularmovies;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.cr.androidnanodegree.popularmovies.data.MovieContract.FavoritesEntry;

import java.net.URL;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailActivityFragment extends Fragment {

    private final static String LOG_TAG = DetailActivityFragment.class.getSimpleName();
    protected MovieInformation movieInformation;

    public DetailActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

        Intent intent = getActivity().getIntent();
        if (intent != null && intent.hasExtra("MovieInformation")) {
            movieInformation = intent.getParcelableExtra("MovieInformation");
        }

        FetchMovieDetailTask movieDetailTask = new FetchMovieDetailTask(this);
        movieDetailTask.execute(Integer.parseInt(movieInformation.getId()));

        Button button = (Button) rootView.findViewById(R.id.fragment_detail_button_favorite);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                onMarkAsFavoriteClick(v);
            }
        });
        
        TextView title = (TextView) rootView.findViewById(R.id.detail_movie_title);
        TextView year = (TextView) rootView.findViewById(R.id.detail_movie_year);
        TextView average = (TextView) rootView.findViewById(R.id.detail_movie_average_rating);
        TextView synopsis = (TextView) rootView.findViewById(R.id.detail_movie_synopsis);
        ImageView imageView = (ImageView) rootView.findViewById(R.id.detail_movie_poster);
        ImageView backgroundImageView = (ImageView) rootView.findViewById(R.id.detail_movie_background);

        title.setText(movieInformation.getTitle());
        year.setText(movieInformation.getYearFromReleaseDate());
        average.setText(movieInformation.getVoteAverage());
        synopsis.setText(movieInformation.getSynopsis());

        //URL posterUrl = new ImageUrlProvider().getImageUrl(movieInformation.getPosterPath());
        URL backgroundUrl = new ImageUrlProvider("w780").getImageUrl(movieInformation.getBackdropPath());

        // TODO Remove if getPoster works better
        /*
        Glide.with(getContext())
                .load(posterUrl.toString())
                .override(550, 825)
                .into(imageView);
        */
        imageView.setImageBitmap(movieInformation.getPoster());

        Glide.with(getContext())
                .load(backgroundUrl.toString())
                .into(backgroundImageView);

        if (Build.VERSION.SDK_INT <= 15 ) {
            backgroundImageView.setAlpha(50);
        } else {
            backgroundImageView.setImageAlpha(50);
        }
        return rootView;
    }

    public void onMarkAsFavoriteClick(View view) {
        Log.d(LOG_TAG, "Button pressed!");

        // Check if the movie is already in the database
        Cursor cursor = getContext().getContentResolver().query(
                FavoritesEntry.CONTENT_URI,
                new String[] {FavoritesEntry.COLUMN_MOVIE_ID, FavoritesEntry.COLUMN_MOVIE_ID},
                FavoritesEntry.COLUMN_MOVIE_ID + " = ?",
                new String[] {movieInformation.getId()},
                null
        );

        // If movie is already in the database we don't have to add it
        if (cursor.moveToFirst()) {
            return;
        }

        // Prepare the values to be entered into the database
        ContentValues values = new ContentValues();

        values.put(FavoritesEntry.COLUMN_MOVIE_ID, movieInformation.getId());
        values.put(FavoritesEntry.COLUMN_MOVIE_TITLE, movieInformation.getTitle());
        values.put(FavoritesEntry.COLUMN_POSTER_PATH, movieInformation.getPosterPath());
        values.put(FavoritesEntry.COLUMN_MOVIE_SYNOPSIS, movieInformation.getSynopsis());
        values.put(FavoritesEntry.COLUMN_VOTE_AVERAGE, movieInformation.getVoteAverage());
        values.put(FavoritesEntry.COLUMN_RELEASE_DATE, movieInformation.getReleaseDate());
        values.put(FavoritesEntry.COLUMN_BACKDROP_PATH, movieInformation.getBackdropPath());

        getContext().getContentResolver().insert(FavoritesEntry.CONTENT_URI, values);
    }
}
