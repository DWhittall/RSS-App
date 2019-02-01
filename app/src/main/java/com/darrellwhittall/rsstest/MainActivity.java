package com.darrellwhittall.rsstest;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
import com.darrellwhittall.rsstest.room.Feed;
import com.prof.rssparser.Article;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ArticleListViewModel articleListViewModel;
    private NavigationViewModel navigationViewModel;

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private DrawerLayout drawerLayout;
    private ProgressBar loadingIndicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Store view member variables.
        recyclerView = findViewById(R.id.rv_articles);
        drawerLayout = findViewById(R.id.drawer_layout);
        loadingIndicator = findViewById(R.id.pb_loading_indicator);

        // Link articleListViewModel to observer
        articleListViewModel = ViewModelProviders.of(this).get(ArticleListViewModel.class);
        articleListViewModel.getArticleList().observe(this, new Observer<List<Article>>() {

            /**
             * When the dataset of articles changes in the viewmodel,
             * Repopulate the recyclerView, show the recyclerView and hide the loadingIndicator
             */
            @Override
            public void onChanged(@Nullable List<Article> articles) {
                if(articles != null){
                    adapter = new ArticleAdapter(articles);
                    recyclerView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();

                    recyclerView.setVisibility(View.VISIBLE);
                    loadingIndicator.setVisibility(View.GONE);
                }
            }
        });

        // Setup RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ArticleAdapter(new ArrayList<Article>());
        recyclerView.setAdapter(adapter);

       // Setup Navigation View
        navigationViewModel = ViewModelProviders.of(this).get(NavigationViewModel.class);
        final NavigationView navigationView = findViewById(R.id.nav_view);

        // Populate the menu whenever the data changes
        navigationViewModel.getAllFeeds().observe(this, new Observer<List<Feed>>() {
            @Override
            public void onChanged(@Nullable List<Feed> feeds) {
                Menu menu = navigationView.getMenu();
                menu.clear();

                SubMenu subMenu = menu.addSubMenu("Feeds");

                for (int i = 0; i < feeds.size(); i++) {
                    subMenu.add(i, Menu.FIRST + i, Menu.FIRST + i, feeds.get(i).getName());
                }

            }
        });

        // Setup "onClick" listener for navigation menu
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                menuItem.setChecked(true);
                drawerLayout.closeDrawers();

                int feedId = menuItem.getItemId() - 1;

                List<Feed> feeds = navigationViewModel.getAllFeeds().getValue();
                if(feeds != null && feeds.size() > feedId)
                    loadNewFeed(feeds.get(feedId).getUrl());

                return true;
            }
        });

        // Only load a default page if the articleListViewModel hasn't tried to load anything yet
        // This means it should be the first time the app has opened since it was last destroyed
        if(articleListViewModel.getState() == ArticleListViewModel.State.BLANK)
            loadNewFeed("http://feeds.feedburner.com/RockPaperShotgun");

    }

    /**
     * Hides the feed, show the loading bar and tells the viewmodel to fetch a new feed
     */
    private void loadNewFeed(final String feedUrl){
        recyclerView.setVisibility(View.INVISIBLE);
        loadingIndicator.setVisibility(View.VISIBLE);
        articleListViewModel.fetchFeed(feedUrl);
    }

}
