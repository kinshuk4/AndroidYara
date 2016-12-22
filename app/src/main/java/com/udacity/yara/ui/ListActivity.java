package com.udacity.yara.ui;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Loader;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Parcelable;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.android.gms.analytics.Tracker;
import com.udacity.yara.R;
import com.udacity.yara.analytics.AnalyticsApplication;
import com.udacity.yara.model.RedditItem;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ListActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{


    private static String LOG_TAG = ListActivity.class.getSimpleName();

    @Bind(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;

    @Bind(R.id.reddititem_list)
    RecyclerView mRecyclerView;

    //    @Bind(R.id.toolbar)
    Toolbar toolbar;

    @Bind(R.id.navigation_view)
    NavigationView navigationView;
    Menu drawerMenu;

    SharedPreferences prefs;
    private Tracker mTracker;

    private ArrayList<RedditItem> listItemsList = new ArrayList<RedditItem>();
    Parcelable mListState;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        ButterKnife.bind(this);

        modifyToolBar();
        drawerMenu = navigationView.getMenu();

        prefs = this.getSharedPreferences(getString(R.string.package_name), Context.MODE_PRIVATE);

        setDrawerLayout();



        //Set the subreddits up in the navigation menu
        setSubrreddits();



    }

    public void setSubrreddits() {
        MenuItem saveThis = drawerMenu.getItem(0);
        //int groupIDtoRemove=drawerMenu.getItem(1).getGroupId();
        drawerMenu.removeGroup(500);
        if (prefs.getBoolean(getString(R.string.first_run), true)) {
            // Do first run stuff here then set 'firstrun' as false
            // using the following line to edit/commit prefs
            prefs.edit().putString(getString(R.string.subreddit_pref_key), getString(R.string.initial_subs)).commit();
            prefs.edit().putBoolean(getString(R.string.first_run), false).commit();
        }


        String subString = prefs.getString(getString(R.string.subreddit_pref_key), "");
        List<String> mItems = Arrays.asList(subString.split(","));
        for (int i = 0; i < mItems.size(); i++) {
            drawerMenu.add(500, Menu.NONE, Menu.NONE, mItems.get(i));
        }
    }


    private void modifyToolBar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setDisplayShowHomeEnabled(false);
        toolbar.setNavigationIcon(R.drawable.ic_menu_white_24dp);
    }

    private void setDrawerLayout() {
        mDrawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                mDrawerLayout,         /* DrawerLayout object */
                toolbar,  /* nav drawer icon to replace 'Up' caret */
                R.string.openDes,  /* "open drawer" description */
                R.string.closedDes  /* "close drawer" description */
        ) {

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                //getActionBar().setTitle(mTitle);
            }

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                // getActionBar().setTitle(mDrawerTitle);
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);
    }


    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
