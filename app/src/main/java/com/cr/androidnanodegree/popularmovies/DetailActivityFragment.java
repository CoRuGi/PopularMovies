package com.cr.androidnanodegree.popularmovies;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.cr.androidnanodegree.popularmovies.data.MovieContract.FavoritesEntry;

import java.io.ByteArrayOutputStream;
import java.net.URL;
import java.util.ArrayList;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailActivityFragment extends Fragment
        implements LoaderManager.LoaderCallbacks<Cursor> {

    private final static String LOG_TAG = DetailActivityFragment.class.getSimpleName();
    protected static final int DETAIL_LOADER = 0;

    public static final String MOVIE_INFORMATION_EXTRA = "MovieInformation";
    public static final String DETAIL_URI = "detail_uri";

    protected MovieInformation movieInformation;
    protected Uri mUri;
    protected MovieVideosAdapter mMovieVideosAdapter;
    protected MovieReviewsAdapter mMovieReviewsAdapter;

    protected ImageView posterView;
    protected ImageView backgroundView;
    protected TextView titleView;
    protected TextView yearView;
    protected TextView averageView;
    protected TextView synopsisView;
    protected LinearLayout videosView;
    protected LinearLayout reviewsView;

    public DetailActivityFragment() {
    }

    public static DetailActivityFragment newInstance(Uri favoriteUri) {
        DetailActivityFragment detailActivityFragment = new DetailActivityFragment();

        Bundle args = new Bundle();
        args.putParcelable(DETAIL_URI, favoriteUri);
        detailActivityFragment.setArguments(args);

        return detailActivityFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

        // To support the ArrayAdapter from Part 1 we need to check if we received an URI
        // else we will get our information from the ArrayAdapter structure
        Bundle arguments = getArguments();
        if (arguments != null) {
            mUri = arguments.getParcelable(DETAIL_URI);
        }

        titleView = (TextView) rootView.findViewById(R.id.detail_movie_title);
        yearView = (TextView) rootView.findViewById(R.id.detail_movie_year);
        averageView = (TextView) rootView.findViewById(R.id.detail_movie_average_rating);
        synopsisView = (TextView) rootView.findViewById(R.id.detail_movie_synopsis);
        posterView = (ImageView) rootView.findViewById(R.id.detail_movie_poster);
        backgroundView = (ImageView) rootView.findViewById(R.id.detail_movie_background);
        videosView = (LinearLayout) rootView.findViewById(R.id.detail_movie_videos);
        reviewsView = (LinearLayout) rootView.findViewById(R.id.detail_movie_reviews);

        Button button = (Button) rootView.findViewById(R.id.fragment_detail_button_favorite);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                onMarkAsFavoriteClick(v);
            }
        });

        Intent intent = getActivity().getIntent();
        if (intent != null && intent.hasExtra(MOVIE_INFORMATION_EXTRA)) {
            movieInformation = intent.getParcelableExtra(MOVIE_INFORMATION_EXTRA);
        }

        if (movieInformation != null) {
            FetchMovieDetailTask movieDetailTask = new FetchMovieDetailTask(this);
            movieDetailTask.execute(Integer.parseInt(movieInformation.getId()));

            FetchMovieVideosTask movieVideosTask = new FetchMovieVideosTask(this);
            movieVideosTask.execute(Integer.parseInt(movieInformation.getId()));

            FetchMovieReviewsTask movieReviewsTask = new FetchMovieReviewsTask(this);
            movieReviewsTask.execute(Integer.parseInt(movieInformation.getId()));

            titleView.setText(movieInformation.getTitle());
            yearView.setText(movieInformation.getYearFromReleaseDate());
            averageView.setText(movieInformation.getVoteAverage());
            synopsisView.setText(movieInformation.getSynopsis());

            //URL posterUrl = new ImageUrlProvider().getImageUrl(movieInformation.getPosterPath());
            URL backgroundUrl = new ImageUrlProvider("w780").getImageUrl(movieInformation.getBackdropPath());

            // TODO Remove if getPoster works better
            /*
            Glide.with(getContext())
                    .load(posterUrl.toString())
                    .override(550, 825)
                    .into(posterView);
            */
            posterView.setImageBitmap(movieInformation.getPoster());

            Glide.with(getContext())
                    .load(backgroundUrl.toString())
                    .into(backgroundView);

        }

        if (Build.VERSION.SDK_INT <= 15) {
            backgroundView.setAlpha(50);
        } else {
            backgroundView.setImageAlpha(50);
        }

        mMovieVideosAdapter = new MovieVideosAdapter(
                getContext(), R.layout.list_item_videos, new ArrayList<ArrayList>()
        );

        // TODO Remove if listView is successful
        /*
        videosView.setAdapter(mMovieVideosAdapter);
        videosView.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        ArrayList arrayList = mMovieVideosAdapter.getItem(position);
                        String key = arrayList.get(FetchMovieVideosTask.MOVIE_KEY).toString();
                        Uri youtubeLocation = Uri.parse("https://www.youtube.com/watch?v=" + key);
                        Intent intent = new Intent(Intent.ACTION_VIEW, youtubeLocation);
                        if (intent.resolveActivity(getContext().getPackageManager()) != null) {
                            startActivity(intent);
                        }
                    }
                }
        );
        */

        mMovieReviewsAdapter = new MovieReviewsAdapter(
                getContext(), R.layout.list_item_reviews, new ArrayList<ArrayList>()
        );

        // TODO Remove if listView is successful
        /*
        reviewsView.setAdapter(mMovieReviewsAdapter);
        reviewsView.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        ArrayList arrayList = mMovieReviewsAdapter.getItem(position);
                        String url = arrayList.get(FetchMovieReviewsTask.REVIEW_URL).toString();
                        Uri reviewLocation = Uri.parse(url);
                        Intent intent = new Intent(Intent.ACTION_VIEW, reviewLocation);
                        if (intent.resolveActivity(getContext().getPackageManager()) != null) {
                            startActivity(intent);
                        }
                    }
                }
        );
        */

        return rootView;
    }

    public void onMarkAsFavoriteClick(View view) {
        Log.d(LOG_TAG, "Button pressed!");
        Button button = (Button) view.findViewById(R.id.fragment_detail_button_favorite);

        // Check if the movie is already in the database
        Cursor cursor = getContext().getContentResolver().query(
                FavoritesEntry.CONTENT_URI,
                new String[]{FavoritesEntry.COLUMN_MOVIE_ID, FavoritesEntry.COLUMN_MOVIE_ID},
                FavoritesEntry.COLUMN_MOVIE_ID + " = ?",
                new String[]{movieInformation.getId()},
                null
        );

        // If movie is already in the database we don't have to add it
        if (cursor.moveToFirst()) {
            setButtonMarked(button);
            return;
        }

        // Prepare the values to be entered into the database
        ContentValues values = new ContentValues();

        values.put(FavoritesEntry.COLUMN_MOVIE_ID, movieInformation.getId());
        values.put(FavoritesEntry.COLUMN_MOVIE_TITLE, movieInformation.getTitle());
        values.put(FavoritesEntry.COLUMN_MOVIE_SYNOPSIS, movieInformation.getSynopsis());
        values.put(FavoritesEntry.COLUMN_VOTE_AVERAGE, movieInformation.getVoteAverage());
        values.put(FavoritesEntry.COLUMN_RELEASE_DATE, movieInformation.getReleaseDate());
        values.put(FavoritesEntry.COLUMN_BACKDROP_PATH, movieInformation.getBackdropPath());

        Bitmap poster = movieInformation.getPoster();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        poster.compress(Bitmap.CompressFormat.PNG, 0, stream);
        values.put(FavoritesEntry.COLUMN_POSTER, stream.toByteArray());

        getContext().getContentResolver().insert(FavoritesEntry.CONTENT_URI, values);

        setButtonMarked(button);

        cursor.close();
    }

    public void setButtonMarked(Button button) {
        button.setText(R.string.fragment_detail_button_text_marked);
        button.setClickable(false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        if (mUri != null) {
            getLoaderManager().initLoader(DETAIL_LOADER, null, this);
        }
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(
                getActivity(),
                mUri,
                MainActivityFragment.MOVIES_PROJECTION,
                null, null, null
        );
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data != null && data.moveToFirst()) {
            // Read poster from cursor
            byte[] blob = data.getBlob(MainActivityFragment.COL_POSTER);
            Bitmap bitmap = BitmapFactory.decodeByteArray(blob, 0, blob.length);
            posterView.setImageBitmap(bitmap);

            // Read year from cursor
            String releaseDate = data.getString(MainActivityFragment.COL_RELEASE_DATE);
            String movieYear = Utility.getYearFromReleaseDate(releaseDate);
            yearView.setText(movieYear);

            // Read rating from cursor
            averageView.setText(data.getString(MainActivityFragment.COL_VOTE_AVERAGE));

            // Read title from cursor
            titleView.setText(data.getString(MainActivityFragment.COL_MOVIE_TITLE));

            // Read average from cursor
            averageView.setText(data.getString(MainActivityFragment.COL_VOTE_AVERAGE));

            // Read synopsis from cursor
            synopsisView.setText(data.getString(MainActivityFragment.COL_MOVIE_SYNOPSIS));

            // Put the values into the movieInformation for the button
            movieInformation = new MovieInformation();
            movieInformation.setId(data.getString((MainActivityFragment.COL_MOVIE_ID)));
            movieInformation.setTitle(data.getString(MainActivityFragment.COL_MOVIE_TITLE));
            movieInformation.setPoster(bitmap);
            movieInformation.setSynopsis(data.getString(MainActivityFragment.COL_MOVIE_SYNOPSIS));
            movieInformation.setVoteAverage(data.getString(MainActivityFragment.COL_VOTE_AVERAGE));
            movieInformation.setReleaseDate(data.getString(MainActivityFragment.COL_RELEASE_DATE));
            movieInformation.setBackdropPath(
                    data.getString(MainActivityFragment.COL_BACKDROP_PATH)
            );

            //URL posterUrl = new ImageUrlProvider().getImageUrl(movieInformation.getPosterPath());
            URL backgroundUrl = new ImageUrlProvider("w780").getImageUrl(
                    data.getString(MainActivityFragment.COL_BACKDROP_PATH)
            );

            Glide.with(getContext())
                    .load(backgroundUrl.toString())
                    .into(backgroundView);

            FetchMovieDetailTask movieDetailTask = new FetchMovieDetailTask(this);
            movieDetailTask.execute(Integer.parseInt(
                            data.getString(MainActivityFragment.COL_MOVIE_ID))
            );

            FetchMovieVideosTask movieVideosTask = new FetchMovieVideosTask(this);
            movieVideosTask.execute(
                    Integer.parseInt(data.getString(MainActivityFragment.COL_MOVIE_ID))
            );

            FetchMovieReviewsTask movieReviewsTask = new FetchMovieReviewsTask(this);
            movieReviewsTask.execute(
                    Integer.parseInt(data.getString(MainActivityFragment.COL_MOVIE_ID))
            );
        }
    }

    public void addToMovieVideosAdapter(ArrayList arraylist) {
        Log.d(LOG_TAG, "New Trailer will be added!");
        mMovieVideosAdapter.add(arraylist);
    }

    public void clearMovieVideosAdapter() {
        mMovieVideosAdapter.clear();
    }

    public void addToMovieReviewsAdapter(ArrayList arraylist) {
        Log.d(LOG_TAG, "New Review will be added!");
        mMovieReviewsAdapter.add(arraylist);
    }

    public void clearMovieReviewsAdapter() {
        mMovieReviewsAdapter.clear();
    }
}
