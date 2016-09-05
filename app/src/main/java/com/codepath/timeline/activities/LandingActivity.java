package com.codepath.timeline.activities;


import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.codepath.timeline.R;
import com.codepath.timeline.adapters.LandingPagerAdapter;
import com.codepath.timeline.models.UserClient;
import com.codepath.timeline.network.ParseApplication;
import com.parse.ParseUser;

import butterknife.BindView;
import butterknife.ButterKnife;
import jp.wasabeef.glide.transformations.CropCircleTransformation;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class LandingActivity extends AppCompatActivity {
    // LandingActivity creates LandingPagerAdapter
    // LandingPagerAdapter creates UserStoriesFragment and SharedStoriesFragment
    // UserStoriesFragment extends BaseStoryModelFragment
    // BaseStoryModelFragment has StoriesAdapter to call TimelineActivity
    // ActivityOptionsCompat.makeSceneTransitionAnimation
    // (Context).startActivity

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

    private static final String TAG = LandingActivity.class.getSimpleName();
    private int[] tabIcons = {R.drawable.my_stories, R.drawable.friends_stories};
    private String tabTitles[] = new String[] { "My stories", "Friends stories"};


    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing);
        ButterKnife.bind(this);

        toolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.white));
        toolbar.setTitleTextAppearance(getApplicationContext(), R.style.MyCustomTabTextAppearance);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        setupDrawerContent(nvDrawer);
        drawerToggle = setupDrawerToggle();

        // Tie DrawerLayout events to the ActionBarToggle
        mDrawer.addDrawerListener(drawerToggle);

        setupViewPager();

        getCurrentUser();

        setupTabBar();

    }

    private void setupTabBar() {
        if (tabBar != null) {
            for (int i = 0; i < tabBar.getTabCount(); i++) {
                tabBar.getTabAt(i).setIcon(tabIcons[i]);
            }
        }

        setActionBarTitle(tabTitles[0]);
        tabBar.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();
                Log.d(TAG, "tab: onTabSelected" + position);
                viewPager.setCurrentItem(position);
                setActionBarTitle(tabTitles[tab.getPosition()]);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
    }

    private void getCurrentUser() {
        ParseUser currentUser = UserClient.getCurrentUser();
        if (currentUser != null) {
            String name = currentUser.getString("name");
            if (name != null) {
                getSupportActionBar().setTitle(name);
            }

            UserClient.printUser(currentUser);
            fillOutDrawer(currentUser);
        } else {
            // show the signup or login screen
            Log.d(TAG, "getCurrentUser failed");
        }
    }

    private void fillOutDrawer(ParseUser currentUser) {
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
            case R.id.miProfile:
                // Todo: open profile
//                fragmentClass = Profile.class;
                break;
            case R.id.miSettings:
                // Todo: open settings
//                fragmentClass = Settings.class;
                break;
            case R.id.miLogout:
                onBackPressed();
                break;
            default:
        }

        /*
        try {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Insert the fragment by replacing any existing fragment
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit(); */

        // Highlight the selected item has been done by NavigationView
        menuItem.setChecked(true);
        // Set action bar title
        setTitle(menuItem.getTitle());
        // Close the navigation drawer
        mDrawer.closeDrawers();
    }

    private void setActionBarTitle(String title){
        if (getSupportActionBar() != null) {
            toolbar.setTitle(title);
            setSupportActionBar(toolbar);
        }
    }

    private void setupViewPager() {
        final LandingPagerAdapter viewPagerAdapter =
                new LandingPagerAdapter(getSupportFragmentManager(), this);
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
            // http://stackoverflow.com/questions/8631095/android-preventing-going-back-to-the-previous-activity
            new AlertDialog.Builder(this)
                    .setMessage(R.string.logout)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            ParseApplication.logout();
                            finish(); // going to kill the activity
                        }
                    })
                    .setNegativeButton(R.string.just_close, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            moveTaskToBack(true); // Same as if user pressed Home button.
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
