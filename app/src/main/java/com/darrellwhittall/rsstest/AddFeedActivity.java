package com.darrellwhittall.rsstest;

import android.arch.lifecycle.ViewModelProviders;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import com.darrellwhittall.rsstest.room.Feed;

public class AddFeedActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_feed);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        }

        EditText feedTitleText = findViewById(R.id.et_feed_title);
        EditText feedUrlText = findViewById(R.id.et_feed_url);
        Button confirmButton = findViewById(R.id.btn_confirm);


        Bundle extras = getIntent().getExtras();
        if(extras != null) {
            if(extras.containsKey("FeedTitle") && extras.containsKey("FeedURL")) {
                feedTitleText.setText(extras.getString("FeedTitle"));
                feedUrlText.setText(extras.getString("FeedURL"));

                actionBar.setTitle(R.string.edit_feed);
            }
        }

        FeedViewModel feedViewModel = ViewModelProviders.of(this).get(FeedViewModel.class);

        confirmButton.setOnClickListener(v -> {
            String title = feedTitleText.getText().toString();
            String url = feedUrlText.getText().toString();

            feedViewModel.insertFeed(new Feed(title, url));
            finish();
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return false;
    }

}
