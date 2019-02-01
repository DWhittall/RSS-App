package com.darrellwhittall.rsstest.room;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity(tableName = "feed_table")
public class Feed {

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "name")
    private String name;

    @NonNull
    @ColumnInfo(name = "url")
    private String url;

    public Feed(@NonNull String name, @NonNull String url) {
        this.name = name;
        this.url = url;
    }

    @NonNull
    public String getName(){return this.name;}

    @NonNull
    public String getUrl(){return this.url;}
}
