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
public class MoviesUrlProvider {

    protected String LOG_TAG = MoviesUrlProvider.class.getSimpleName();

    // Constants for the URL query
    final String MOVIES_BASE_URL =
            "https://api.themoviedb.org/3/discover/movie?";
    final String SORT_BY_PARAM = "sort_by";
    final String PAGE_PARAM = "page";
    final String API_KEY_PARAM = "api_key";

    public URL getMoviesUrl(String apiKey, String sortBy, String page) {
        //Construct the URL for the API query
        Uri builtUri = Uri.parse(MOVIES_BASE_URL).buildUpon()
                .appendQueryParameter(API_KEY_PARAM, apiKey)
                .appendQueryParameter(SORT_BY_PARAM, sortBy)
                .appendQueryParameter(PAGE_PARAM, page)
                .build();

        try {
            return new URL(builtUri.toString());
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error ", e);
            return null;
        }
    }
}

