package com.udacity.yara.ui;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;


import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.udacity.yara.R;
import com.udacity.yara.adapter.CommentAdapter;
import com.udacity.yara.adapter.CommentProcessor;
import com.udacity.yara.adapter.ImageQueueManager;
import com.udacity.yara.analytics.AnalyticsApplication;
import com.udacity.yara.data.FavContract;
import com.udacity.yara.model.Comment;
import com.udacity.yara.util.Constants;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;


public class DetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private static String LOG_TAG = DetailFragment.class.getSimpleName();
    Boolean isFavourite;
    String id;


    Parcelable mListState;
    Bundle extras;

    RecyclerView.LayoutManager mLayoutManager;
    private Tracker mTracker;

    CommentProcessor processor;
    ArrayList<Comment> comments;
    ImageLoader mImageLoader;

    @Bind(R.id.commentRecycler)
    RecyclerView mRecyclerView;
    CommentAdapter commentAdapter;

    @Bind(R.id.menu)
    ImageButton menu;

    @Bind(R.id.addFav)
    ImageButton favStar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

        ButterKnife.bind(this, rootView);

        final Activity activity = this.getActivity();

        comments = new ArrayList<Comment>();

        extras = getActivity().getIntent().getExtras();

        if (extras == null) {
            extras = getArguments();
        }

        final String url = Constants.redditUrl + extras.getString("permalink") + Constants.jsonExt;
        Log.i(LOG_TAG, url);

        processor = new CommentProcessor(url);
        id = extras.getString(getString(R.string.reddit_id));
        class AddComments extends AsyncTask<String, Void, String> {

            protected String doInBackground(String... arg0) {
                //Your implementation
                comments.addAll(processor.fetchComments());
                return "done";
            }

            protected void onPostExecute(String result) {
                commentAdapter = new CommentAdapter(activity, comments);
                mRecyclerView.setAdapter(commentAdapter);
                mLayoutManager = new LinearLayoutManager(getContext());
                mRecyclerView.setLayoutManager(mLayoutManager);
                mRecyclerView.setNestedScrollingEnabled(false); // Disables scrolling
            }
        }
        if (savedInstanceState == null) {
            AddComments addComments = new AddComments();
            addComments.execute(url);
        } else {
            // AddComments addComments=new AddComments();
            //addComments.execute(url);
            comments = savedInstanceState.getParcelableArrayList(getString(R.string.listitems));
            mListState = savedInstanceState.getParcelable(getString(R.string.liststate_key));
            commentAdapter = new CommentAdapter(activity, comments);
            mRecyclerView.setAdapter(commentAdapter);
            mLayoutManager = new LinearLayoutManager(getContext());
            mRecyclerView.getLayoutManager().onRestoreInstanceState(mListState);
            mRecyclerView.setNestedScrollingEnabled(false);
        }


        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(getContext(), menu);
                //Inflating the Popup using xml file
                popup.getMenuInflater()
                        .inflate(R.menu.detail_menu, popup.getMenu());
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.open:
                                Intent i = new Intent(Intent.ACTION_VIEW);
                                i.setData(Uri.parse(extras.getString("url")));
                                startActivity(i);
                                break;
                            case R.id.share:
                                Intent sendIntent = new Intent();
                                sendIntent.setAction(Intent.ACTION_SEND);
                                sendIntent.putExtra(Intent.EXTRA_TEXT, extras.getString("url"));
                                sendIntent.setType("text/plain");
                                startActivity(Intent.createChooser(sendIntent, "Share link"));
                        }
                        return true;
                    }
                });

                popup.show();
            }
        });


        AdView mAdView = (AdView) rootView.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().
                addTestDevice(AdRequest.DEVICE_ID_EMULATOR).
                build();
        mAdView.loadAd(adRequest);
        

        // Obtain the shared Tracker instance.
        AnalyticsApplication application = (AnalyticsApplication) getActivity().getApplication();
        mTracker = application.getDefaultTracker();


        favStar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isFavourite) {

                    ContentValues values = new ContentValues();
                    values.put(FavContract.favourite.COLUMN_TITLE, extras.getString("title"));
                    values.put(FavContract.favourite.COLUMN_AUTHOR, extras.getString("author"));
                    values.put(FavContract.favourite.COLUMN_PERMALINK, extras.getString("permalink"));
                    values.put(FavContract.favourite.COLUMN_POINTS, extras.getInt("score"));
                    values.put(FavContract.favourite.COLUMN_COMMENTS, extras.getInt("num_comments"));
                    values.put(FavContract.favourite.COLUMN_IMAGE_URL, extras.getString("image_url"));
                    values.put(FavContract.favourite.COLUMN_URL, extras.getString("url"));
                    values.put(FavContract.favourite.COLUMN_THUMBNAIL, extras.getString("thumbnail"));
                    values.put(FavContract.favourite.COLUMN_POSTED_ON, extras.getLong("postedOn"));
                    values.put(FavContract.favourite.COLUMN_POST_ID, id);
                    values.put(FavContract.favourite.COLUMN_SUBREDDIT, extras.getString("subreddit"));
                    values.put(FavContract.favourite.COLUMN_FAVORITES, 1);
                    getContext().getContentResolver().insert(FavContract.favourite.CONTENT_URI, values);

                    //  Toast.makeText(getContext(), "Post added to favourites", Toast.LENGTH_SHORT).show();
                    favStar.setSelected(true);
                    isFavourite = true;

                } else {
                    //Toast.makeText(getContext(),"Post removed...",Toast.LENGTH_SHORT).show();

                    favStar.setSelected(false);
                    isFavourite = false;
                    //Delete from db
                    getContext().getContentResolver().delete(FavContract.favourite.CONTENT_URI,
                            FavContract.favourite.COLUMN_POST_ID + "=?", new String[]{String.valueOf(id)});
                }

                //Notify widget to update
                Context context = getContext();
                Intent dataUpdatedIntent = new Intent(getString(R.string.data_update_key))
                        .setPackage(context.getPackageName());
                context.sendBroadcast(dataUpdatedIntent);
            }
        });

        mImageLoader = ImageQueueManager.getInstance(getContext()).getImageLoader();
        NetworkImageView headerImage = (NetworkImageView) rootView.findViewById(R.id.headerImage);

        headerImage.setImageUrl(extras.getString("image_url"), mImageLoader);
        if (headerImage.getImageURL() == null) {
            headerImage.setVisibility(View.GONE);
        }

        TextView commentsTxt = (TextView) rootView.findViewById(R.id.commentsNum);
        commentsTxt.setText(String.valueOf((extras.getInt("num_comments"))) );

        TextView points = (TextView) rootView.findViewById(R.id.score);
        points.setText(String.valueOf((extras.getInt("score"))) );

        TextView title = (TextView) rootView.findViewById(R.id.headerTitle);
        title.setText(String.valueOf((extras.getString("title"))));

        initLoader();
        return rootView;
    }

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public DetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {

        super.onSaveInstanceState(outState);
        // Save list state
        Log.i(LOG_TAG, "onSaveInstance Save outstate");
        outState.putParcelable(getString(R.string.liststate_key), mLayoutManager.onSaveInstanceState());
        outState.putParcelableArrayList(getString(R.string.listitems), comments);

    }


    @Override
    public void onResume() {
        super.onResume();
        Log.i(LOG_TAG, "Setting screen name: " + "Detail Fragment");
        mTracker.setScreenName("Image~" + "Detail Activity");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());


    }

    private void initLoader() {

        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int Id, Bundle args) {
        return new CursorLoader(getContext(),
                FavContract.favourite.CONTENT_URI,
                null,
                FavContract.favourite.COLUMN_POST_ID + "=?",
                new String[]{id},
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        int fav = 0;
        isFavourite = false;
        Log.i("loader", "finished");
        if (cursor != null && cursor.getCount() > 0) {
            if (cursor.moveToFirst()) {
                fav = cursor.getInt(10);
                Log.d(LOG_TAG, "  " + cursor.getString(3));
            }
        }

        if (fav == 1) {
            isFavourite = true;
            favStar.setSelected(true);
        } else {
            isFavourite = false;
            favStar.setSelected(false);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }

}
