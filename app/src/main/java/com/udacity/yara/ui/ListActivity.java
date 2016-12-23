package com.udacity.yara.ui;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
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

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.analytics.Tracker;
import com.udacity.yara.R;
import com.udacity.yara.adapter.RedditRecyclerAdapter;
import com.udacity.yara.analytics.AnalyticsApplication;
import com.udacity.yara.model.RedditItem;
import com.udacity.yara.util.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ListActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {


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


    private int counter = 0;
    private RedditRecyclerAdapter adapter;

    private boolean mTwoPane;
    private String after_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        ButterKnife.bind(this);

        modifyToolBar();
        drawerMenu = navigationView.getMenu();

        prefs = this.getSharedPreferences(getString(R.string.package_name), Context.MODE_PRIVATE);

        setDrawerLayout();


        AnalyticsApplication application = (AnalyticsApplication) getApplication();
        mTracker = application.getDefaultTracker();

        //Set the subreddits up in the navigation menu
        setSubrreddits();

        setNavigationView();

        if (savedInstanceState == null) {
            updateList(getResources().getString(R.string.Frontpage));
            getSupportActionBar().setTitle(R.string.Frontpage);
        } else {
            listItemsList = savedInstanceState.getParcelableArrayList(getString(R.string.listitems));
            mListState = savedInstanceState.getParcelable(getString(R.string.liststate_key));
            adapter = new RedditRecyclerAdapter(this, listItemsList);
            mRecyclerView.setAdapter(adapter);
            adapter.SetOnItemClickListener(adapterClick);
        }
        if (findViewById(R.id.reddititem_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;
            //getSupportFragmentManager().beginTransaction().replace(R.id.reddititem_detail_container,new DetailFragment()).commit();

            Log.i(LOG_TAG, "Two pane is true.");
        } else {
            Log.i(LOG_TAG, "Two pane is false.");
            mTwoPane = false;
        }

    }

    private void setSubrreddits() {
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
            }

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);
    }

    private void initLoader() {
        getLoaderManager().initLoader(0, null, this);
    }

    private void setNavigationView() {
        //Setting Navigation View Item Selected Listener to handle the item click of the navigation menu
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {

            // This method will trigger on item Click of navigation menu
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {

                if (menuItem.getGroupId() == R.id.group1) {
                    Intent openSetting = new Intent(getBaseContext(), ManageSubs.class);
                    openSetting.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    getBaseContext().startActivity(openSetting);
                    mDrawerLayout.closeDrawers();
                    return true;
                }
                if (menuItem.getGroupId() == R.id.groupFav) {
                    initLoader();
                    toolbar.setTitle("Favourites");
                    mDrawerLayout.closeDrawers();

                    return true;
                } else updateList(menuItem.toString());
                //Closing drawer on item click
                mDrawerLayout.closeDrawers();

                return true;
            }
        });
    }

    public void updateList(String subreddit) {


        counter = 0;
        toolbar.setTitle(subreddit);

        if (subreddit.equals(getResources().getString(R.string.Frontpage))) {
            subreddit = Constants.redditUrl + Constants.jsonEnd;
        } else {
            subreddit = Constants.redditUrl + Constants.subredditUrl + subreddit + Constants.jsonEnd;
        }


        adapter = new RedditRecyclerAdapter(this, listItemsList);
        mRecyclerView.setAdapter(adapter);
        adapter.SetOnItemClickListener(adapterClick);

        RequestQueue queue = Volley.newRequestQueue(this);

        adapter.clearAdapter();

        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET, subreddit, (String) null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {

                Log.d(LOG_TAG, response.toString());

                // Parse json data.
                try {
                    JSONObject data = response.getJSONObject("data");
                    after_id = data.getString("after");
                    JSONArray children = data.getJSONArray("children");

                    for (int i = 0; i < children.length(); i++) {

                        JSONObject post = children.getJSONObject(i).getJSONObject("data");
                        RedditItem item = new RedditItem();
                        item.setTitle(post.getString("title"));
                        item.setThumbnail(post.getString("thumbnail"));
                        item.setUrl(post.getString("url"));
                        item.setSubreddit(post.getString("subreddit"));
                        item.setAuthor(post.getString("author"));
                        item.setNumComments(post.getInt("num_comments"));
                        item.setScore(post.getInt("score"));
                        item.setOver18(post.getBoolean("over_18"));
                        item.setPermalink(post.getString("permalink"));
                        item.setPostedOn(post.getLong("created_utc"));
                        item.setId(post.getString("id"));
                        try {
                            Log.i(LOG_TAG, post.getJSONObject("preview").getJSONArray("images").getJSONObject(0).getJSONObject("source").getString("url"));
                            item.setImageUrl(post.getJSONObject("preview").getJSONArray("images").getJSONObject(0).getJSONObject("source").getString("url"));
                        } catch (JSONException e) {

                        }

                        listItemsList.add(item);

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                // Update list by notifying the adapter of changes
                adapter.notifyDataSetChanged();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(LOG_TAG, "Error: " + error.getMessage());

            }
        });

        queue.add(jsObjRequest);

    }

    public RedditRecyclerAdapter.OnItemClickListener adapterClick = new RedditRecyclerAdapter.OnItemClickListener() {
        @Override
        public void onItemClick(View view, int position) {

            RedditItem item = adapter.getListItems().get(position);
            item.getTitle();

            if (mTwoPane) {

                Bundle arguments = new Bundle();
                arguments.putString("title", item.getTitle());
                arguments.putString("subreddit", item.getSubreddit());
                arguments.putString("image_url", item.getImageUrl());
                arguments.putString("url", item.getUrl());
                arguments.putInt("score", item.getScore());
                arguments.putString("thumbnail", item.getThumbnail());
                arguments.putLong("postedOn", item.getPostedOn());
                arguments.putInt("num_comments", item.getNumComments());
                arguments.putString("permalink", item.getPermalink());
                arguments.putString("id", item.getId());
                arguments.putString("author", item.getAuthor());

                DetailFragment fragment = new DetailFragment();

                fragment.setArguments(arguments);
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.reddititem_detail_container, fragment)
                        .commit();
            } else {
                Intent openDetailActivity = new Intent(getBaseContext(), DetailActivity.class);

                openDetailActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                Bundle arguments = new Bundle();
                arguments.putString("title", item.getTitle());
                arguments.putString("subreddit", item.getSubreddit());
                arguments.putString("image_url", item.getImageUrl());
                arguments.putString("url", item.getUrl());
                arguments.putInt("score", item.getScore());
                arguments.putString("thumbnail", item.getThumbnail());
                arguments.putLong("postedOn", item.getPostedOn());
                arguments.putInt("num_comments", item.getNumComments());
                arguments.putString("permalink", item.getPermalink());
                arguments.putString("id", item.getId());
                arguments.putString("author", item.getAuthor());
                openDetailActivity.putExtras(arguments);
                getBaseContext().startActivity(openDetailActivity);
            }

        }


    };


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
