package com.darrellwhittall.rsstest;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import com.darrellwhittall.rsstest.room.Feed;
import com.darrellwhittall.rsstest.room.FeedRepository;

import java.util.List;

/**
 * View model for the navigation drawer of the app,
 * handles fetching of subscribed feeds from the FeedRepository
 */
public class NavigationViewModel extends AndroidViewModel {

    private FeedRepository feedRepository;
    private LiveData<List<Feed>> feedListLive;

    public NavigationViewModel(Application application){
        super(application);
        feedRepository = new FeedRepository(application);
        feedListLive = feedRepository.getAllFeeds();
    }

    public LiveData<List<Feed>> getAllFeeds(){ return feedListLive; }

    public void insertFeed(Feed feed){ feedRepository.insert(feed); }
}
