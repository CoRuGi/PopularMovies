package com.cr.androidnanodegree.popularmovies;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.cr.androidnanodegree.popularmovies.data.MovieContract;

import java.util.ArrayList;

/**
 * Fragment to show the poster grid view
 */
public class MainActivityFragment extends Fragment
        implements LoaderManager.LoaderCallbacks<Cursor> {

    protected static final int MOVIES_LOADER = 0;
    public MovieInformationAdapter mMovieInformationAdapter;
    public MovieInformationCursorAdapter mMovieInformationCursorAdapter;
    public ProgressDialog mProgressDialog;
    protected String mStoredSortByPreference;
    protected Boolean mStoredExtraInformationPreference;
    protected Callback mActivity;

    protected final static String[] MOVIES_PROJECTION = {
            MovieContract.FavoritesEntry._ID,
            MovieContract.FavoritesEntry.COLUMN_MOVIE_ID,
            MovieContract.FavoritesEntry.COLUMN_MOVIE_TITLE,
            MovieContract.FavoritesEntry.COLUMN_POSTER,
            MovieContract.FavoritesEntry.COLUMN_MOVIE_SYNOPSIS,
            MovieContract.FavoritesEntry.COLUMN_VOTE_AVERAGE,
            MovieContract.FavoritesEntry.COLUMN_RELEASE_DATE,
            MovieContract.FavoritesEntry.COLUMN_BACKDROP_PATH
    };

    static final int COL_ID = 0;
    static final int COL_MOVIE_ID = 1;
    static final int COL_MOVIE_TITLE = 2;
    static final int COL_POSTER = 3;
    static final int COL_MOVIE_SYNOPSIS = 4;
    static final int COL_VOTE_AVERAGE = 5;
    static final int COL_RELEASE_DATE = 6;
    static final int COL_BACKDROP_PATH = 7;

    public interface Callback {
        /**
         * Callback for when an item has been selected.
         */
        public void OnItemSelected(Uri favoritesUri);
        public void OnItemSelected(MovieInformation movieInformation);
    }

    public MainActivityFragment() {
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = (Callback) getActivity();
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
        mMovieInformationCursorAdapter = new MovieInformationCursorAdapter(
                getContext(), null, 0
        );

        // Fill the GridView with the data
        GridView gridView = (GridView) rootView.findViewById(
                R.id.gridview_movies
        );

        // When SortBy is set to favorites we need to set the ArrayAdaptor or the cursor adapter
        if (!mStoredSortByPreference.equals(getString(R.string.pref_sortby_favorites))) {
            gridView.setAdapter(mMovieInformationAdapter);

            // When a movie is selected start the DetailActivity
            gridView.setOnItemClickListener(
                    new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            MovieInformation movieInformation =
                                    mMovieInformationAdapter.getItem(position);
                            mActivity.OnItemSelected(movieInformation);
//                            TODO Remove if two pane fragments works
//                            Intent intent = new Intent(getContext(), DetailActivity.class)
//                                    .putExtra(
//                                            DetailActivityFragment.MOVIE_INFORMATION_EXTRA,
//                                            movieInformation
//                                    );
//                            startActivity(intent);
                        }
                    }
            );
        } else {
            gridView.setAdapter(mMovieInformationCursorAdapter);

            gridView.setOnItemClickListener(
                    new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            Cursor cursor = (Cursor) parent.getItemAtPosition(position);
                            if (cursor != null) {
                                long movieId = cursor.getLong(COL_MOVIE_ID);
                                Uri favoritesUri =
                                        MovieContract.FavoritesEntry.buildFavoritesUri(movieId);
                                mActivity.OnItemSelected(favoritesUri);
//                                TODO Remove if two pane fragments works
//                                Intent intent = new Intent(getActivity(), DetailActivity.class)
//                                        .setData(favoritesUri);
//                                startActivity(intent);
                            }
                        }
                    }
            );
        }

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

        // If the SortBy Favorites Setting is selected we need to use the CursorLoader
        if (sortByPreference.equals("favorites")) {
            getLoaderManager().initLoader(MOVIES_LOADER, null, this);
            return;
        }

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

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri uri = MovieContract.FavoritesEntry.CONTENT_URI;

        return new CursorLoader(getContext(),
                uri,
                MOVIES_PROJECTION,
                null, null, null
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mMovieInformationCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}