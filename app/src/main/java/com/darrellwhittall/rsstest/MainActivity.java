package com.darrellwhittall.rsstest;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.darrellwhittall.rsstest.room.Feed;
import com.prof.rssparser.Article;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final int ADD_FEED_INTENT_ID = 0;

    private ArticleListViewModel articleViewModel;
    private NavigationViewModel navViewModel;

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private DrawerLayout drawerLayout;
    private ProgressBar loadingIndicator;
    private TextView errorView;

    private FloatingActionButton navFab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        //Get viewModels
        articleViewModel = ViewModelProviders.of(this).get(ArticleListViewModel.class);
        navViewModel = ViewModelProviders.of(this).get(NavigationViewModel.class);

        // Get views
        recyclerView = findViewById(R.id.rv_articles);
        drawerLayout = findViewById(R.id.drawer_layout);
        loadingIndicator = findViewById(R.id.pb_loading_indicator);
        errorView = findViewById(R.id.tv_error_message);
        navFab = findViewById(R.id.nav_fab);

        // Setup Views
        createArticlesList();
        createNavigationView();

        // Only load a default page if the articleViewModel hasn't tried to load anything yet
        // This means it should be the first time the app has opened since it was last destroyed
        if(articleViewModel.getState() == ArticleListViewModel.State.BLANK) {
            setTitle("Rock Paper Shotgun");
            loadNewFeed("http://feeds.feedburner.com/RockPaperShotgun");
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == ADD_FEED_INTENT_ID && resultCode == RESULT_OK && data != null){

            String name = data.getExtras().getString("Name");
            String url = data.getExtras().getString("URL");

            if(name != null && url != null) {
                navViewModel.insertFeed(new Feed(name, url));
            }

        }
    }

    /**
     * RECYCLER VIEW SETUP
     */
    private void createArticlesList() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ArticleAdapter(new ArrayList<Article>());
        recyclerView.setAdapter(adapter);

        articleViewModel.getArticleList().observe(this, new Observer<List<Article>>() {
            @Override
            public void onChanged(@Nullable List<Article> articles) {

                if(articles == null){
                    showErrorMessage(R.string.feed_load_error);
                    return;
                }

                if(articles.size() == 0) {
                    showErrorMessage(R.string.feed_empty_error);
                    return;
                }

                showArticlesList();
                adapter = new ArticleAdapter(articles);
                recyclerView.setAdapter(adapter);
                adapter.notifyDataSetChanged();

            }
        });
    }


    /**
     * NAVIGATION VIEW SETUP
     */
    private void createNavigationView() {

        final NavigationView navView = findViewById(R.id.nav_view);

        navViewModel.getAllFeeds().observe(this, new Observer<List<Feed>>() {
            @Override
            public void onChanged(@Nullable List<Feed> feeds) {

                if(feeds == null){
                    return;
                }

                Menu menu = navView.getMenu();
                menu.clear();

                SubMenu subMenu = menu.addSubMenu("Feeds");

                for (int i = 0; i < feeds.size(); i++) {
                    subMenu.add(i, Menu.FIRST + i, Menu.FIRST + i, feeds.get(i).getName());
                }

            }
        });

        navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                menuItem.setChecked(true);
                drawerLayout.closeDrawers();

                int feedId = menuItem.getItemId() - 1;

                List<Feed> feeds = navViewModel.getAllFeeds().getValue();
                if(feeds != null && feeds.size() > feedId) {
                    setTitle(feeds.get(feedId).getName());
                    loadNewFeed(feeds.get(feedId).getUrl());
                }

                return true;
            }
        });

        navFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AddFeedActivity.class);
                startActivityForResult(intent, ADD_FEED_INTENT_ID);
            }
        });
    }

    /**
     *  Hides all relevant views and displays the error message
     */
    private void showErrorMessage(int errorMsgResource){
        recyclerView.setVisibility(View.GONE);
        loadingIndicator.setVisibility(View.GONE);
        errorView.setVisibility(View.VISIBLE);
        errorView.setText(errorMsgResource);
    }

    /**
     *  Hides all overlapping views and displays the recycler views
     */
    private void showArticlesList(){
        recyclerView.setVisibility(View.VISIBLE);
        loadingIndicator.setVisibility(View.GONE);
        errorView.setVisibility(View.GONE);
    }

    /**
     * Hides the feed, show the loading bar and tells the viewModel to fetch a new feed
     */
    private void loadNewFeed(String feedUrl){
        recyclerView.setVisibility(View.INVISIBLE);
        loadingIndicator.setVisibility(View.VISIBLE);
        errorView.setVisibility(View.GONE);
        articleViewModel.fetchFeed(feedUrl);
    }
}