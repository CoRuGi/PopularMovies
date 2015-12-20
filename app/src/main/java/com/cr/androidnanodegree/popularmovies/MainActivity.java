package com.cr.androidnanodegree.popularmovies;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity
        implements MainActivityFragment.Callback {

    protected static final String LOG_TAG = MainActivity.class.getSimpleName();
    protected String mStoredSortByPreference;
    protected boolean mTwoPane;
    protected static final String DETAIL_FRAGMENT_TAG = "DFTAG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mStoredSortByPreference = Utility.getSortByPreference(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (findViewById(R.id.movie_detail_container) != null) {
            mTwoPane = true;
            if (savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.movie_detail_container,
                                new DetailActivityFragment(), DETAIL_FRAGMENT_TAG)
                        .commit();
            }
        } else {
            mTwoPane = false;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(LOG_TAG, "MainActivity onResume called.");
        String sortByPreference = Utility.getSortByPreference(this);
        Log.d(LOG_TAG, "Current sortByPreference is " + sortByPreference);
        Log.d(LOG_TAG, "Stored sortByPreference is " + mStoredSortByPreference);

        if (sortByPreference != null && !sortByPreference.equals(mStoredSortByPreference)) {
            MainActivityFragment mainActivityFragment = (MainActivityFragment)
                    getSupportFragmentManager().findFragmentById(R.id.fragment);
            if (null != mainActivityFragment) {
                mainActivityFragment.onSortByChanged();
            }
            mStoredSortByPreference = sortByPreference;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void OnItemSelected(Uri uri) {
        DetailActivityFragment detailActivityFragment =
                DetailActivityFragment.newInstance(uri);
        if (mTwoPane) {
            FragmentTransaction fragmentTransaction =
                    getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(
                    R.id.movie_detail_container, detailActivityFragment, DETAIL_FRAGMENT_TAG
            );
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        } else {
            Intent intent = new Intent(getApplicationContext(), DetailActivity.class)
                    .setData(uri);
            startActivity(intent);
        }
    }

    @Override
    public void OnItemSelected(MovieInformation movieInformation) {
        if (mTwoPane) {
            Intent intent = new Intent(getApplicationContext(), DetailActivityFragment.class)
                    .putExtra(
                            DetailActivityFragment.MOVIE_INFORMATION_EXTRA,
                            movieInformation
                    );
            setIntent(intent);
            FragmentTransaction fragmentTransaction =
                    getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(
                    R.id.movie_detail_container, new DetailActivityFragment(), DETAIL_FRAGMENT_TAG
            );
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        } else {
            Intent intent = new Intent(getApplicationContext(), DetailActivity.class)
                    .putExtra(
                            DetailActivityFragment.MOVIE_INFORMATION_EXTRA,
                            movieInformation
                    );
            startActivity(intent);
        }
    }

    @Override
    public String getStoredSortByPreference() {
        return mStoredSortByPreference;
    }

    @Override
    public void setStoredSortByPreference(String sortByPreference) {
        mStoredSortByPreference = sortByPreference;
    }

    @Override
    protected void onPause() {
        Log.d(LOG_TAG, "MainActivity onPause called.");
        super.onPause();
    }

    @Override
    protected void onStop() {
        Log.d(LOG_TAG, "MainActivity onStop called.");
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        Log.d(LOG_TAG, "MainActivity onDestroy called.");
        super.onDestroy();
    }
}
