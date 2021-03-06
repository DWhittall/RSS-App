package com.darrellwhittall.rsstest;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.util.Log;
import com.darrellwhittall.rsstest.room.Feed;
import com.prof.rssparser.Article;
import com.prof.rssparser.OnTaskCompleted;
import com.prof.rssparser.Parser;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ArticleViewModel extends ViewModel {

    private Feed currentFeed;

    // TODO: Decide if this is good practice,
    //  probably remove it and load last opened feed instead
    public enum State {
        BLANK,
        LOADED,
        LOADING,
        FAILED
    }

    private State currentState = State.BLANK;

    private MutableLiveData<List<Article>> articleListLive = null;

    public State getState(){
        return currentState;
    }

    public Feed getCurrentFeed() { return currentFeed; }

    MutableLiveData<List<Article>> getArticleList() {
        if (articleListLive == null) {
            articleListLive = new MutableLiveData<>();
        }
        return articleListLive;
    }

    private void setArticleList(List<Article> articleList){
        this.articleListLive.postValue(articleList);
    }

    void fetchFeed(final Feed feed){

        currentFeed = feed;
        currentState = State.LOADING;

        final Parser parser = new Parser();

        parser.onFinish(new OnTaskCompleted() {

            @Override
            public void onTaskCompleted(@NotNull final List<Article> fetchedArticles) {
                setArticleList(fetchedArticles);

                currentState = State.LOADED;
            }

            @Override
            public void onError(@NotNull Exception e) {
                setArticleList(null);
                Log.e("RSS", "Could not fetch rss feed", e);

                currentState = State.FAILED;
            }
        });

        // Todo: Keep a thread and kill it if another one is started
        new Thread(() -> parser.execute(feed.getUrl())).start();
    }

}
