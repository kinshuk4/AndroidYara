package com.udacity.yara.widget;

import android.content.Intent;
import android.database.Cursor;
import android.os.Binder;
import android.util.Log;
import android.widget.AdapterView;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.udacity.yara.R;
import com.udacity.yara.data.FavContract;
import com.udacity.yara.model.RedditItem;


public class FavouriteWidgetRemoteViewsService extends RemoteViewsService {
    private static String LOG_TAG = FavouriteWidgetRemoteViewsService.class.getSimpleName();

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new RemoteViewsFactory() {
            private Cursor data = null;

            @Override
            public void onCreate() {
                // Nothing to do
                Log.d(LOG_TAG,"onCreate()");
            }

            @Override
            public void onDataSetChanged() {
                Log.d(LOG_TAG,"onDataSetChanged()");
                if (data != null) {
                    data.close();
                }
                final long identityToken = Binder.clearCallingIdentity();
                data = getContentResolver().query(FavContract.favourite.CONTENT_URI,
                        null,
                        null,
                        null,
                        null);
                Binder.restoreCallingIdentity(identityToken);
            }

            @Override
            public void onDestroy() {
                Log.d(LOG_TAG,"onDestroy()");
                if (data != null) {
                    data.close();
                    data = null;
                }
            }

            @Override
            public int getCount() {
                return data == null ? 0 : data.getCount();

            }

            @Override
            public RemoteViews getViewAt(int position) {
                Log.d(LOG_TAG,"getViewAt()");
                if (position == AdapterView.INVALID_POSITION ||
                        data == null || !data.moveToPosition(position)) {
                    Log.d(LOG_TAG,"getViewAt() invalid position");
                    return null;
                }
                RemoteViews views = new RemoteViews(getPackageName(),
                        R.layout.favourite_widget_list_item);

                String title=data.getString(2);
                String subreddit=data.getString(12);
                int points=data.getInt(9);
                int comments=data.getInt(8);

                views.setTextViewText(R.id.widgetTitle, title);
                views.setTextViewText(R.id.widgeSub, "r/"+subreddit);
                views.setTextViewText(R.id.widgetPoints, String.valueOf(points));
                views.setTextViewText(R.id.widgetComments, String.valueOf(comments));
                //final Intent fillInIntent = new Intent();
                Intent openDetailActivity = new Intent();



                RedditItem item = new RedditItem();
                item.setId(data.getString(1));
                item.setTitle(data.getString(2));
                item.setAuthor(data.getString(3));
                item.setThumbnail(data.getString(4));
                item.setPermalink(data.getString(5));
                item.setUrl(data.getString(6));
                item.setImageUrl(data.getString(7));
                item.setNumComments(data.getInt(8));
                item.setScore(data.getInt(9));
                item.setPostedOn(data.getLong(11));
                item.setOver18(false);
                item.setSubreddit(data.getString(12));
                Log.d(LOG_TAG,"getViewAt() Opening detail location");

                openDetailActivity.putExtra("title",item.getTitle());
                openDetailActivity.putExtra("subreddit",item.getSubreddit());
                openDetailActivity.putExtra("image_url",item.getImageUrl());
                openDetailActivity.putExtra("url",item.getUrl());
                openDetailActivity.putExtra("score",item.getScore());
                openDetailActivity.putExtra("thumbnail",item.getThumbnail());
                openDetailActivity.putExtra("postedOn",item.getPostedOn());
                openDetailActivity.putExtra("num_comments",item.getNumComments());
                openDetailActivity.putExtra("permalink",item.getPermalink());
                openDetailActivity.putExtra("id",item.getId());
                openDetailActivity.putExtra("author",item.getAuthor());
                openDetailActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);


                views.setOnClickFillInIntent(R.id.widget_list_item, openDetailActivity);
                return views;
            }

            @Override
            public RemoteViews getLoadingView() {
                return new RemoteViews(getPackageName(), R.layout.favourite_widget_list_item);
            }

            @Override
            public int getViewTypeCount() {
                return 1;
            }

            @Override
            public long getItemId(int position) {
                return position;
            }

            @Override
            public boolean hasStableIds() {
                return true;
            }
        };
    }

    ;
}
