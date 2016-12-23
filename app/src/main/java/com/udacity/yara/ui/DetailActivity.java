package com.udacity.yara.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;


import com.android.volley.toolbox.ImageLoader;
import com.udacity.yara.R;

import butterknife.Bind;
import butterknife.ButterKnife;


public class DetailActivity extends AppCompatActivity {
    private static String LOG_TAG = DetailActivity.class.getSimpleName();

    String id;
    Bundle extras;


    @Bind(R.id.toolbar)
    Toolbar toolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        extras = getIntent().getExtras();
        id = extras.getString(getString(R.string.reddit_id));

        toolbar.setTitle(extras.getString(getString(R.string.reddit_subreddit)));
        toolbar.setTitle(getString(R.string.title_ask_reddit));


        //
        // Show the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        // saved from previous configurations of this activity
        // (e.g. when rotating the screen from portrait to landscape).
        // In this case, the fragment will automatically be re-added
        // to its container so we don't need to manually add it.
        // For more information, see the Fragments API guide at:
        //
        // http://developer.android.com/guide/components/fragments.html
        //
        Log.d(LOG_TAG, getIntent().getExtras().getString(getString(R.string.reddit_title)));
        if (savedInstanceState == null) {
            // Create the detail fragment and add it to the activity
            // using a fragment transaction.
            Bundle arguments = new Bundle();
            arguments.putString(getString(R.string.arg_item_id),
                    getIntent().getStringExtra(getString(R.string.arg_item_id)));
            DetailFragment fragment = new DetailFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.frameLayout, fragment)
                    .commit();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            // This ID represents the Home or Up button. In the case of this
            // activity, the Up button is shown. Use NavUtils to allow users
            // to navigate up one level in the application structure. For
            // more details, see the Navigation pattern on Android Design:
            //
            // http://developer.android.com/design/patterns/navigation.html#up-vs-back
            //
            NavUtils.navigateUpTo(this, new Intent(this, ListActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
