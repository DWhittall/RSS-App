package com.darrellwhittall.rsstest;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.support.v7.widget.Toolbar;
import com.darrellwhittall.rsstest.room.Feed;
import com.prof.rssparser.Article;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private ArticleViewModel articleViewModel;

    private DrawerLayout drawerLayout;
    private RecyclerView articlesView;
    private ProgressBar loadingIndicator;
    private TextView errorView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        if(actionbar != null) {
            actionbar.setDisplayHomeAsUpEnabled(true);
            actionbar.setHomeAsUpIndicator(R.drawable.ic_menu);
        }

        //Get viewModels and views
        articleViewModel = ViewModelProviders.of(this).get(ArticleViewModel.class);
        FeedViewModel feedViewModel = ViewModelProviders.of(this).get(FeedViewModel.class);

        articlesView = findViewById(R.id.rv_articles);
        loadingIndicator = findViewById(R.id.pb_loading_indicator);
        errorView = findViewById(R.id.tv_error_message);

        // Article RecyclerView Setup
        articlesView.setLayoutManager(new LinearLayoutManager(this));
        ArticleAdapter articleAdapter = new ArticleAdapter(new ArrayList<>(), article -> {
            Intent intent = new Intent(MainActivity.this, ArticleActivity.class);
            intent.putExtra("Title", article.getTitle());
            intent.putExtra("Author", article.getAuthor());

            if(article.getPubDate() != null) {
                intent.putExtra("Date", article.getPubDate().toString());
            }

            intent.putExtra("Image", article.getImage());
            intent.putExtra("Content", article.getContent());

            startActivity(intent);
        });
        articlesView.setAdapter(articleAdapter);

        articleViewModel.getArticleList().observe(this, articles -> {
            if(articles == null){
                showErrorMessage(R.string.feed_load_error);
                return;
            }

            if(articles.size() == 0) {
                showErrorMessage(R.string.feed_empty_error);
                return;
            }

            articleAdapter.updateData(articles);
            showArticlesList();
        });

        // Navigation View Setup
        RecyclerView feedsView = findViewById(R.id.rv_nav_feeds);
        drawerLayout = findViewById(R.id.drawer_layout);
        feedsView.setLayoutManager(new LinearLayoutManager(this));

        FeedAdapter feedAdapter = new FeedAdapter(new ArrayList<>(), feed -> {
            loadNewFeed(feed);
            drawerLayout.closeDrawers();
        });

        feedsView.setAdapter(feedAdapter);
        feedViewModel.getAllFeeds().observe(this, feedAdapter::updateData);

        FloatingActionButton navFab = findViewById(R.id.nav_fab);
        navFab.setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, AddFeedActivity.class))
        );

        // Only load a default page if the articleViewModel hasn't tried to load anything yet
        // This means it should be the first time the app has opened since it was last destroyed
        if(articleViewModel.getState() == ArticleViewModel.State.BLANK) {
            loadNewFeed(new Feed(
                    "Rock Paper Shotgun",
                    "http://feeds.feedburner.com/RockPaperShotgun")
            );
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.action_bar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            drawerLayout.openDrawer(GravityCompat.START);
            return true;
        }

        if (item.getItemId() == R.id.action_edit){
            Intent intent = new Intent(MainActivity.this, AddFeedActivity.class);

            Feed currentFeed = articleViewModel.getCurrentFeed();
            intent.putExtra("FeedTitle", currentFeed.getName());
            intent.putExtra("FeedURL", currentFeed.getUrl());

            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     *  Hides all relevant views and displays the error message
     */
    private void showErrorMessage(int errorMsgResource){
        articlesView.setVisibility(View.GONE);
        loadingIndicator.setVisibility(View.GONE);
        errorView.setVisibility(View.VISIBLE);
        errorView.setText(errorMsgResource);
    }

    /**
     *  Hides all overlapping views and displays the recycler views
     */
    private void showArticlesList(){
        articlesView.setVisibility(View.VISIBLE);
        loadingIndicator.setVisibility(View.GONE);
        errorView.setVisibility(View.GONE);
    }

    /**
     * Hides the feed, show the loading bar and tells the viewModel to fetch a new feed
     */
    private void loadNewFeed(Feed feed){
        articlesView.setVisibility(View.INVISIBLE);
        loadingIndicator.setVisibility(View.VISIBLE);
        errorView.setVisibility(View.GONE);

        setTitle(feed.getName());
        articleViewModel.fetchFeed(feed);
    }
}