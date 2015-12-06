package com.cr.androidnanodegree.popularmovies;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.net.URL;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailActivityFragment extends Fragment {

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
}
