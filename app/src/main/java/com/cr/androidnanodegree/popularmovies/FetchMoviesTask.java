package com.cr.androidnanodegree.popularmovies;

import android.content.ContentValues;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;

import com.bumptech.glide.Glide;
import com.cr.androidnanodegree.popularmovies.data.MovieContract.LastRequestedEntry;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Vector;
import java.util.concurrent.ExecutionException;

/**
 * FetchMoviesTask
 * <p/>
 * Fetch the movies from the themoviesdb.org website
 */
public class FetchMoviesTask extends AsyncTask<FetchMovieTaskRequest, Void, Vector<ContentValues>> {

    MainActivityFragment parentActivity;

    public FetchMoviesTask(MainActivityFragment parentActivity) {
        this.parentActivity = parentActivity;
    }

    protected final String LOG_TAG = FetchMoviesTask.class.getSimpleName();

    @Override
    protected Vector<ContentValues> doInBackground(FetchMovieTaskRequest... requests) {

        String line;

        // These two need to be declared outside the try/catch
        // so that they can be closed in the finally block.
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        // Will contain the raw JSON response as a string.
        String moviesJsonStr = null;

        // If no params have been given the sort will be popular and the first page will be shown
        if (requests.length == 0) {
            Log.e(LOG_TAG, "No request was given to FetchMoviesTask");
            return null;
        }

        URL url = new MoviesUrlProvider().getMoviesUrl(
                requests[0].getApiKey(),
                requests[0].getSortBy(),
                "" + requests[0].getPage()
        );

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
                return null;
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
                return null;
            }
            moviesJsonStr = builder.toString();

        } catch (IOException e) {
            Log.e(LOG_TAG, "Error ", e);
            // If the code didn't successfully get the movies data, there's no point in attempting
            // to parse it.
            return null;
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
            return getMoviesDataFromJson(moviesJsonStr);
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }

        return null;
    }

    private Vector<ContentValues> getMoviesDataFromJson(String moviesJsonStr) throws JSONException {

        // These are the names of the JSON objects that need to be extracted.
        final String OWM_RESULTS = "results";
        final String OWM_ID = "id";
        final String OWM_TITLE = "original_title";
        final String OWM_POSTER_PATH = "poster_path";
        final String OWM_SYNOPSIS = "overview";
        final String OWM_RELEASE_DATE = "release_date";
        final String OWM_VOTE_AVERAGE = "vote_average";
        final String OWM_BACKDROP_PATH = "backdrop_path";
        Bitmap bitmap;

        JSONObject moviesJson = new JSONObject(moviesJsonStr);
        JSONArray moviesArray = moviesJson.getJSONArray(OWM_RESULTS);

        Vector<ContentValues> contentValuesVector = new Vector<ContentValues>(moviesArray.length());

        for (int i = 0; i < moviesArray.length(); i++) {
            JSONObject movie = moviesArray.getJSONObject(i);

            String movieId      = movie.getString(OWM_ID);
            String title        = movie.getString(OWM_TITLE);
            String synopsis     = movie.getString(OWM_SYNOPSIS);
            String releaseDate  = movie.getString(OWM_RELEASE_DATE);
            String voteAverage  = movie.getString(OWM_VOTE_AVERAGE);
            String backdropPath = movie.getString(OWM_BACKDROP_PATH);

            URL posterUrl = new ImageUrlProvider().getImageUrl(movie.getString(OWM_POSTER_PATH));

            try {
                bitmap = Glide.with(parentActivity.getContext())
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

            ContentValues movieValues = new ContentValues();

            movieValues.put(LastRequestedEntry.COLUMN_MOVIE_ID, movieId);
            movieValues.put(LastRequestedEntry.COLUMN_MOVIE_TITLE, title);
            movieValues.put(LastRequestedEntry.COLUMN_MOVIE_SYNOPSIS, synopsis);
            movieValues.put(LastRequestedEntry.COLUMN_VOTE_AVERAGE, voteAverage);
            movieValues.put(LastRequestedEntry.COLUMN_RELEASE_DATE, releaseDate);
            movieValues.put(LastRequestedEntry.COLUMN_BACKDROP_PATH, backdropPath);

            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            if (bitmap != null) {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 50, stream);
            }
            movieValues.put(LastRequestedEntry.COLUMN_POSTER, stream.toByteArray());

            contentValuesVector.add(movieValues);
        }

        return contentValuesVector;
    }

    @Override
    protected void onPostExecute(Vector<ContentValues> movieInformations) {
        int inserted = 0;
        if (movieInformations != null) {

            parentActivity.getContext().getContentResolver().delete(
                    LastRequestedEntry.CONTENT_URI, null, null
            );

            ContentValues[] contentValuesArray = new ContentValues[movieInformations.size()];
            movieInformations.toArray(contentValuesArray);
            inserted = parentActivity.getContext().getContentResolver().bulkInsert(
                    LastRequestedEntry.CONTENT_URI,
                    contentValuesArray
            );
        }
        if (parentActivity.mProgressDialog != null) {
            parentActivity.mProgressDialog.dismiss();
        }

        Log.d(LOG_TAG, "FetchMovieTask Complete. " + inserted + " Inserted");
    }
}
