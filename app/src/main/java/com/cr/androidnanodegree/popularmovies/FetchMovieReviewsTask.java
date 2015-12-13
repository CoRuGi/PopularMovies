package com.cr.androidnanodegree.popularmovies;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * FetchMoviesTask
 * <p/>
 * Fetch the movies from the themoviesdb.org website
 */
public class FetchMovieReviewsTask extends AsyncTask<Integer, Void, ArrayList<ArrayList>> {

    public static final int REVIEW_ID = 0;
    public static final int REVIEW_AUTHOR = 1;
    public static final int REVIEW_CONTENT = 2;
    public static final int REVIEW_URL = 3;

    DetailActivityFragment parentActivity;

    public FetchMovieReviewsTask(DetailActivityFragment parentActivity) {
        this.parentActivity = parentActivity;
    }

    protected final String LOG_TAG = FetchMovieReviewsTask.class.getSimpleName();

    @Override
    protected ArrayList<ArrayList> doInBackground(Integer... id) {

        String line;

        // These two need to be declared outside the try/catch
        // so that they can be closed in the finally block.
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        // Will contain the raw JSON response as a string.
        String movieVideosJsonStr = null;

        // If no params have been given the sort will be popular and the first page will be shown
        if (id.length == 0) {
            Log.e(LOG_TAG, "No request was given to FetchMovieVideosTask");
            return null;
        }

        URL url = new MovieDetailUrlProvider().getMovieReviewsURL(id[0]);

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
            movieVideosJsonStr = builder.toString();

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
            return getMovieVideosDataFromJson(movieVideosJsonStr);
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }

        return null;
    }

    private ArrayList<ArrayList> getMovieVideosDataFromJson(String movieVideosJsonStr) throws JSONException {
        ArrayList<ArrayList> reviews;

        // These are the names of the JSON objects that need to be extracted.
        final String OWM_RESULTS = "results";
        final String OWM_ID = "id";
        final String OWM_AUTHOR = "author";
        final String OWM_CONTENT = "content";
        final String OWM_URL = "url";

        JSONObject moviesJson = new JSONObject(movieVideosJsonStr);
        JSONArray VideosArray = moviesJson.getJSONArray(OWM_RESULTS);

        reviews = new ArrayList<>();

        for (int i = 0; i < VideosArray.length(); i++) {
            JSONObject video = VideosArray.getJSONObject(i);

            ArrayList<String> arraylist = new ArrayList<String>();
            arraylist.add(REVIEW_ID, video.getString(OWM_ID));
            arraylist.add(REVIEW_AUTHOR, video.getString(OWM_AUTHOR));
            arraylist.add(REVIEW_CONTENT, video.getString(OWM_CONTENT));
            arraylist.add(REVIEW_URL, video.getString(OWM_URL));

            reviews.add(arraylist);
        }

        return reviews;
    }

    @Override
    protected void onPostExecute(ArrayList<ArrayList> arrayList) {
        if (arrayList != null) {
            parentActivity.clearMovieReviewsAdapter();
            for (ArrayList list : arrayList) {
                Log.d(LOG_TAG, "New review list will be send!");
                parentActivity.addToMovieReviewsAdapter(list);
            }
        }
    }
}
