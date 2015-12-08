package com.cr.androidnanodegree.popularmovies;

import android.util.Log;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Utilities
 */
public class Utility {

    protected static final String LOG_TAG = Utility.class.getSimpleName();

    public static String getYearFromReleaseDate(String releaseDate) {
        Date date;
        String year;

        DateFormat format = new SimpleDateFormat("yyyy", Locale.ENGLISH);
        if (releaseDate != null) {
            try {
                date = format.parse(releaseDate);
                year = format.format(date);
            } catch (ParseException e) {
                Log.e(LOG_TAG, "Parsing string to date resulted in error: " + e);
                year = "";
            }
        } else {
            year = "";
        }

        return year;
    }

}
