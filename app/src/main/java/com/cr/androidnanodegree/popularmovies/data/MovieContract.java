package com.cr.androidnanodegree.popularmovies.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Class that defines the tables and columns for the movie database.
 */
public class MovieContract {

    // The content authority
    public static final String CONTENT_AUTHORITY = "com.cr.androidnanodegree.popularmovies";

    // Uri to contact the content provider
    public static final Uri BASE_CONENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    // Possible paths
    public static final String PATH_FAVORITES = "favorites";

    public static final class FavoritesEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONENT_URI.buildUpon().appendEncodedPath(PATH_FAVORITES).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" +
                        PATH_FAVORITES;
        
        // Table name
        public static final String TABLE_NAME = "favorites";

        // ID of the movie
        public static final String COLUMN_MOVIE_ID = "movie_id";

        // Path to the poster
        public static final String COLUMN_POSTER_PATH = "poster_path";

        public static Uri buildFavoritesUri(long movieId) {
            return ContentUris.withAppendedId(CONTENT_URI, movieId);
        }
    }
}