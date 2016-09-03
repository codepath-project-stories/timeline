package com.codepath.timeline.activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.codepath.timeline.R;
import com.codepath.timeline.adapters.MomentsHeaderAdapter;
import com.codepath.timeline.fragments.DetailDialogFragment;
import com.codepath.timeline.models.Moment;
import com.codepath.timeline.network.TimelineClient;
import com.codepath.timeline.network.UserClient;
import com.codepath.timeline.util.AppConstants;
import com.codepath.timeline.util.DateUtil;
import com.codepath.timeline.view.ItemClickSupport;
import com.parse.ParseFile;
import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationRequest;
import com.spotify.sdk.android.authentication.AuthenticationResponse;
import com.spotify.sdk.android.player.Config;
import com.spotify.sdk.android.player.ConnectionStateCallback;
import com.spotify.sdk.android.player.Player;
import com.spotify.sdk.android.player.PlayerNotificationCallback;
import com.spotify.sdk.android.player.PlayerState;
import com.spotify.sdk.android.player.Spotify;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersDecoration;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class TimelineActivity extends AppCompatActivity implements
        PlayerNotificationCallback, ConnectionStateCallback {
    // TimelineActivity calls showDetailDialog() to generate DetailDialogFragment
    // DetailDialogFragment creates R.layout.fragment_moment_detail and ScreenSlidePagerAdapter

    static int SOURCE_MODE = 1;
    // 0: R.drawable.image_test2
    // 1: getIntent().getStringExtra("imageUrl")

    private static final String TAG = "TimelineLog:" + TimelineActivity.class.getSimpleName();
    // TODO: Replace with your client ID
    private static final String CLIENT_ID = "08fae0038f1148a5b60c36db0322805f";
    // TODO: Replace with your redirect URI
    private static final String REDIRECT_URI = "timeline-spotify-integration://callback";
    // Request code that will be passed together with authentication result to the onAuthenticationResult callback
    // Can be any integer
    private static final int REQUEST_CODE = 1337;
    private static final int REFRESH_INTERVAL = 5000;       // default to 5s


    @BindView(R.id.appbar)
    AppBarLayout appbar;
    @BindView(R.id.collapsing_toolbar)
    CollapsingToolbarLayout collapsing_toolbar;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.rvMoments)
    RecyclerView rvMoments;
    @BindView(R.id.rvMomentsChat)
    RecyclerView rvMomentsChat;
    @BindView(R.id.ivAutoPlay)
    ImageView ivAutoPlay;
    @BindView(R.id.miAdd)
    com.github.clans.fab.FloatingActionButton miAdd;
    @BindView(R.id.miMusic)
    com.github.clans.fab.FloatingActionButton miMusic;
    @BindView(R.id.miShare)
    com.github.clans.fab.FloatingActionButton miShare;
    @BindView(R.id.miSwitch)
    com.github.clans.fab.FloatingActionButton miSwitch;
    @BindView(R.id.menu)
    com.github.clans.fab.FloatingActionMenu menu;

    private List<Moment> mMomentList;
    private List<Moment> mMomentChatList;
    private MomentsHeaderAdapter mAdapter;
    private MomentsHeaderAdapter mAdapterChat;
    private int ADD_MOMENT_REQUEST_CODE = 6;
    private String storyObjectId;
    private String storyTitle;
    private String storyBackgroundImageUrl;
    private Handler mMomentsHandler;

    // Zoom in/out
    private ScaleGestureDetector mScaleGestureDetector;
    private int pinch_zoom_index;
    GridLayoutManager gridLayoutManagerChat;
    LinearLayoutManager gridLayoutManager;
    GridLayoutManager gridLayoutManagerTwoColumns;

    private Player mPlayer;

    private Runnable getMomentsRunnable = new Runnable() {
        @Override
        public void run() {
            Log.d(TAG, "Checking for new moments");
            getMomentList();
            mMomentsHandler.postDelayed(getMomentsRunnable, REFRESH_INTERVAL);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);
        ButterKnife.bind(this);

        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // get story info from intent
        // NOTE: Can't pass 'Story' since it's not Parcelable/Serializable
        storyObjectId = getIntent().getStringExtra(AppConstants.OBJECT_ID);
        storyTitle = getIntent().getStringExtra(AppConstants.STORY_TITLE);
        storyBackgroundImageUrl = getIntent().getStringExtra(AppConstants.STORY_BACKGROUND_IMAGE_URL);

        updateStoryInfo();

        initList();
    }

    private void updateStoryInfo() {
        // extract from the intent
        // load the image url for the background of the story into the image view
        if (SOURCE_MODE == 0) {
            collapsing_toolbar.setTitle("Baby Matthew Smith");
            collapsing_toolbar.setCollapsedTitleTextColor(Color.WHITE);
            Glide.with(this)
                    .load(R.drawable.image_test2)
                    .centerCrop()
                    .into(ivAutoPlay);
        } else if (SOURCE_MODE == 1) {
            collapsing_toolbar.setTitle(storyTitle);
            collapsing_toolbar.setCollapsedTitleTextColor(Color.WHITE);
            Glide.with(this)
                    .load(storyBackgroundImageUrl)
                    .centerCrop()
                    .into(ivAutoPlay);
        }
    }

    private void initList() {
        gridLayoutManagerChat = new GridLayoutManager(this, 1); // 1
        gridLayoutManager = new GridLayoutManager(this, 1); // 2
        gridLayoutManagerTwoColumns = new GridLayoutManager(this, 2); // 3

        pinch_zoom_index = 2;

        mMomentChatList = new ArrayList<>();
        mAdapterChat = new MomentsHeaderAdapter(this, mMomentChatList);
        rvMomentsChat.setLayoutManager(gridLayoutManagerChat);
        rvMomentsChat.setAdapter(mAdapterChat);
        // Add the sticky headers decoration
        final StickyRecyclerHeadersDecoration headersDecorChat = new StickyRecyclerHeadersDecoration(mAdapterChat);
        rvMomentsChat.addItemDecoration(headersDecorChat);
        mAdapterChat.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                headersDecorChat.invalidateHeaders();
            }
        });

        mMomentList = new ArrayList<>();
        mAdapter = new MomentsHeaderAdapter(this, mMomentList);
        rvMoments.setLayoutManager(gridLayoutManager);
        rvMoments.setAdapter(mAdapter);
        // Add the sticky headers decoration
        final StickyRecyclerHeadersDecoration headersDecor = new StickyRecyclerHeadersDecoration(mAdapter);
        rvMoments.addItemDecoration(headersDecor);
        mAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                headersDecor.invalidateHeaders();
            }
        });

        getMomentList();

        setupFAB();

        mScaleGestureDetector = new ScaleGestureDetector(
                this,
                new ScaleGestureDetector.SimpleOnScaleGestureListener() {
                    @Override
                    public boolean onScale(ScaleGestureDetector detector) {
                        // TODO: tune pinch zoom for demo
                        if (detector.getCurrentSpan() > 200 && detector.getTimeDelta() > 200) {
                            // if (true) {
                            if (detector.getCurrentSpan() - detector.getPreviousSpan() < -1) {
                                if (pinch_zoom_index == 1) {
                                    rvMoments.setLayoutManager(gridLayoutManager);
                                    pinch_zoom_index = 2;
                                    return true;
                                } else if (pinch_zoom_index == 2) {
                                    rvMoments.setLayoutManager(gridLayoutManagerTwoColumns);
                                    pinch_zoom_index = 3;
                                    return true;
                                }
                            } else if (detector.getCurrentSpan() - detector.getPreviousSpan() > 1) {
                                if (pinch_zoom_index == 3) {
                                    rvMoments.setLayoutManager(gridLayoutManager);
                                    pinch_zoom_index = 2;
                                    return true;
                                } else if (pinch_zoom_index == 2) {
                                    rvMoments.setLayoutManager(gridLayoutManagerChat);
                                    pinch_zoom_index = 1;
                                    return true;
                                }
                            }
                        }
                        return false;
                    }
                });

        ItemClickSupport.addTo(rvMoments).setOnItemClickListener(
                new ItemClickSupport.OnItemClickListener() {
                    @Override
                    public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                        showDetailDialog(position);
                    }
                });
        rvMoments.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mScaleGestureDetector.onTouchEvent(event);
                return false;
            }
        });

        rvMomentsChat.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mScaleGestureDetector.onTouchEvent(event);
                return false;
            }
        });

        mMomentsHandler = new Handler(Looper.getMainLooper());
        mMomentsHandler.postDelayed(getMomentsRunnable, REFRESH_INTERVAL);
    }

    private void getMomentList() {
        TimelineClient.getInstance().getMomentList(storyObjectId,
                new TimelineClient.TimelineClientGetMomentListListener() {
            @Override
            public void onGetMomentList(List<Moment> itemList) {
                mMomentList.clear();
                mMomentList.addAll(itemList);
                // mAdapter.notifyItemRangeInserted(0, mMomentChatList.size());
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onGetMomentChatList(List<Moment> itemList) {
                mMomentChatList.addAll(itemList);
                // mAdapterChat.notifyItemRangeInserted(0, mMomentChatList.size());
                mAdapterChat.notifyDataSetChanged();
            }
        });
    }

    private void showDetailDialog(int position) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        DetailDialogFragment composeDialogFragment = DetailDialogFragment.newInstance(storyObjectId, position);
        composeDialogFragment.show(fragmentManager, "fragment_compose");
    }

    // TODO
    boolean onTwoColumn = true;

    @OnClick(R.id.ivAutoPlay)
    public void onAutoPlay(View view) {
        // TEMPORARY PLACEHOLDER
        Intent intent = new Intent(TimelineActivity.this, AutoPlayActivity.class);
        intent.putExtra(AppConstants.OBJECT_ID, storyObjectId);
        intent.putExtra(AppConstants.STORY_TITLE, storyTitle);
        intent.putExtra(AppConstants.STORY_BACKGROUND_IMAGE_URL, storyBackgroundImageUrl);
        startActivity(intent);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request it is that we're responding to
        if (requestCode == ADD_MOMENT_REQUEST_CODE && resultCode == 1) {
            Moment moment = new Moment();
            moment.setCreatedAtReal(DateUtil.getCurrentDate());
            moment.setDescription(data.getStringExtra(AppConstants.MOMENT_DESCRIPTION));
            moment.setLocation(data.getStringExtra(AppConstants.MOMENT_LOCATION));
            moment.setAuthor(UserClient.getCurrentUser());
            moment.setTempPhotoUri(data.getStringExtra(AppConstants.PHOTO_URI));

            addMoment(moment);
        }

        if (requestCode == REQUEST_CODE) {
            AuthenticationResponse response = AuthenticationClient.getResponse(resultCode, data);
            if (response.getType() == AuthenticationResponse.Type.TOKEN) {
                Config playerConfig = new Config(this, response.getAccessToken(), CLIENT_ID);
                mPlayer = Spotify.getPlayer(playerConfig, this, new Player.InitializationObserver() {
                    @Override
                    public void onInitialized(Player player) {
                        mPlayer.addConnectionStateCallback(TimelineActivity.this);
                        mPlayer.addPlayerNotificationCallback(TimelineActivity.this);
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
    public void onPlaybackEvent(EventType eventType, PlayerState playerState) {
        Log.d("MainActivity", "Playback event received: " + eventType.name());
        switch (eventType) {
            // Handle event type as necessary
            default:
                break;
        }
    }

    @Override
    public void onPlaybackError(ErrorType errorType, String errorDetails) {
        Log.d("MainActivity", "Playback error received: " + errorType.name());
        switch (errorType) {
            // Handle error type as necessary
            default:
                break;
        }
    }

    private void addMoment(final Moment moment) {
        TimelineClient.getInstance().uploadFile("photo.jpg", moment.getTempPhotoUri(),
                new TimelineClient.TimelineClientUploadFileListener() {
            @Override
            public void onUploadFileListener(ParseFile file) {
                moment.setMediaUrl(file.getUrl());
                moment.setMediaFile(file);
                TimelineClient.getInstance().addMoment(moment, storyObjectId);
            }
        });

        Log.d(TAG, "Adding to recyclerview");

        // add to top
        mMomentList.add(0, moment);
        mAdapter.notifyDataSetChanged();

        rvMoments.smoothScrollToPosition(0);

        // TODO: do we need this?
        // Close the fab menu
        menu.close(true);

        Log.d(TAG, "Finished adding to recyclerview");
    }

    @Override
    protected void onDestroy() {
        if (mMomentsHandler != null) {
            mMomentsHandler.removeCallbacks(getMomentsRunnable);
        }

        // VERY IMPORTANT! This must always be called or else you will leak resources
        Spotify.destroyPlayer(this);
        super.onDestroy();
    }

    private void alphaAnimationCreator(final View view, final boolean isfadeIn) {
        final float alpha = isfadeIn?0.0f:1.0f;

        view.setVisibility(View.VISIBLE);
        AlphaAnimation fade = new AlphaAnimation(alpha, 1-alpha);
        fade.setAnimationListener(
                new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        view.setVisibility(isfadeIn?View.VISIBLE:View.INVISIBLE);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {
                    }
                }
        );
        fade.setDuration(1000);
        view.startAnimation(fade);
    }

    void setupFAB() {
        menu.setClosedOnTouchOutside(true);

        miAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Close the fab menu
                menu.close(true);

                Intent intent = new Intent(TimelineActivity.this, NewMomentActivity.class);
                startActivityForResult(intent, ADD_MOMENT_REQUEST_CODE);
            }
        });

        miMusic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Close the fab menu
                menu.close(true);

                if (Integer.parseInt(v.getTag().toString()) == 1) {
                    v.setTag(2);
                    // Todo: add spotify integration
                    AuthenticationRequest.Builder builder =
                            new AuthenticationRequest.Builder(CLIENT_ID, AuthenticationResponse.Type.TOKEN, REDIRECT_URI);
                    builder.setScopes(new String[]{"user-read-private", "streaming"});
                    AuthenticationRequest request = builder.build();
                    AuthenticationClient.openLoginActivity(TimelineActivity.this, REQUEST_CODE, request);
                    miMusic.setLabelText("Unspotify story");
                } else {
                    v.setTag(1);
                    mPlayer.pause();
                    miMusic.setLabelText("Spotify story");
                }
            }
        });

        miShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Close the fab menu
                menu.close(true);

                // check if coming from user's stories - enable sharing
                // if not - alert and disable
                String code = getIntent().getStringExtra("code");
                Snackbar.make(findViewById(android.R.id.content),
                        code,
                        Snackbar.LENGTH_SHORT).show();
                // Todo: getting name doesn't quite work
                if (code.contains("UsersStoriesFragment")) {
                    // Todo: add sharing functionality
                } else {
//                    Snackbar.make(findViewById(android.R.id.content),
//                            "Bummer, but you can't share your friend's stories!",
//                            Snackbar.LENGTH_SHORT).show();
                }
            }
        });

        miSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Close the fab menu
                menu.close(true);

                onTwoColumn = !onTwoColumn;
                alphaAnimationCreator(rvMoments, onTwoColumn);
            }
        });
    }
}
