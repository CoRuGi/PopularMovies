package com.cr.androidnanodegree.popularmovies;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * FetchMoviesTask
 * <p/>
 * Fetch the movies from the themoviesdb.org website
 */
public class FetchMovieDetailTask extends AsyncTask<Integer, Void, Integer> {

    DetailActivityFragment parentActivity;

    public FetchMovieDetailTask(DetailActivityFragment parentActivity) {
        this.parentActivity = parentActivity;
    }

    protected final String LOG_TAG = FetchMovieDetailTask.class.getSimpleName();

    @Override
    protected Integer doInBackground(Integer... id) {

        String line;

        // These two need to be declared outside the try/catch
        // so that they can be closed in the finally block.
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        // Will contain the raw JSON response as a string.
        String movieDetailJsonStr = null;

        // If no params have been given the sort will be popular and the first page will be shown
        if (id.length == 0) {
            Log.e(LOG_TAG, "No request was given to FetchMoviesTask");
            return -1;
        }

        URL url = new MovieDetailUrlProvider().getMovieDetailUrl(id[0]);

        try {

            // Create the request to themoviesdb.org, and open the connection
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // Read the input stream into a String
            InputStream inputStream = urlConnection.getInputStream();
            StringBuilder builder = new StringBuilder();
            if (inputStream == null) {
                // Nothing to do.
                return -1;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            while ((line = reader.readLine()) != null) {
                // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                // But it does make debugging a *lot* easier if you print out the completed
                // buffer for debugging.
                builder.append(line + "\n");
            }

            if (builder.length() == 0) {
                // Stream was empty.  No point in parsing.
                return -1;
            }
            movieDetailJsonStr = builder.toString();

        } catch (IOException e) {
            Log.e(LOG_TAG, "Error ", e);
            // If the code didn't successfully get the movies data, there's no point in attempting
            // to parse it.
            return -1;
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(LOG_TAG, "Error closing stream", e);
                }
            }
        }

        try {
            return getMovieDetailDataFromJson(movieDetailJsonStr);
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }

        return -1;
    }

    private int getMovieDetailDataFromJson(String movieDetailJsonStr) throws JSONException {

        // These are the names of the JSON objects that need to be extracted.
        final String OWM_RUNTIME = "runtime";
        Bitmap bitmap;

        JSONObject movieDetailJson = new JSONObject(movieDetailJsonStr);
        return movieDetailJson.getInt(OWM_RUNTIME);
    }

    @Override
    protected void onPostExecute(Integer runtime) {
        TextView runtimeView =
                (TextView) parentActivity.getActivity().findViewById(R.id.detail_movie_runtime);
        runtimeView.setText(runtime);
    }
}
