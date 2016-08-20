package com.codepath.timeline.activities;


import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.codepath.timeline.R;
import com.codepath.timeline.adapters.MyPagerAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LandingActivity extends AppCompatActivity {
    // LandingActivity creates MyPagerAdapter
    // MyPagerAdapter creates UserStoriesFragment and SharedStoriesFragment
    // UserStoriesFragment extends BaseStoryModelFragment
    // BaseStoryModelFragment calls TimelineActivity

    @BindView(R.id.vpPager) ViewPager viewPager;
    @BindView(R.id.sliding_tabs) TabLayout tabs;
    @BindView(R.id.ivBackgroundImage) ImageView backgroundImage;
    @BindView(R.id.tvUserName) TextView userName;
    @BindView(R.id.tvStoriesCount) TextView storiesCount;
    @BindView(R.id.tvStoriesText) TextView storiesCountText;
    private MyPagerAdapter viewPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing);
        ButterKnife.bind(this);

        viewPagerAdapter = new MyPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(viewPagerAdapter);

        // Give the TabLayout the ViewPager
        tabs.setupWithViewPager(viewPager);

        // setup main views
        backgroundImage.setImageResource(0);
        Glide.with(getApplicationContext())
                .load(R.drawable.background_image)
                .into(backgroundImage);
        userName.setText("Dianne Bautista");
        storiesCount.setText("20");
        storiesCountText.setText("STORIES");

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }
}