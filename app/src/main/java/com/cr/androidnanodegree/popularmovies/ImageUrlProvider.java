package com.cr.androidnanodegree.popularmovies;

import android.net.Uri;
import android.util.Log;

import java.io.IOException;
import java.net.URL;

/**
 * MoviePosterUrlProvider
 *
 * Provides the url for the posters from a poster path
 */
public class ImageUrlProvider {

    protected String LOG_TAG = ImageUrlProvider.class.getSimpleName();
    protected final String POSTER_BASE_URL = "http://image.tmdb.org/t/p/";
    protected String posterWidth = "w342";

    public ImageUrlProvider() {

    }

    public ImageUrlProvider(String posterWidth) {
        this.posterWidth = posterWidth;
    }

    public URL getImageUrl(String posterPath)
    {
        Uri builtUri = Uri.parse(POSTER_BASE_URL).buildUpon()
                .appendPath(posterWidth)
                .appendEncodedPath(posterPath)
                .build();

        try {
            return new URL(builtUri.toString());
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error ", e);
            return null;
        }
    }
}
