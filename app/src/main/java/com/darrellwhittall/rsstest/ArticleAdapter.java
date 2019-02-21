package com.darrellwhittall.rsstest;

import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.prof.rssparser.Article;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ArticleAdapter extends RecyclerView.Adapter<ArticleAdapter.ArticleViewHolder>  {

    private final List<Article> dataset;

    public static class ArticleViewHolder extends RecyclerView.ViewHolder {

        private final ImageView imageView;
        private final TextView titleView;
        private final TextView dateView;
        private final TextView authorView;

        ArticleViewHolder(@NonNull View view) {
            super(view);

            imageView = view.findViewById(R.id.iv_article_image);
            titleView = view.findViewById(R.id.tv_article_title);
            authorView = view.findViewById(R.id.tv_article_author);
            dateView = view.findViewById(R.id.tv_article_date);
        }
    }

    ArticleAdapter(List<Article> dataset) {
        this.dataset = dataset;
    }

    void updateData(List<Article> newData) {
        dataset.clear();
        dataset.addAll(newData);
        notifyDataSetChanged();
    }

    /**
     * Called when ViewHolders are created to fill a RecyclerView.
     *
     * @return A new TaskViewHolder that holds the view for each task
     */
    @Override
    @NonNull
    public ArticleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // create a new view
        CardView v = (CardView) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.article_list_item, parent, false);

        return new ArticleViewHolder(v);
    }

    /**
     * Called by the RecyclerView to display data at a specified position in the Cursor.
     *
     * @param holder   The ViewHolder to bind Cursor data to
     * @param position The position of the data in the Cursor
     */
    @Override
    public void onBindViewHolder(@NonNull ArticleViewHolder holder, int position) {

        Article currentArticle = dataset.get(position);

        // Set the holders data
        holder.titleView.setText(currentArticle.getTitle());
        holder.authorView.setText(currentArticle.getAuthor());

        if(currentArticle.getPubDate() != null) {
            holder.dateView.setText(currentArticle.getPubDate().toString());
        }

        Picasso.get()
                .load(currentArticle.getImage())
                .placeholder(R.drawable.ic_launcher_background)
                .into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return dataset.size();
    }
}