package com.cr.androidnanodegree.popularmovies;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import java.util.ArrayList;

/**
 * Fragment to show the poster grid view
 */
public class MainActivityFragment extends Fragment {

    public MovieInformationAdapter mMovieInformationAdapter;
    public ProgressDialog mProgressDialog;
    protected String mStoredSortByPreference;
    protected Boolean mStoredExtraInformationPreference;

    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        // Store the settings to know if they changed during our lifecycle
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        mStoredSortByPreference = sharedPreferences.getString(
                getString(R.string.pref_sortby_key),
                getString(R.string.pref_sortby_most_popular)
        );
        mStoredExtraInformationPreference = sharedPreferences.getBoolean(
                getString(R.string.pref_extrainformation_key),
                true
        );

        mMovieInformationAdapter = new MovieInformationAdapter(
                getContext(), R.layout.grid_item_movie, new ArrayList<MovieInformation>()
        );

        // Fill the GridView with the data
        GridView gridView = (GridView) rootView.findViewById(
                R.id.gridview_movies
        );
        gridView.setAdapter(mMovieInformationAdapter);

        // When a movie is selected start the DetailActivity
        gridView.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        MovieInformation movieInformation = mMovieInformationAdapter.getItem(position);
                        Intent intent = new Intent(getContext(), DetailActivity.class)
                                .putExtra("MovieInformation", movieInformation);
                        startActivity(intent);
                    }
                }
        );

        return rootView;
    }

    public void updateMoviesGrid() {
        mProgressDialog = new ProgressDialog(getContext());
        mProgressDialog.setMessage("Loading movies...");
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.setProgress(0);
        mProgressDialog.setMax(100);
        mProgressDialog.show();

        // Get the API key from ApiKeyProvider
        String apiKey = ApiKeyProvider.getApiKey();

        // Prepare the fetch request
        // Create the FetchMovieTaskRequest with the given APIKey
        FetchMovieTaskRequest request = new FetchMovieTaskRequest(apiKey);

        // Set the page, option to make it variable in future releases
        request.setPage(1);

        // Get the sort preference from the settings
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        String sortByPreference = sharedPreferences.getString(
                getString(R.string.pref_sortby_key),
                getString(R.string.pref_sortby_most_popular)
        );
        request.setSortBy(sortByPreference);

        // Fetch the movie information from themoviedb.org
        FetchMoviesTask moviesTask = new FetchMoviesTask(this);
        moviesTask.execute(request);
    }

    @Override
    public void onStart() {
        super.onStart();

        // Get the preferences to see if they have changed
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        String sortByPreference = sharedPreferences.getString(
                getString(R.string.pref_sortby_key),
                getString(R.string.pref_sortby_most_popular)
        );
        Boolean extraInformationPreference = sharedPreferences.getBoolean(
                getString(R.string.pref_extrainformation_key),
                true
        );

        // If the mMovieInformationAdapter is already filled we don't need to update it
        if (mMovieInformationAdapter.getCount() <= 2) {
            updateMoviesGrid();
        }

        // If one of the settings has changed we need to call updateMoviesGrid
        if (!sortByPreference.equals(mStoredSortByPreference) ||
                !mStoredExtraInformationPreference.equals(extraInformationPreference)
                ) {
            updateMoviesGrid();
        }
    }

    public void addToMovieInformationAdapter(MovieInformation movieInformation) {
        mMovieInformationAdapter.add(movieInformation);
    }

    public void clearMovieInformationAdapter() {
        mMovieInformationAdapter.clear();
    }
}
