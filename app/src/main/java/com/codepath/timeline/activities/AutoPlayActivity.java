package com.codepath.timeline.activities;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.util.Pair;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.transition.Explode;
import android.util.Log;
import android.view.View;

import com.codepath.timeline.R;
import com.codepath.timeline.fragments.AutoPlayFragment;
import com.codepath.timeline.models.Moment;
import com.codepath.timeline.network.TimelineClient;
import com.codepath.timeline.util.AppConstants;
import com.qslll.library.ExpandingPagerFactory;
import com.qslll.library.ExpandingViewPagerAdapter;
import com.qslll.library.fragments.ExpandingFragment;
import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationResponse;
import com.spotify.sdk.android.player.Config;
import com.spotify.sdk.android.player.ConnectionStateCallback;
import com.spotify.sdk.android.player.Player;
import com.spotify.sdk.android.player.PlayerNotificationCallback;
import com.spotify.sdk.android.player.PlayerState;
import com.spotify.sdk.android.player.Spotify;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.trinea.android.view.autoscrollviewpager.AutoScrollViewPager;

/*
      Expanding view for displaying list of moments: https://github.com/qs-lll/ExpandingPager
      Automatic scroll used for auto-playing list of moments: https://github.com/Trinea/android-auto-scroll-view-pager
 */
public class AutoPlayActivity extends AppCompatActivity
        implements ExpandingFragment.OnExpandingClickListener,
        PlayerNotificationCallback, ConnectionStateCallback {

    @BindView(R.id.vpMoment)
    AutoScrollViewPager vpMoment;
//    @BindView(R.id.collapsing_toolbar)
//    CollapsingToolbarLayout collapsing_toolbar;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
//    @BindView(R.id.ivStoryBackground)
//    ImageView ivStoryBackground;

    private List<Moment> mMomentList;
    private Moment mMoment;
    private AutoPlayPagerAdapter mPagerAdapter;
    private String storyObjectId;
    private String storyTitle;
    private String storyBackgroundImageUrl;

    // TODO: Replace with your client ID
    private static final String CLIENT_ID = "08fae0038f1148a5b60c36db0322805f";
    // TODO: Replace with your redirect URI
    private static final String REDIRECT_URI = "timeline-spotify-integration://callback";

    // Request code that will be passed together with authentication result to the onAuthenticationResult callback
    // Can be any integer
    private static final int REQUEST_CODE = 1337;

    private Player mPlayer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auto_play);
        ButterKnife.bind(this);

        // get story info from intent
        // NOTE: Can't pass 'Story' since it's not Parcelable/Serializable
        storyObjectId = getIntent().getStringExtra(AppConstants.OBJECT_ID);
        storyTitle = getIntent().getStringExtra(AppConstants.STORY_TITLE);
        storyBackgroundImageUrl = getIntent().getStringExtra(AppConstants.STORY_BACKGROUND_IMAGE_URL);

        toolbar.setTitle(storyTitle);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Todo: remove this if decided to leave spotify in the timeline activity
//        AuthenticationRequest.Builder builder =
//                new AuthenticationRequest.Builder(CLIENT_ID, AuthenticationResponse.Type.TOKEN, REDIRECT_URI);
//        builder.setScopes(new String[]{"user-read-private", "streaming"});
//        AuthenticationRequest request = builder.build();

        // Todo: two options - either play from inside the timeline activity or during the autoplay
