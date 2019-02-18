package com.darrellwhittall.rsstest;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.darrellwhittall.rsstest.room.Feed;

import java.util.List;

public class FeedAdapter extends RecyclerView.Adapter<FeedAdapter.FeedViewHolder> {

    private final List<Feed> dataset;
    private final OnFeedClickListener onClickListener;

    FeedAdapter(List<Feed> dataset, OnFeedClickListener onClickListener) {
        this.dataset = dataset;
        this.onClickListener = onClickListener;
    }

    @NonNull
    @Override
    public FeedViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LinearLayout view = (LinearLayout) LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.nav_list_item, viewGroup, false);
        return new FeedViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FeedViewHolder holder, int position) {
        holder.titleView.setText(dataset.get(position).getName());
        holder.itemView.setOnClickListener(view -> onClickListener.onFeedClick(dataset.get(position)));
    }

    @Override
    public int getItemCount() {
        return dataset.size();
    }

    /**
     * Updates the feeds data and notifies of it's change.
     */
    void updateData(List<Feed> newData) {
        dataset.clear();
        dataset.addAll(newData);
        notifyDataSetChanged();
    }

    class FeedViewHolder extends RecyclerView.ViewHolder {
        private final TextView titleView;

        FeedViewHolder(@NonNull View itemView) {
            super(itemView);
            titleView = itemView.findViewById(R.id.nav_list_item_title);
        }
    }

    interface OnFeedClickListener {
        void onFeedClick(Feed feed);
    }
}
