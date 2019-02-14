package com.darrellwhittall.rsstest.room;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;

import java.util.List;

public class FeedRepository {
    final private FeedDao feedDao;
    final private LiveData<List<Feed>> allFeeds;

    public FeedRepository(Application application) {
        FeedRoomDatabase db = FeedRoomDatabase.getDatabase(application);
        feedDao = db.feedDao();
        allFeeds = feedDao.getAllFeeds();
    }

    public LiveData<List<Feed>> getAllFeeds() {
        return allFeeds;
    }

    public void insert (Feed word) {
        new insertAsyncTask(feedDao).execute(word);
    }

    private static class insertAsyncTask extends AsyncTask<Feed, Void, Void> {
        private final FeedDao asyncTaskDao;

        insertAsyncTask(FeedDao dao) {
            asyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final Feed... params) {
            asyncTaskDao.insert(params[0]);
            return null;
        }
    }
}