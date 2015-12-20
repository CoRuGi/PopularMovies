package com.cr.androidnanodegree.popularmovies;

import android.app.ProgressDialog;
import android.content.Context;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.Toast;

import com.cr.androidnanodegree.popularmovies.data.MovieContract;

/**
 * Fragment to show the poster grid view
 */
public class MainActivityFragment extends Fragment
        implements LoaderManager.LoaderCallbacks<Cursor> {

    protected static final String LOG_TAG = MainActivityFragment.class.getSimpleName();
    protected static final int MOVIES_LOADER = 0;
    protected static final String SELECTED_KEY = "storedPosition";

    public MovieInformationAdapter mMovieInformationAdapter;
    public MovieInformationCursorAdapter mMovieInformationCursorAdapter;
    public ProgressDialog mProgressDialog;
    protected String mStoredSortByPreference;
    protected Boolean mStoredExtraInformationPreference;
    protected Callback mActivity;
    protected int mPosition;
    protected GridView mGridView;

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

        public void setStoredSortByPreference(String sortByPreference);

        public String getStoredSortByPreference();
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

        if (mActivity.getStoredSortByPreference() != null) {
            mStoredSortByPreference = mActivity.getStoredSortByPreference();
        } else {
            mStoredSortByPreference = Utility.getSortByPreference(getContext());
//            mActivity.setStoredSortByPreference(Utility.getSortByPreference(getContext()));
        }
        mStoredExtraInformationPreference = Utility.getExtraInformationPreference(getContext());

        mMovieInformationCursorAdapter = new MovieInformationCursorAdapter(
                getContext(), null, 0
        );

        // Fill the GridView with the data
        mGridView = (GridView) rootView.findViewById(
                R.id.gridview_movies
        );
        mGridView.setAdapter(mMovieInformationCursorAdapter);

        mGridView.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Cursor cursor = (Cursor) parent.getItemAtPosition(position);
                        if (cursor != null) {
                            long movieId = cursor.getLong(COL_MOVIE_ID);
                            Uri uri;
                            if (Utility.getSortByPreference(getContext()).equals(
                                    getString(R.string.pref_sortby_favorites))) {
                                uri =
                                        MovieContract.FavoritesEntry.buildFavoritesUri(movieId);
                            } else {
                                uri =
                                        MovieContract.LastRequestedEntry.buildFavoritesUri(movieId);
                            }
                            mActivity.OnItemSelected(uri);
                        }
                        mPosition = position;
                    }
                }
        );

        if (null != savedInstanceState && savedInstanceState.containsKey(SELECTED_KEY)) {
            mPosition = savedInstanceState.getInt(SELECTED_KEY);
        }

        return rootView;
    }

    void onSortByChanged() {
        String sortByPreference = Utility.getSortByPreference(getContext());
        if (!sortByPreference.equals(getString(R.string.pref_sortby_favorites))) {
            if (!isNetworkAvailable()) {
                Log.d(LOG_TAG, "No network connection.");
                String msg = "Network connection is needed to get the Latest Movies";
                Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
                return;
            }
            updateMoviesGrid();
        }
        getLoaderManager().restartLoader(MOVIES_LOADER, null, this);
    }

    public void updateMoviesGrid() {
        // Get the API key from ApiKeyProvider
        String apiKey = ApiKeyProvider.getApiKey();

        // Prepare the fetch request
        // Create the FetchMovieTaskRequest with the given APIKey
        FetchMovieTaskRequest request = new FetchMovieTaskRequest(apiKey);

        // Set the page, option to make it variable in future releases
        request.setPage(1);

        // Get the sort preference from the settings
        String sortByPreference = Utility.getSortByPreference(getContext());
        request.setSortBy(sortByPreference);

        // Fetch the movie information from themoviedb.org
        FetchMoviesTask moviesTask = new FetchMoviesTask(this);
        moviesTask.execute(request);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(MOVIES_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (mPosition != GridView.INVALID_POSITION) {
            outState.putInt(SELECTED_KEY, mPosition);
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String sortByPreference = Utility.getSortByPreference(getContext());
        if (sortByPreference.equals(getString(R.string.pref_sortby_favorites))) {
            Log.d(LOG_TAG, "Favorites will be requested from the database.");
            Uri uri = MovieContract.FavoritesEntry.CONTENT_URI;

            return new CursorLoader(getContext(),
                    uri,
                    MOVIES_PROJECTION,
                    null, null, null
            );
        } else {
            Log.d(LOG_TAG, "Last requested will be requested from the database.");
            Uri uri = MovieContract.LastRequestedEntry.CONTENT_URI;

            return new CursorLoader(
                    getContext(),
                    uri,
                    MOVIES_PROJECTION,
                    null, null, null
            );
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        String sortByPreference = Utility.getSortByPreference(getContext());
        if (data.moveToFirst()) {
            Log.d(LOG_TAG, "Retrieved data from the database.");
            mMovieInformationCursorAdapter.swapCursor(data);
            if (mPosition != ListView.INVALID_POSITION) {
                mGridView.smoothScrollToPosition(mPosition);
            }
        } else {
            Log.d(LOG_TAG, "No data was available in the database.");
            Log.d(LOG_TAG, "sortByPreference is: " + sortByPreference);

            if (sortByPreference.equals(getString(R.string.pref_sortby_favorites))) {
                Log.d(LOG_TAG, "No favorites stored.");
                String msg = "No favorites are saved.";
                Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
                mMovieInformationCursorAdapter.swapCursor(null);
                return;
            }

            if (!isNetworkAvailable()) {
                Log.d(LOG_TAG, "No network connection.");
                String msg = "Network connection is needed to get the Latest movies";
                Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
                return;
            }

            mProgressDialog = new ProgressDialog(getContext());
            mProgressDialog.setMessage("Loading movies...");
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            mProgressDialog.setProgress(0);
            mProgressDialog.setMax(100);
            mProgressDialog.show();
            updateMoviesGrid();
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mMovieInformationCursorAdapter.swapCursor(null);
    }

    protected boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}