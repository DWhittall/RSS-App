package com.darrellwhittall.rsstest.room;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface FeedDao {
    @Insert
    void insert(Feed feed);

    @Query("DELETE FROM feed_table")
    void deleteAll();

    @Query("SELECT * from feed_table ORDER BY name ASC")
    LiveData<List<Feed>> getAllFeeds();
}
