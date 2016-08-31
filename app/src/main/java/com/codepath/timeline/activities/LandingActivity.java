package com.codepath.timeline.activities;


import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.bumptech.glide.Glide;
import com.codepath.timeline.R;
import com.codepath.timeline.adapters.MyPagerAdapter;
import com.codepath.timeline.network.UserClient;
import com.codepath.timeline.util.ParseApplication;
import com.parse.ParseUser;

import butterknife.BindView;
import butterknife.ButterKnife;
import jp.wasabeef.glide.transformations.CropCircleTransformation;

public class LandingActivity extends AppCompatActivity {
    private static final String TAG = LandingActivity.class.getSimpleName();

    // LandingActivity creates MyPagerAdapter
    // MyPagerAdapter creates UserStoriesFragment and SharedStoriesFragment
    // UserStoriesFragment extends BaseStoryModelFragment
    // BaseStoryModelFragment calls TimelineActivity

    @BindView(R.id.vpPager)
    ViewPager viewPager;
    @BindView(R.id.sliding_tabs)
    TabLayout tabBar;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.nvView) NavigationView nvDrawer;
    private ActionBarDrawerToggle drawerToggle;
    @BindView(R.id.drawer_layout)
    DrawerLayout mDrawer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing);
        ButterKnife.bind(this);

        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        setupDrawerContent(nvDrawer);
        drawerToggle = setupDrawerToggle();

        // Tie DrawerLayout events to the ActionBarToggle
        mDrawer.addDrawerListener(drawerToggle);

        setupViewPager();

        ParseUser currentUser = UserClient.getCurrentUser();
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

        // Fill out navigation drawer
        View headerLayout = nvDrawer.inflateHeaderView(R.layout.nav_header);
        ImageView ivHeaderPhoto = (ImageView) headerLayout.findViewById(R.id.ivHeaderImage);
        TextView tvHeaderUserName = (TextView) headerLayout.findViewById(R.id.tvHeaderUserName);
        TextView tvHeaderUserEmail = (TextView) headerLayout.findViewById(R.id.tvHeaderUserEmail);

        ivHeaderPhoto.setImageResource(R.drawable.app_icon);
        if (currentUser.getUsername() != null && currentUser.getEmail() != null) {
            tvHeaderUserName.setText(UserClient.getName(currentUser));
            tvHeaderUserEmail.setText(currentUser.getEmail());
            Glide.with(getApplicationContext()).load(UserClient.getProfileImageUrl(currentUser))
                    .fitCenter()
                    .bitmapTransform(new CropCircleTransformation(getApplicationContext()))
                    .into(ivHeaderPhoto);
        } else {
            Snackbar.make(findViewById(android.R.id.content), "Couldn't load user details", Snackbar.LENGTH_SHORT).show();
            tvHeaderUserName.setText("User User");
            tvHeaderUserEmail.setText("user@gmail.com");
        }
    }

    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        selectDrawerItem(menuItem);
                        return true;
                    }
                });
    }

    private ActionBarDrawerToggle setupDrawerToggle() {
        return new ActionBarDrawerToggle(this, mDrawer, toolbar, R.string.drawer_open,  R.string.drawer_close);
    }

    public void selectDrawerItem(MenuItem menuItem) {
        // Create a new fragment and specify the fragment to show based on nav item clicked
//        Fragment fragment = null;
//        Class fragmentClass;
        switch(menuItem.getItemId()) {
            case R.id.miRecent:
                // Todo: open recent
//                fragmentClass = Recent.class;
                break;
            case R.id.miProfile:
                // Todo: open profile
//                fragmentClass = Profile.class;
                break;
            case R.id.miSettings:
                // Todo: open settings
//                fragmentClass = Settings.class;
                break;
            case R.id.miLogout:
                // Todo: logout
                break;
            default:
        }

//        try {
//            fragment = (Fragment) fragmentClass.newInstance();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

        // Insert the fragment by replacing any existing fragment
//        FragmentManager fragmentManager = getSupportFragmentManager();
//        fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();

        // Highlight the selected item has been done by NavigationView
        menuItem.setChecked(true);
        // Set action bar title
        setTitle(menuItem.getTitle());
        // Close the navigation drawer
        mDrawer.closeDrawers();
    }

    private void setupViewPager() {
        final MyPagerAdapter viewPagerAdapter = new MyPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(viewPagerAdapter);

        // Give the TabLayout the ViewPager
        tabBar.setupWithViewPager(viewPager);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
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

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggles
        drawerToggle.onConfigurationChanged(newConfig);
    }
}
