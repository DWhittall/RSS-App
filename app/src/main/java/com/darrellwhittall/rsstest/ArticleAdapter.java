package com.darrellwhittall.rsstest;

import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
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

    public static class ArticleViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final ImageView imageView;
        private final TextView titleView;
        private final TextView dateView;
        private final TextView authorView;

        // Dropdown Views
        private final TextView previewView;
        private final Button openBrowserBtn;
        private final Button shareButton;

        // TODO: Use data binding
        ArticleViewHolder(View v) {
            super(v);

            imageView = v.findViewById(R.id.iv_article_image);
            titleView = v.findViewById(R.id.tv_article_title);
            authorView = v.findViewById(R.id.tv_article_author);
            dateView = v.findViewById(R.id.tv_article_date);

            previewView = v.findViewById(R.id.wv_article_preview);
            openBrowserBtn = v.findViewById(R.id.btn_open_browser);
            shareButton = v.findViewById(R.id.btn_share);

            v.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {

            if(previewView.getVisibility() != View.VISIBLE){
                showPreview(view);
            } else {
                hidePreview(view);
            }

        }

        void showPreview(View view){

            view.setBackgroundColor(view.getResources()
                    .getColor(R.color.colorBackgroundTint, view.getContext().getTheme())
            );

            previewView.setVisibility(View.VISIBLE);
            openBrowserBtn.setVisibility(View.VISIBLE);
            shareButton.setVisibility(View.VISIBLE);

        }

        void hidePreview(View view){

            view.setBackground(null);

            previewView.setVisibility(View.GONE);
            openBrowserBtn.setVisibility(View.GONE);
            shareButton.setVisibility(View.GONE);
        }
    }

    ArticleAdapter(List<Article> dataset) {
        this.dataset = dataset;
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
        ConstraintLayout v = (ConstraintLayout) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.article_list_row, parent, false);

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

        holder.previewView.setText(currentArticle.getDescription());

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