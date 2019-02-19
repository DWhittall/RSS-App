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
class FeedViewModel extends AndroidViewModel {

    private final FeedRepository feedRepository;
    private final LiveData<List<Feed>> feedListLive;

    public FeedViewModel(Application application){
        super(application);
        feedRepository = new FeedRepository(application);
        feedListLive = feedRepository.getAllFeeds();
    }

    LiveData<List<Feed>> getAllFeeds(){ return feedListLive; }

    void insertFeed(Feed feed){ feedRepository.insert(feed); }
}
