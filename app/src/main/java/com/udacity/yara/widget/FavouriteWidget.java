package com.udacity.yara.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.RemoteViews;

import com.udacity.yara.R;
import com.udacity.yara.ui.DetailActivity;

import java.util.Arrays;

public class FavouriteWidget extends AppWidgetProvider {
    private static String LOG_TAG = FavouriteWidget.class.getSimpleName();
    @Override
    public void onReceive(@NonNull Context context, @NonNull Intent intent) {
        super.onReceive(context, intent);
        Log.d(LOG_TAG,"onReceive() 1. "+intent.getAction()+" 2."+context.getString(R.string.data_update_key));
        if (context.getString(R.string.data_update_key).equals(intent.getAction())) {
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            int[] appWidgetIds = appWidgetManager.getAppWidgetIds(
                    new ComponentName(context, getClass()));
            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.widget_list);
        }
    }
    private void setRemoteAdapter(Context context, @NonNull final RemoteViews views) {
        Log.d(LOG_TAG,"setRemoteAdapter()");
        views.setRemoteAdapter(R.id.widget_list,
                new Intent(context, FavouriteWidgetRemoteViewsService.class));
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        Log.d(LOG_TAG,"onUpdate() : "+Arrays.toString(appWidgetIds));
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.favourite_widget);


            Intent intent = new Intent(context, DetailActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
            views.setPendingIntentTemplate(R.id.widget_list, pendingIntent);

            // Set up the collection

                setRemoteAdapter(context, views);



            views.setEmptyView(R.id.widget_list, R.id.widget_empty);

            // Tell the AppWidgetManager to perform an update on the current app widget
            appWidgetManager.updateAppWidget(appWidgetId, views);
            Log.d(LOG_TAG,"onUpdate() complete");
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
        Log.d(LOG_TAG,"onEnabled()");
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
        Log.d(LOG_TAG,"onDisabled()");
    }
}

