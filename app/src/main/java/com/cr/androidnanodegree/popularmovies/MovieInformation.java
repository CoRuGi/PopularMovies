package com.cr.androidnanodegree.popularmovies;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Contains information about a movie.
 */
public class MovieInformation implements Parcelable {
    protected String LOG_TAG = MovieInformation.class.getSimpleName();
    String id;
    String title;
    String posterPath;
    String synopsis;
    String voteAverage;
    String releaseDate;
    String backdropPath;
    Bitmap poster;

    public MovieInformation() {

    }

    public MovieInformation(Parcel in) {
        setId(in.readString());
        setTitle(in.readString());
        setPosterPath(in.readString());
        setSynopsis(in.readString());
        setVoteAverage(in.readString());
        setReleaseDate(in.readString());
        setBackdropPath(in.readString());
        setPoster((Bitmap) in.readParcelable(getClass().getClassLoader()));
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }

    public String getSynopsis() {
        return synopsis;
    }

    public void setSynopsis(String synopsis) {
        this.synopsis = synopsis;
    }

    public String getVoteAverage() {
        return voteAverage;
    }

    public void setVoteAverage(String voteAverage) {
        this.voteAverage = voteAverage;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getBackdropPath() {
        return backdropPath;
    }

    public void setBackdropPath(String backdropPath) {
        this.backdropPath = backdropPath;
    }

    public Bitmap getPoster() {
        return poster;
    }

    public void setPoster(Bitmap poster) {
        this.poster = poster;
    }

    public String getYearFromReleaseDate() {
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

    @Override
    public String toString() {
        return title;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(getId());
        dest.writeString(getTitle());
        dest.writeString(getPosterPath());
        dest.writeString(getSynopsis());
        dest.writeString(getVoteAverage());
        dest.writeString(getReleaseDate());
        dest.writeString(getBackdropPath());
        dest.writeParcelable(getPoster(), flags);
    }

    public static final Parcelable.Creator<MovieInformation> CREATOR
            = new Parcelable.Creator<MovieInformation>() {
        public MovieInformation createFromParcel(Parcel in) {
            return new MovieInformation(in);
        }

        public MovieInformation[] newArray(int size) {
            return new MovieInformation[size];
        }
    };
}
