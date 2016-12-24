package com.udacity.yara.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.udacity.yara.R;
import com.udacity.yara.model.RedditItem;

import java.util.List;


public class RedditRecyclerAdapter extends RecyclerView.Adapter<RedditRecyclerAdapter.ListRowViewHolder> {
    private static String LOG_TAG = RedditRecyclerAdapter.class.getSimpleName();

    private List<RedditItem> listItemsList;
    private Context mContext;
    private ImageLoader mImageLoader;
   OnItemClickListener mItemClickListener;

    private int focusedItem = 0;


    public RedditRecyclerAdapter(Context context, List<RedditItem> listItemsList) {
        this.listItemsList = listItemsList;
        this.mContext = context;
    }

    @Override
    public RedditRecyclerAdapter.ListRowViewHolder onCreateViewHolder(final ViewGroup viewGroup, final int position) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.listitem_content, null);
        final ListRowViewHolder holder = new ListRowViewHolder(v);

        return holder;
    }

    @Override
    public void onBindViewHolder(ListRowViewHolder holder, int position) {
        RedditItem listItems = listItemsList.get(position);
        holder.itemView.setSelected(focusedItem == position);



        mImageLoader = ImageQueueManager.getInstance(mContext).getImageLoader();

        holder.thumbnail.setImageUrl(listItems.getThumbnail(), mImageLoader);
        //  listRowViewHolder.thumbnail.setDefaultImageResId(R.drawable.ic_action_menu);

        holder.title.setText(Html.fromHtml(listItems.getTitle()));
        //    listRowViewHolder.url.setText(Html.fromHtml(listItems.getUrl()));
        holder.subreddit.setText("r/"+ Html.fromHtml(listItems.getSubreddit()));
        holder.comments.setText((Html.fromHtml(String.valueOf(listItems.getNumComments()))));
        holder.score.setText(Html.fromHtml(String.valueOf(listItems.getScore())));
        //      listRowViewHolder.author.setText(Html.fromHtml(listItems.getAuthor()));

    }

public List<RedditItem> getListItems(){
    return  listItemsList;
}

    public void clearAdapter()
    {
        if(listItemsList!=null)
            listItemsList.clear();
        notifyDataSetChanged();
    }


    @Override
    public int getItemCount() {
        Log.d(LOG_TAG, listItemsList!=null ? String.valueOf(listItemsList.size()) : "Size is 0");
        return (null != listItemsList ? listItemsList.size() : 0);
    }

    public class ListRowViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        protected NetworkImageView thumbnail;
        protected TextView title;
        protected TextView url;
        protected RelativeLayout recLayout;
        protected TextView author;
        protected TextView subreddit, score, comments;


        public ListRowViewHolder(View view) {
            super(view);
            this.thumbnail = (NetworkImageView) view.findViewById(R.id.avatar_imageview);
            this.title = (TextView) view.findViewById(R.id.id);
//            this.recLayout = (RelativeLayout) view.findViewById(R.id.relCardLayout);
            this.subreddit = (TextView) view.findViewById(R.id.content);
            this.score = (TextView) view.findViewById(R.id.score);
            this.comments = (TextView) view.findViewById(R.id.comments);
            view.setClickable(true);
            view.setOnClickListener(this);

        }


        @Override
        public void onClick(View v) {
            if (mItemClickListener != null) {
                mItemClickListener.onItemClick(v, getAdapterPosition());
            }
        }


    }
    public interface OnItemClickListener {
        public void onItemClick(View view, int position);
    }

    public void SetOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }
}