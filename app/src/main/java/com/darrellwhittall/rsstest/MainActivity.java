package com.darrellwhittall.rsstest;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.support.v7.widget.Toolbar;
import com.darrellwhittall.rsstest.room.Feed;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static final int ADD_FEED_INTENT_ID = 0;

    private ArticleViewModel articleViewModel;
    private FeedViewModel navViewModel;

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


        //Get viewModels
        articleViewModel = ViewModelProviders.of(this).get(ArticleViewModel.class);
        navViewModel = ViewModelProviders.of(this).get(FeedViewModel.class);

        // Get views
        articlesView = findViewById(R.id.rv_articles);
        loadingIndicator = findViewById(R.id.pb_loading_indicator);
        errorView = findViewById(R.id.tv_error_message);


        // Article RecyclerView Setup
        articlesView.setLayoutManager(new LinearLayoutManager(this));
        ArticleAdapter articleAdapter = new ArticleAdapter(new ArrayList<>());
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
        navViewModel.getAllFeeds().observe(this, feedAdapter::updateData);

        FloatingActionButton navFab = findViewById(R.id.nav_fab);
        navFab.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddFeedActivity.class);
            startActivityForResult(intent, ADD_FEED_INTENT_ID);
        });

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
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            drawerLayout.openDrawer(GravityCompat.START);
            return true;
        }
        
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(data == null || data.getExtras() == null){
            return;
        }

        if(requestCode == ADD_FEED_INTENT_ID && resultCode == RESULT_OK ){

            String name = data.getExtras().getString("Name");
            String url = data.getExtras().getString("URL");

            if(name != null && url != null) {
                navViewModel.insertFeed(new Feed(name, url));
            }

        }
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
        articleViewModel.fetchFeed(feed.getUrl());
    }
}