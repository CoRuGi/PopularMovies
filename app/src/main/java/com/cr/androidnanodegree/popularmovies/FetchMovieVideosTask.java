package com.cr.androidnanodegree.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

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
public class FetchMovieVideosTask extends AsyncTask<Integer, Void, ArrayList<ArrayList>> {

    public static final int MOVIE_ID = 0;
    public static final int MOVIE_ISO = 1;
    public static final int MOVIE_KEY = 2;
    public static final int MOVIE_NAME = 3;
    public static final int MOVIE_SITE = 4;
    public static final int MOVIE_SIZE = 5;
    public static final int MOVIE_TYPE = 6;

    DetailActivityFragment parentActivity;

    public FetchMovieVideosTask(DetailActivityFragment parentActivity) {
        this.parentActivity = parentActivity;
    }

    protected final String LOG_TAG = FetchMovieVideosTask.class.getSimpleName();

    @Override
    protected ArrayList doInBackground(Integer... id) {

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

        URL url = new MovieDetailUrlProvider().getMovieVideosUrl(id[0]);

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
        ArrayList<ArrayList> videos;

        // These are the names of the JSON objects that need to be extracted.
        final String OWM_RESULTS = "results";
        final String OWM_ID = "id";
        final String OWM_ISO = "iso_639_1";
        final String OWM_KEY = "key";
        final String OWM_NAME = "name";
        final String OWM_SITE = "site";
        final String OWM_SIZE = "size";
        final String OWM_TYPE = "type";

        JSONObject moviesJson = new JSONObject(movieVideosJsonStr);
        JSONArray VideosArray = moviesJson.getJSONArray(OWM_RESULTS);

        videos = new ArrayList<>();

        for (int i = 0; i < VideosArray.length(); i++) {
            JSONObject video = VideosArray.getJSONObject(i);

            ArrayList<String> arraylist = new ArrayList<String>();
            arraylist.add(0, video.getString(OWM_ID));
            arraylist.add(1, video.getString(OWM_ISO));
            arraylist.add(2, video.getString(OWM_KEY));
            arraylist.add(3, video.getString(OWM_NAME));
            arraylist.add(4, video.getString(OWM_SITE));
            arraylist.add(5, video.getString(OWM_SIZE));
            arraylist.add(6, video.getString(OWM_TYPE));

            videos.add(arraylist);
        }

        return videos;
    }

    @Override
    protected void onPostExecute(ArrayList<ArrayList> arrayList) {
        LinearLayout linearLayout =
                (LinearLayout) parentActivity.getActivity().findViewById(R.id.detail_movie_videos);
        LayoutInflater layoutInflater = (LayoutInflater) parentActivity.getActivity()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (arrayList != null) {

            parentActivity.mTrailerUrl = "https://www.youtube.com/watch?v=" +
                    arrayList.get(0).get(MOVIE_KEY);
            if (parentActivity.mShareActionProvider != null) {
                parentActivity.mShareActionProvider.
                        setShareIntent(parentActivity.createShareTrailerIntent());
            }

            for (ArrayList list : arrayList) {
                View view = layoutInflater.inflate(
                        R.layout.list_item_videos, null
                );
                TextView textView = (TextView) view.findViewById(R.id.detail_movie_list_video_name);
                final String videoKey = (String) list.get(MOVIE_KEY);

                view.setOnClickListener(
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Uri youtubeLocation = Uri.parse("https://www.youtube.com/watch?v="
                                        + videoKey);
                                Intent intent = new Intent(Intent.ACTION_VIEW, youtubeLocation);
                                if (intent.resolveActivity(
                                        parentActivity.getActivity().getPackageManager()) != null) {
                                    parentActivity.startActivity(intent);
                                }
                            }
                        }
                );

                textView.setText((String) list.get(MOVIE_NAME));
                linearLayout.addView(view);
            }
        }
    }
}
