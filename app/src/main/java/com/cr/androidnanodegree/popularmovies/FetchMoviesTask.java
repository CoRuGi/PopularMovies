package com.cr.androidnanodegree.popularmovies;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;

import com.bumptech.glide.Glide;

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
import java.util.concurrent.ExecutionException;

/**
 * FetchMoviesTask
 * <p/>
 * Fetch the movies from the themoviesdb.org website
 */
public class FetchMoviesTask extends AsyncTask<FetchMovieTaskRequest, Void, ArrayList<MovieInformation>> {

    MainActivityFragment parentActivity;

    public FetchMoviesTask(MainActivityFragment parentActivity) {
        this.parentActivity = parentActivity;
    }

    protected final String LOG_TAG = FetchMoviesTask.class.getSimpleName();

    @Override
    protected ArrayList<MovieInformation> doInBackground(FetchMovieTaskRequest... requests) {

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

    private ArrayList<MovieInformation> getMoviesDataFromJson(String moviesJsonStr) throws JSONException {
        ArrayList<MovieInformation> movies;

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

        movies = new ArrayList<>();

        for (int i = 0; i < moviesArray.length(); i++) {
            JSONObject movie = moviesArray.getJSONObject(i);

            MovieInformation movieInformation = new MovieInformation();
            movieInformation.setId(movie.getString(OWM_ID));
            movieInformation.setTitle(movie.getString(OWM_TITLE));
            movieInformation.setPosterPath(movie.getString(OWM_POSTER_PATH));
            movieInformation.setSynopsis(movie.getString(OWM_SYNOPSIS));
            movieInformation.setReleaseDate(movie.getString(OWM_RELEASE_DATE));
            movieInformation.setVoteAverage(movie.getString(OWM_VOTE_AVERAGE));
            movieInformation.setBackdropPath(movie.getString(OWM_BACKDROP_PATH));

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

            movieInformation.setPoster(bitmap);

            movies.add(movieInformation);
        }

        return movies;
    }

    @Override
    protected void onPostExecute(ArrayList<MovieInformation> movieInformations) {
        if (movieInformations != null) {
            parentActivity.clearMovieInformationAdapter();
            for (MovieInformation movie : movieInformations) {
                parentActivity.addToMovieInformationAdapter(movie);
            }
        }
        parentActivity.progressDialog.dismiss();
    }
}
