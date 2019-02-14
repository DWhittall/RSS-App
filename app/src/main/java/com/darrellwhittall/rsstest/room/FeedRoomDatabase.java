package com.darrellwhittall.rsstest.room;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

@Database(entities = {Feed.class}, version = 1, exportSchema = false)
public abstract class FeedRoomDatabase extends RoomDatabase {

    public abstract FeedDao feedDao();

    private static volatile FeedRoomDatabase INSTANCE;

    // Make class a singleton
    static FeedRoomDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (FeedRoomDatabase.class) {
                if (INSTANCE == null) {
                    // Create database here
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            FeedRoomDatabase.class, "feed_database")

                            // Repopulate database on startup
                            .addCallback(roomDatabaseCallback)
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    private final static RoomDatabase.Callback roomDatabaseCallback =
            new RoomDatabase.Callback(){
                @Override
                public void onOpen (@NonNull SupportSQLiteDatabase db){
                    super.onOpen(db);
                    new PopulateDbAsync(INSTANCE).execute();
                }
            };

    private static class PopulateDbAsync extends AsyncTask<Void, Void, Void> {

        private final FeedDao dao;

        PopulateDbAsync(FeedRoomDatabase db) {
            dao = db.feedDao();
        }

        @Override
        protected Void doInBackground(final Void... params) {
            dao.deleteAll();
            Feed feed = new Feed("Rock Paper Shotgun", "http://feeds.feedburner.com/RockPaperShotgun");
            dao.insert(feed);
            feed = new Feed("Android Central", "http://feeds.androidcentral.com/androidcentral");
            dao.insert(feed);
            feed = new Feed("Godot Engine", "https://godotengine.org/rss.xml");
            dao.insert(feed);
            return null;
        }
    }
}