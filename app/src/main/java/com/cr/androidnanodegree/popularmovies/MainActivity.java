package com.cr.androidnanodegree.popularmovies;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity
        implements MainActivityFragment.Callback {

    protected boolean mTwoPane;
    protected static final String DETAIL_FRAGMENT_TAG = "DFTAG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
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
    public void OnItemSelected(Uri favoritesUri) {
        DetailActivityFragment detailActivityFragment =
                DetailActivityFragment.newInstance(favoritesUri);
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
                    .setData(favoritesUri);
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
}