//        AuthenticationClient.openLoginActivity(this, REQUEST_CODE, request);

        updateStoryInfo();
        getMomentList();
    }

    private void updateStoryInfo() {
//        collapsing_toolbar.setTitle(storyTitle);
//        collapsing_toolbar.setCollapsedTitleTextColor(Color.WHITE);
//        Glide.with(this)
//                .load(storyBackgroundImageUrl)
//                .fitCenter()
//                .into(ivStoryBackground);
    }

    private void initList() {
        mPagerAdapter = new AutoPlayPagerAdapter(getSupportFragmentManager());
        vpMoment.setAdapter(mPagerAdapter);

        ExpandingPagerFactory.setupViewPager(vpMoment);
        vpMoment.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                ExpandingFragment expandingFragment = ExpandingPagerFactory.getCurrentFragment(vpMoment);
                if (expandingFragment != null && expandingFragment.isOpenend()) {
                    expandingFragment.close();
                }
            }

            @Override
            public void onPageSelected(int position) {
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        // initialize viewpager for automatically scrolling through the list of moments
        vpMoment.setInterval(5000);
        vpMoment.startAutoScroll();
        vpMoment.setCurrentItem(0);
    }

    private void getMomentList() {
        TimelineClient.getInstance().getMomentList(storyObjectId, new TimelineClient.TimelineClientGetMomentListListener() {
            @Override
            public void onGetMomentList(List<Moment> itemList) {
                mMomentList = new ArrayList<>();
                mMomentList.addAll(itemList);
                initList();
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (!ExpandingPagerFactory.onBackPressed(vpMoment)) {
            super.onBackPressed();
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void setupWindowAnimations() {
        Explode slideTransition = new Explode();
        getWindow().setReenterTransition(slideTransition);
        getWindow().setExitTransition(slideTransition);
    }

    @SuppressWarnings("unchecked")
    private void startInfoActivity(View view, Moment moment) {
        Activity activity = this;
        ActivityCompat.startActivity(activity,
                AutoPlayInfoActivity.newInstance(activity, moment),
                ActivityOptionsCompat.makeSceneTransitionAnimation(
                        activity,
                        new Pair<>(view, getString(R.string.transition_image)))
                        .toBundle());
    }

    @Override
    public void onExpandingClick(View v) {
        //v is expanding fragment layout
        View view = v.findViewById(R.id.ivMedia);
        Moment moment = mMomentList.get(vpMoment.getCurrentItem());
        startInfoActivity(view, moment);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        // Check if result comes from the correct activity
        if (requestCode == REQUEST_CODE) {
            AuthenticationResponse response = AuthenticationClient.getResponse(resultCode, intent);
            if (response.getType() == AuthenticationResponse.Type.TOKEN) {
                Config playerConfig = new Config(this, response.getAccessToken(), CLIENT_ID);
                mPlayer = Spotify.getPlayer(playerConfig, this, new Player.InitializationObserver() {
                    @Override
                    public void onInitialized(Player player) {
                        mPlayer.addConnectionStateCallback(AutoPlayActivity.this);
                        mPlayer.addPlayerNotificationCallback(AutoPlayActivity.this);
                        mPlayer.play("spotify:track:2TpxZ7JUBn3uw46aR7qd6V");
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        Log.e("MainActivity", "Could not initialize player: " + throwable.getMessage());
                    }
                });
            }
        }
    }

    @Override
    public void onLoggedIn() {
        Log.d("MainActivity", "User logged in");
    }

    @Override
    public void onLoggedOut() {
        Log.d("MainActivity", "User logged out");
    }

    @Override
    public void onLoginFailed(Throwable error) {
        Log.d("MainActivity", "Login failed");
    }

    @Override
    public void onTemporaryError() {
        Log.d("MainActivity", "Temporary error occurred");
    }

    @Override
    public void onConnectionMessage(String message) {
        Log.d("MainActivity", "Received connection message: " + message);
    }

    @Override
    public void onPlaybackEvent(PlayerNotificationCallback.EventType eventType, PlayerState playerState) {
        Log.d("MainActivity", "Playback event received: " + eventType.name());
        switch (eventType) {
            // Handle event type as necessary
            default:
                break;
        }
    }

    @Override
    public void onPlaybackError(PlayerNotificationCallback.ErrorType errorType, String errorDetails) {
        Log.d("MainActivity", "Playback error received: " + errorType.name());
        switch (errorType) {
            // Handle error type as necessary
            default:
                break;
        }
    }

    @Override
    protected void onDestroy() {
        // VERY IMPORTANT! This must always be called or else you will leak resources
        Spotify.destroyPlayer(this);
        super.onDestroy();
    }

    private class AutoPlayPagerAdapter extends ExpandingViewPagerAdapter {
        public AutoPlayPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            mMoment = mMomentList.get(position);
            return AutoPlayFragment.newInstance(mMoment.getObjectId());
        }

        @Override
        public int getCount() {
            return mMomentList.size();
        }
    }
}
