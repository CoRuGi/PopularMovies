package com.cr.androidnanodegree.popularmovies;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
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

    public static Boolean getExtraInformationPreference(Context context) {
        SharedPreferences sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getBoolean(
                context.getString(R.string.pref_extrainformation_key), true);
    }

}
