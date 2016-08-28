package com.codepath.timeline.activities;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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
    private static final String TAG = LandingActivity.class.getSimpleName();

    // LandingActivity creates MyPagerAdapter
    // MyPagerAdapter creates UserStoriesFragment and SharedStoriesFragment
    // UserStoriesFragment extends BaseStoryModelFragment
    // BaseStoryModelFragment calls TimelineActivity

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.vpPager)
    ViewPager viewPager;
    @BindView(R.id.sliding_tabs)
    TabLayout tabBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing);
        ButterKnife.bind(this);

        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        setupViewPager();

        ParseUser currentUser = ParseUser.getCurrentUser();
        if (currentUser != null) {
            String name = currentUser.getString("name");
            if (name != null) {
                getSupportActionBar().setTitle(name);
            }

            StringBuilder str = new StringBuilder();
            str.append("\nobjectId=").append(currentUser.getObjectId());
            str.append("\ncreatedAt=").append(currentUser.getCreatedAt().toString());
            str.append("\nuserName=").append(currentUser.getUsername());
            str.append("\nemail=").append(currentUser.getEmail());
            Log.d(TAG, "CurrentUser=" + str);

        } else {
            // show the signup or login screen
            Log.d("LandingActivity", "getCurrentUser failed");
        }
    }

    private void setupViewPager() {
        final MyPagerAdapter viewPagerAdapter = new MyPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(viewPagerAdapter);

        // Give the TabLayout the ViewPager
        tabBar.setupWithViewPager(viewPager);
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
                            } else if (which.equals(DialogAction.NEGATIVE)) {
                                moveTaskToBack(true); // Same as if user pressed Home button.
                            }
                        }
                    })
                    .show();
        } else {
            moveTaskToBack(true); // Same as if user pressed Home button.
        }
    }
}
