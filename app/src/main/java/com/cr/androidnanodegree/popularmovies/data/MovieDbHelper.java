package com.cr.androidnanodegree.popularmovies.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.cr.androidnanodegree.popularmovies.data.MovieContract.FavoritesEntry;
import com.cr.androidnanodegree.popularmovies.data.MovieContract.LastRequestedEntry;

/**
 * Manages a local database for favorites data.
 */
public class MovieDbHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;

    static final String DATABASE_NAME = "movie.db";

    public MovieDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create the table to hold favorites.
        final String SQL_CREATE_FAVORITES_TABLE =
                "CREATE TABLE " + FavoritesEntry.TABLE_NAME + " (" +
                        FavoritesEntry._ID + " INTEGER PRIMARY KEY, " +
                        FavoritesEntry.COLUMN_MOVIE_ID + " REAL UNIQUE NOT NULL, " +
                        FavoritesEntry.COLUMN_MOVIE_TITLE + " TEXT NOT NULL, " +
                        FavoritesEntry.COLUMN_POSTER + " BLOB NOT NULL, " +
                        FavoritesEntry.COLUMN_MOVIE_SYNOPSIS + " TEXT NOT NULL, " +
                        FavoritesEntry.COLUMN_VOTE_AVERAGE + " TEXT NOT NULL, " +
                        FavoritesEntry.COLUMN_RELEASE_DATE + " TEXT NOT NULL, " +
                        FavoritesEntry.COLUMN_BACKDROP_PATH + " TEXT NOT NULL" +
                        " );";

        // Create the table to hold favorites.
        final String SQL_CREATE_LAST_REQUESTED_TABLE =
                "CREATE TABLE " + LastRequestedEntry.TABLE_NAME + " (" +
                        LastRequestedEntry._ID + " INTEGER PRIMARY KEY, " +
                        LastRequestedEntry.COLUMN_MOVIE_ID + " REAL UNIQUE NOT NULL, " +
                        LastRequestedEntry.COLUMN_MOVIE_TITLE + " TEXT NOT NULL, " +
                        LastRequestedEntry.COLUMN_POSTER + " BLOB NOT NULL, " +
                        LastRequestedEntry.COLUMN_MOVIE_SYNOPSIS + " TEXT NOT NULL, " +
                        LastRequestedEntry.COLUMN_VOTE_AVERAGE + " TEXT NOT NULL, " +
                        LastRequestedEntry.COLUMN_RELEASE_DATE + " TEXT NOT NULL, " +
                        LastRequestedEntry.COLUMN_BACKDROP_PATH + " TEXT NOT NULL" +
                        " );";

        db.execSQL(SQL_CREATE_FAVORITES_TABLE);
        db.execSQL(SQL_CREATE_LAST_REQUESTED_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onCreate(db);
    }
}
