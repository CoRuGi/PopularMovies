package com.cr.androidnanodegree.popularmovies;

import android.app.Application;

import com.facebook.stetho.Stetho;

/**
 * Created by Coen on 07/12/2015.
 */
public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Stetho.initializeWithDefaults(this);
    }
}
