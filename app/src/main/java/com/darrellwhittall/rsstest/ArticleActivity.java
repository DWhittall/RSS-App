package com.darrellwhittall.rsstest;

import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;
import com.squareup.picasso.Picasso;

public class ArticleActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article);

        TextView titleView = findViewById(R.id.tv_article_title);
        ImageView imageView = findViewById(R.id.iv_article_image);
        TextView authorDateView = findViewById(R.id.tv_article_author);
        TextView contentView = findViewById(R.id.wv_article_content);

        Bundle extras = getIntent().getExtras();
        if(extras == null) {
            finish();
            return;
        }

        titleView.setText(extras.getString("Title"));

        Picasso.get()
                .load(extras.getString("Image"))
                .placeholder(R.drawable.ic_launcher_background)
                .into(imageView);

        authorDateView.setText(String.format("%s : %s",
                extras.getString("Author"), extras.getString("Date")));

        if(extras.get("Content") != null) {
            contentView.setText(
                    Html.fromHtml(extras.getString("Content"), null, null));
            contentView.setMovementMethod(LinkMovementMethod.getInstance());
        }
    }
}
