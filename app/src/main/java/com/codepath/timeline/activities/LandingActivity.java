package com.codepath.timeline.activities;


import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.codepath.timeline.R;
import com.codepath.timeline.adapters.MyPagerAdapter;
import com.parse.ParseUser;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LandingActivity extends AppCompatActivity {
    // LandingActivity creates MyPagerAdapter
    // MyPagerAdapter creates UserStoriesFragment and SharedStoriesFragment
    // UserStoriesFragment extends BaseStoryModelFragment
    // BaseStoryModelFragment calls TimelineActivity

    @BindView(R.id.vpPager) ViewPager viewPager;
    @BindView(R.id.sliding_tabs) TabLayout tabBar;
    @BindView(R.id.ivBackgroundImage) ImageView backgroundImage;
    @BindView(R.id.tvUserName) TextView userName;
    @BindView(R.id.tvStoriesCount) TextView storiesCount;
    @BindView(R.id.tvStoriesText) TextView storiesCountText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing);
        ButterKnife.bind(this);

        final MyPagerAdapter viewPagerAdapter = new MyPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(viewPagerAdapter);

        // Give the TabLayout the ViewPager
        tabBar.setupWithViewPager(viewPager);

        // setup main views
        backgroundImage.setImageResource(0);
        Glide.with(getApplicationContext())
                .load(R.drawable.background_image)
                .into(backgroundImage);
        userName.setText("Jane Smith");
        storiesCount.setText("20");
        storiesCountText.setText("STORIES");

        // test LoginActivity
        ParseUser currentUser = ParseUser.getCurrentUser();
        if (currentUser != null) {
            // do stuff with the user
            Log.d("LandingActivity", currentUser.toString());
        } else {
            // show the signup or login screen
            Log.d("LandingActivity", "getCurrentUser failed");
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }
    
    // http://stackoverflow.com/questions/8631095/android-preventing-going-back-to-the-previous-activity
    @Override
    public void onBackPressed() {
        // finish(); // going to kill the activity
        moveTaskToBack(true); // Same as if user pressed Home button.
    }
}
