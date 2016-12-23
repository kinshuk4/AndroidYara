package com.udacity.yara.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import com.udacity.yara.R;
import com.udacity.yara.helper.ItemTouchHelperAdapter;
import com.udacity.yara.helper.ItemTouchHelperViewHolder;
import com.udacity.yara.helper.OnStartDragListener;
import com.udacity.yara.util.StringUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;


public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ItemHolder> implements ItemTouchHelperAdapter {
    private static String LOG_TAG = RecyclerViewAdapter.class.getSimpleName();

    private OnItemClickListener onItemClickListener;
    private LayoutInflater layoutInflater;
    private final OnStartDragListener mDragStartListener;
    private final ArrayList<String> mItems = new ArrayList<>();

    SharedPreferences prefs;

    public RecyclerViewAdapter(Context context, OnStartDragListener dragStartListener) {
        mDragStartListener = dragStartListener;
        layoutInflater = LayoutInflater.from(context);
        prefs = context.getSharedPreferences(
                context.getString(R.string.package_name), Context.MODE_PRIVATE);
        Log.d(LOG_TAG, prefs.getString(context.getString(R.string.subreddit_pref_key), "ERROR"));


        mItems.addAll(Arrays.asList(prefs.getString(context.getString(R.string.subreddit_pref_key), "").split(",")));

    }

    @Override
    public RecyclerViewAdapter.ItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = layoutInflater.inflate(R.layout.subreddit_layout_item, parent, false);
        return new ItemHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final RecyclerViewAdapter.ItemHolder holder, int position) {
        holder.setItemName(mItems.get(position));
        holder.handleView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (MotionEventCompat.getActionMasked(event) == MotionEvent.ACTION_DOWN) {
                    mDragStartListener.onStartDrag(holder);
                }
                return false;
            }
        });
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    public ArrayList<String> getmItems() {
        return mItems;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        onItemClickListener = listener;
    }

    public OnItemClickListener getOnItemClickListener() {
        return onItemClickListener;
    }

    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        Collections.swap(mItems, fromPosition, toPosition);
        notifyItemMoved(fromPosition, toPosition);
        prefs.edit().putString(Resources.getSystem().getString(R.string.subreddit_pref_key), StringUtil.arrayToString(mItems)).apply();
        return true;

    }

    @Override
    public void onItemDismiss(int position) {
        mItems.remove(position);
        notifyItemRemoved(position);
        prefs.edit().putString(Resources.getSystem().getString(R.string.subreddit_pref_key), StringUtil.arrayToString(mItems)).apply();
    }

    public interface OnItemClickListener {
        public void onItemClick(ItemHolder item, int position);
    }

    public void add(int location, String iName) {
        mItems.add(location, iName);
        notifyItemInserted(location);
        prefs.edit().putString(Resources.getSystem().getString(R.string.subreddit_pref_key), StringUtil.arrayToString(mItems)).apply();
    }

    public void remove(int location) {
        if (location >= mItems.size())
            return;

        mItems.remove(location);
        notifyItemRemoved(location);
        prefs.edit().putString(Resources.getSystem().getString(R.string.subreddit_pref_key), StringUtil.arrayToString(mItems)).apply();
    }

    public static class ItemHolder extends RecyclerView.ViewHolder implements
            ItemTouchHelperViewHolder {

        public final TextView textItemName;
        public final ImageView handleView;

        public ItemHolder(View itemView) {
            super(itemView);

            textItemName = (TextView) itemView.findViewById(R.id.item_name);
            handleView = (ImageView) itemView.findViewById(R.id.handle);
        }

        public void setItemName(CharSequence name) {
            textItemName.setText(name);
        }

        public CharSequence getItemName() {
            return textItemName.getText();
        }


        @Override
        public void onItemSelected() {
            itemView.setBackgroundColor(Color.LTGRAY);
        }

        @Override
        public void onItemClear() {
            itemView.setBackgroundColor(0);
        }
    }
}
