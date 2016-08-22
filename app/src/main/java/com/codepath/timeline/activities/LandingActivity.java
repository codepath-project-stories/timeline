package com.codepath.timeline.activities;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.codepath.timeline.R;
import com.codepath.timeline.adapters.MyPagerAdapter;
import com.codepath.timeline.util.ParseApplication;
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
//    @BindView(R.id.ivBackgroundImage) ImageView backgroundImage;
//    @BindView(R.id.tvUserName) TextView userName;
//    @BindView(R.id.tvStoriesCount) TextView storiesCount;
//    @BindView(R.id.tvStoriesText) TextView storiesCountText;
//    @BindView(R.id.mainView) RelativeLayout mainView;

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
//        backgroundImage.setImageResource(0);
//        Glide.with(getApplicationContext())
//                .load(R.drawable.background_image)
//                .into(backgroundImage);
//        userName.setText("Jane Smith");
//        storiesCount.setText("20");
//        storiesCountText.setText("STORIES");

        // TODO: production needs to remove the following
        if (ParseApplication.TURN_ON_PARSE && ParseApplication.DEMO_MODE) {
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
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (ParseApplication.TURN_ON_PARSE) {
            new MaterialDialog.Builder(this)
                    .content(getString(R.string.logout))
                    .positiveText(getString(android.R.string.yes))
                    .negativeText(getString(R.string.just_close))
                    .onAny(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog,
                                            @NonNull DialogAction which) {
                            // http://stackoverflow.com/questions/8631095/android-preventing-going-back-to-the-previous-activity
                            if (which.equals(DialogAction.POSITIVE)) {
                                ParseApplication.logout();
                                finish(); // going to kill the activity
                            }
                            else if (which.equals(DialogAction.NEGATIVE)) {
                                moveTaskToBack(true); // Same as if user pressed Home button.
                            }
                        }
                    })
                    .show();
        }
        else {
            moveTaskToBack(true); // Same as if user pressed Home button.
        }
    }
}
