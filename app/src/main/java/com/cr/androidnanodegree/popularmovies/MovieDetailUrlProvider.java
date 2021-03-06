package com.cr.androidnanodegree.popularmovies;

import android.net.Uri;
import android.util.Log;

import java.io.IOException;
import java.net.URL;

/**
 * MoviesUrlProvider
 *
 * Provide the URL to fetch the movies
 */
public class MovieDetailUrlProvider {

    protected String LOG_TAG = MovieDetailUrlProvider.class.getSimpleName();

    // Constants for the URL query
    final String MOVIES_BASE_URL =
            "https://api.themoviedb.org/3/movie";
    final String API_KEY_PARAM = "api_key";

    public URL getMovieDetailUrl(int id) {
        String apiKey = ApiKeyProvider.getApiKey();

        //Construct the URL for the API query
        Uri builtUri = Uri.parse(MOVIES_BASE_URL).buildUpon()
                .appendEncodedPath(Integer.toString(id))
                .appendQueryParameter(API_KEY_PARAM, apiKey)
                .build();

        try {
            return new URL(builtUri.toString());
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error ", e);
            return null;
        }
    }

    public URL getMovieVideosUrl(int id) {
        String apiKey = ApiKeyProvider.getApiKey();

        //Construct the URL for the API query
        Uri builtUri = Uri.parse(MOVIES_BASE_URL).buildUpon()
                .appendEncodedPath(Integer.toString(id))
                .appendEncodedPath("videos")
                .appendQueryParameter(API_KEY_PARAM, apiKey)
                .build();

        try {
            return new URL(builtUri.toString());
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error ", e);
            return null;
        }
    }

    public URL getMovieReviewsURL(int id) {
        String apiKey = ApiKeyProvider.getApiKey();

        //Construct the URL for the API query
        Uri builtUri = Uri.parse(MOVIES_BASE_URL).buildUpon()
                .appendEncodedPath(Integer.toString(id))
                .appendEncodedPath("reviews")
                .appendQueryParameter(API_KEY_PARAM, apiKey)
                .build();

        try {
            return new URL(builtUri.toString());
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error ", e);
            return null;
        }
    }
}
