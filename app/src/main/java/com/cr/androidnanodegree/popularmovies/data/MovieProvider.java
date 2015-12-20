package com.cr.androidnanodegree.popularmovies.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Handles the content queries.
 */
public class MovieProvider extends ContentProvider {

    // The URI Matcher used by this content provider.
    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private MovieDbHelper mOpenHelper;

    static final int FAVORITES = 100;
    static final int FAVORITES_ID = 101;
    static final int LAST_REQUESTED = 200;
    static final int LAST_REQUESTED_ID = 201;

    static UriMatcher buildUriMatcher() {
        final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = MovieContract.CONTENT_AUTHORITY;

        uriMatcher.addURI(authority, MovieContract.PATH_FAVORITES, FAVORITES);
        uriMatcher.addURI(authority, MovieContract.PATH_FAVORITES + "/#", FAVORITES_ID);
        uriMatcher.addURI(authority, MovieContract.PATH_LAST_REQUESTED, LAST_REQUESTED);
        uriMatcher.addURI(authority, MovieContract.PATH_LAST_REQUESTED + "/#", LAST_REQUESTED_ID);

        return uriMatcher;
    }

    @Override
    public boolean onCreate() {
        mOpenHelper = new MovieDbHelper(getContext());
        return true;
    }

    @Override
    public String getType(@Nullable Uri uri) {
        // Use the Uri Matcher to determine what kind of URI this is.
        final int match = sUriMatcher.match(uri);

        switch (match) {
            case FAVORITES:
                return MovieContract.FavoritesEntry.CONTENT_TYPE;
            case FAVORITES_ID:
                return MovieContract.FavoritesEntry.CONTENT_ITEM_TYPE;
            case LAST_REQUESTED:
                return MovieContract.LastRequestedEntry.CONTENT_TYPE;
            case LAST_REQUESTED_ID:
                return MovieContract.LastRequestedEntry.CONTENT_ITEM_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public Cursor query(@Nullable Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor returnCursor;
        final int match = sUriMatcher.match(uri);

        switch (match) {
            case FAVORITES: {
                returnCursor = mOpenHelper.getReadableDatabase().query(
                        MovieContract.FavoritesEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null, null,
                        sortOrder
                );
                break;
            }
            case FAVORITES_ID: {
                long id = MovieContract.FavoritesEntry.getFavoritesIdFromUri(uri);
                returnCursor = mOpenHelper.getReadableDatabase().query(
                        MovieContract.FavoritesEntry.TABLE_NAME,
                        projection,
                        MovieContract.FavoritesEntry.COLUMN_MOVIE_ID + "= ?",
                        new String[]{Long.toString(id)},
                        null, null,
                        sortOrder
                );
                break;
            }
            case LAST_REQUESTED: {
                returnCursor = mOpenHelper.getReadableDatabase().query(
                        MovieContract.LastRequestedEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null, null,
                        sortOrder
                );
                break;
            }
            case LAST_REQUESTED_ID: {
                long id = MovieContract.FavoritesEntry.getFavoritesIdFromUri(uri);
                returnCursor = mOpenHelper.getReadableDatabase().query(
                        MovieContract.LastRequestedEntry.TABLE_NAME,
                        projection,
                        MovieContract.LastRequestedEntry.COLUMN_MOVIE_ID + "= ?",
                        new String[]{Long.toString(id)},
                        null, null,
                        sortOrder
                );
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        returnCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return returnCursor;
    }

    @Override
    public Uri insert(@Nullable Uri uri, ContentValues values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri;

        switch (match) {
            case FAVORITES: {
                long _id = db.insert(MovieContract.FavoritesEntry.TABLE_NAME, null, values);
                if (_id > 0) {
                    returnUri = MovieContract.FavoritesEntry.buildFavoritesUri(_id);
                } else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;
            }
            case LAST_REQUESTED: {
                long _id = db.insert(MovieContract.LastRequestedEntry.TABLE_NAME, null, values);
                if (_id > 0) {
                    returnUri = MovieContract.LastRequestedEntry.buildFavoritesUri(_id);
                } else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int update(@Nullable Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int updatedRows;

        switch (match) {
            case FAVORITES: {
                updatedRows = db.update(
                        MovieContract.FavoritesEntry.TABLE_NAME, values, selection, selectionArgs
                );
                break;
            }
            case LAST_REQUESTED: {
                updatedRows = db.update(
                        MovieContract.LastRequestedEntry.TABLE_NAME, values, selection, selectionArgs
                );
                break;
            }
            default:
                throw new UnsupportedOperationException("Unkown uri: " + uri);
        }

        if (updatedRows != 0 || selection != null) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return updatedRows;
    }

    @Override
    public int delete(@Nullable Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int deletedRows;

        switch (match) {
            case FAVORITES: {
                deletedRows = db.delete(
                        MovieContract.FavoritesEntry.TABLE_NAME, selection, selectionArgs
                );
                break;
            }
            case LAST_REQUESTED: {
                deletedRows = db.delete(
                        MovieContract.LastRequestedEntry.TABLE_NAME, selection, selectionArgs
                );
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if (null != selection || deletedRows != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return deletedRows;
    }

    @Override
    public int bulkInsert(@NonNull Uri uri, @NonNull ContentValues[] values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);

        switch (match) {
            case LAST_REQUESTED:
                db.beginTransaction();
                int returnCount = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(MovieContract.LastRequestedEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            default:
                return super.bulkInsert(uri, values);
        }
    }
}
