package com.darrellwhittall.rsstest;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.util.Log;
import com.prof.rssparser.Article;
import com.prof.rssparser.OnTaskCompleted;
import com.prof.rssparser.Parser;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class MainViewModel extends ViewModel {

    // TODO: Decide if this is good practice
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

    public MutableLiveData<List<Article>> getArticleList() {
        if (articleListLive == null) {
            articleListLive = new MutableLiveData<>();
        }
        return articleListLive;
    }

    private void setArticleList(List<Article> articleList){
        this.articleListLive.postValue(articleList);
    }

    public void fetchFeed(final String url){

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
                setArticleList(new ArrayList<Article>());
                Log.e("RSS", "Could not fetch rss feed", e);

                currentState = State.FAILED;
            }
        });

        new Thread(new Runnable() {
            @Override
            public void run() {
                parser.execute(url);
            }
        }).start();
    }

}
