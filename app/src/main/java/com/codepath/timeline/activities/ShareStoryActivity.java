package com.codepath.timeline.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ShareActionProvider;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import com.codepath.timeline.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class ShareStoryActivity extends AppCompatActivity {

    @BindView(R.id.wvArticle) WebView webView;
    @BindView(R.id.toolbarDetail) Toolbar toolbar;

    @BindView(R.id.avi)
    com.wang.avi.AVLoadingIndicatorView avi;
    boolean loadingFinished = true;
    boolean redirect = false;


    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_story);
        ButterKnife.bind(this);

        toolbar.setTitle("Share story");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        String storyHTML = getIntent().getStringExtra("storyHTML");

//        startAnim();

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (!loadingFinished) {
                    redirect = true;
                }
                loadingFinished = false;
                view.loadUrl(url);
                return true;
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap facIcon) {
                loadingFinished = false;
                startAnim();
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                if(!redirect){
                    loadingFinished = true;
                }

                if(loadingFinished && !redirect){
                    stopAnim();
                } else {
                    redirect = false;
                }

            }

        });
        webView.loadUrl(storyHTML);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_share_story, menu);
        MenuItem item = menu.findItem(R.id.menu_item_share);
        ShareActionProvider miShareAction = (ShareActionProvider) MenuItemCompat.getActionProvider(item);
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, webView.getUrl());
        miShareAction.setShareIntent(shareIntent);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    void startAnim() {
        avi.show();
    }

    void stopAnim() {
        avi.hide();
    }
}