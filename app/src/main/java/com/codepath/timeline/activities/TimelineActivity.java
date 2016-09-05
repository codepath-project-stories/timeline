package com.codepath.timeline.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
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
import com.codepath.timeline.network.ParseApplication;
import com.codepath.timeline.network.TimelineClient;
import com.codepath.timeline.models.UserClient;
import com.codepath.timeline.util.AppConstants;
import com.codepath.timeline.util.DateUtil;
import com.codepath.timeline.view.ItemClickSupport;
import com.codepath.timeline.view.SpacesItemDecoration;
import com.github.clans.fab.FloatingActionMenu;
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
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class TimelineActivity extends AppCompatActivity implements
        PlayerNotificationCallback, ConnectionStateCallback {
    // TimelineActivity calls showDetailDialog() to generate DetailDialogFragment
    // DetailDialogFragment creates R.layout.fragment_moment_detail and ScreenSlidePagerAdapter

    static int SOURCE_MODE = 1;
    // 0: R.drawable.image_test2
    // 1: getIntent().getStringExtra("imageUrl")

    private static final String TAG = TimelineActivity.class.getSimpleName();
    private static final String CLIENT_ID = "08fae0038f1148a5b60c36db0322805f";
    private static final String REDIRECT_URI = "timeline-spotify-integration://callback";
    // Request code that will be passed together with authentication result to the onAuthenticationResult callback
    private static final int REQUEST_CODE = 1337;

    @BindView(R.id.appbar)
    AppBarLayout appbar;
    @BindView(R.id.collapsing_toolbar)
    CollapsingToolbarLayout collapsing_toolbar;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.rvMomentsTwoColumns)
    RecyclerView rvMomentsTwoColumns;
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
    private MomentsHeaderAdapter mAdapterTwoColumns;
    private int ADD_MOMENT_REQUEST_CODE = 6;
    private String storyObjectId;
    private String storyTitle;
    private String storyBackgroundImageUrl;
    private String storyHtmlSummaryUrl;
    private Handler mMomentsHandler;

    // Zoom in/out
    private ScaleGestureDetector mScaleGestureDetector;
    private int pinch_zoom_index;
    LinearLayoutManager layoutManagerChat;
    GridLayoutManager layoutManager;
    GridLayoutManager layoutManagerTwoColumns;
    boolean showDefaultView = true;
    boolean showTwoColumns = true;
    boolean lock;

    private Player mPlayer;


    // TODO: use push notification instead of pulling and auto refresh every few seconds
    private Runnable getMomentsRunnable = new Runnable() {
        @Override
        public void run() {
            Log.d(TAG, "Checking for new moments");
            getMomentList();
            mMomentsHandler.postDelayed(getMomentsRunnable, ParseApplication.REFRESH_INTERVAL);
        }
    };

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

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
        storyHtmlSummaryUrl = getIntent().getStringExtra(AppConstants.STORY_HTML_SUMMARY_URL);

        updateStoryInfo();

        initList();
    }

    private void updateStoryInfo() {
        collapsing_toolbar.setTitle(storyTitle);
        Glide.with(this)
                .load(storyBackgroundImageUrl)
                .centerCrop()
                .into(ivAutoPlay);
    }

    private void initList() {

        // the order of the following is very important

        setupRecyclerView();

        getMomentList();

        setupFAB();

        setupGesture();

        mMomentsHandler = new Handler(Looper.getMainLooper());
        mMomentsHandler.postDelayed(getMomentsRunnable, ParseApplication.REFRESH_INTERVAL);
    }

    private void setupRecyclerView() {
        pinch_zoom_index = 2;
        showDefaultView = true;
        showTwoColumns = false;
        lock = false;

        layoutManagerChat = new LinearLayoutManager(this); // pinch_zoom_index 1
        layoutManager = new GridLayoutManager(this, 1); // pinch_zoom_index 2
        layoutManagerTwoColumns = new GridLayoutManager(this, 2); // pinch_zoom_index 3

        // TODO: need to implement a input text box for chat
        // TODO: try setReverseLayout()
        // http://stackoverflow.com/questions/26580723/how-to-scroll-to-the-bottom-of-a-recyclerview-scrolltoposition-doesnt-work
        // http://stackoverflow.com/questions/27727354/linearlayoutmanager-setreverselayout-true-but-items-stack-from-bottom
        // layoutManagerChat.setReverseLayout(true);
        mMomentChatList = new ArrayList<>();
        mAdapterChat = new MomentsHeaderAdapter(this, mMomentChatList, 1);
        rvMomentsChat.setLayoutManager(layoutManagerChat);
        rvMomentsChat.setAdapter(mAdapterChat);
        // Add the sticky headers decoration
        final StickyRecyclerHeadersDecoration headersDecorChat =
                new StickyRecyclerHeadersDecoration(mAdapterChat);
        rvMomentsChat.addItemDecoration(headersDecorChat);
        mAdapterChat.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                headersDecorChat.invalidateHeaders();
            }
        });

        mMomentList = new ArrayList<>();
        mAdapter = new MomentsHeaderAdapter(this, mMomentList, 2);
        rvMoments.setLayoutManager(layoutManager);
        rvMoments.setAdapter(mAdapter);
        // Add the sticky headers decoration
        final StickyRecyclerHeadersDecoration headersDecor =
                new StickyRecyclerHeadersDecoration(mAdapter);
        rvMoments.addItemDecoration(headersDecor);
        mAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                headersDecor.invalidateHeaders();
            }
        });

        mAdapterTwoColumns = new MomentsHeaderAdapter(this, mMomentList, 3);
        rvMomentsTwoColumns.setLayoutManager(layoutManagerTwoColumns);
        rvMomentsTwoColumns.setAdapter(mAdapterTwoColumns);
        rvMomentsTwoColumns.addItemDecoration(new SpacesItemDecoration(20));
    }

    // TODO: now zoom in zoom out doesn't look like sony/album or google/photo
    // TODO: the future work needs
    // TODO: 1. different states for zoom in zoom out
    // TODO: 2. current view size should respond to pinch difference (now size doesn't change at all)
    private void setupGesture() {
        mScaleGestureDetector = new ScaleGestureDetector(
                this,
                new ScaleGestureDetector.SimpleOnScaleGestureListener() {
                    @Override
                    public boolean onScale(ScaleGestureDetector detector) {
                        if (!lock) {
                            // TODO: tune pinch zoom for demo
                            if (true) { // no threshold, too sensitive, but good for emulator
                                // 50
                                // 100 // i prefer this
                                // 200
                                // if (detector.getCurrentSpan() > 100 && detector.getTimeDelta() > 100) {
                                if (detector.getCurrentSpan() - detector.getPreviousSpan() < -1) {
                                    // pinch close
                                    if (pinch_zoom_index == 1) {
                                        lock = true;
                                        pinch_zoom_index = 2;
                                        showDefaultView = !showDefaultView;
                                        alphaAnimationCreator(rvMoments, showDefaultView);
                                        return true;
                                    } else if (pinch_zoom_index == 2) {
                                        lock = true;
                                        pinch_zoom_index = 3;
                                        showTwoColumns = !showTwoColumns;
                                        alphaAnimationCreator(rvMomentsTwoColumns, showTwoColumns);
                                        return true;
                                    }
                                } else if (detector.getCurrentSpan() - detector.getPreviousSpan() > 1) {
                                    // pinch away
                                    if (pinch_zoom_index == 3) {
                                        lock = true;
                                        pinch_zoom_index = 2;
                                        showTwoColumns = !showTwoColumns;
                                        alphaAnimationCreator(rvMomentsTwoColumns, showTwoColumns);
                                        return true;
                                    } else if (pinch_zoom_index == 2) {
                                        lock = true;
                                        pinch_zoom_index = 1;
                                        showDefaultView = !showDefaultView;
                                        alphaAnimationCreator(rvMoments, showDefaultView);
                                        return true;
                                    }
                                }
                            }
                        }
                        return false;
                    }
                });

        setupGestureOnRecyclerView(rvMomentsChat, true);
        setupGestureOnRecyclerView(rvMoments, false);
        setupGestureOnRecyclerView(rvMomentsTwoColumns, false);
    }

    private void setupGestureOnRecyclerView(RecyclerView rv, final boolean isChat) {
        // onClicked != onItemClicked
        ItemClickSupport.addTo(rv).setOnItemClickListener(
                new ItemClickSupport.OnItemClickListener() {
                    @Override
                    public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                        showDetailDialogWithAnimation(position, isChat);
                    }
                });

        rv.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mScaleGestureDetector.onTouchEvent(event);
                return false;
            }
        });
    }

    private void getMomentList() {
        TimelineClient.getInstance().getMomentList(storyObjectId,
                new TimelineClient.TimelineClientGetMomentListListener() {
            @Override
            public void onGetMomentList(List<Moment> itemList) {
                // TODO: only need to add new items instead of clear()
                // DB: Be careful when using 'notifyItemRangeInserted', I ran into an issue where the RecyclerView
                // would throw an error like "Inconsistency detected. Invalid view holder adapter"
                // Though less efficient, replacing it with 'notifyDataSetChanged' solved it
                mMomentList.clear();
                mMomentList.addAll(itemList);
                // mAdapter.notifyItemRangeInserted(0, mMomentChatList.size());
                mAdapter.notifyDataSetChanged();
                mAdapterTwoColumns.notifyDataSetChanged();
            }

            @Override
            public void onGetMomentChatList(List<Moment> itemList) {
                // TODO: only need to add new items instead of clear()
                mMomentChatList.clear();
                mMomentChatList.addAll(itemList);
                // mAdapterChat.notifyItemRangeInserted(0, mMomentChatList.size());
                mAdapterChat.notifyDataSetChanged();
                rvMomentsChat.scrollToPosition(mMomentChatList.size() - 1);
            }
        });
    }

    private void showDetailDialogWithAnimation(final int position, final boolean isChat) {
        if (menu.isOpened()) {
            menu.setOnMenuToggleListener(new FloatingActionMenu.OnMenuToggleListener() {
                @Override
                public void onMenuToggle(boolean opened) {
                    menu.setOnMenuToggleListener(null);
                    if (!opened) {
                        showDetailDialog(position, isChat);
                    }
                }
            });
            menu.close(true);
        }
        else {
            showDetailDialog(position, isChat);
        }
    }

    private void showDetailDialog(int position, boolean isChat) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        DetailDialogFragment composeDialogFragment =
                DetailDialogFragment.newInstance(TimelineActivity.this, storyObjectId, position, isChat);
        composeDialogFragment.show(fragmentManager, "fragment_compose");
    }

    @OnClick(R.id.ivAutoPlay)
    public void onAutoPlayWithAnimation(final View view) {
        if (menu.isOpened()) {
            menu.setOnMenuToggleListener(new FloatingActionMenu.OnMenuToggleListener() {
                @Override
                public void onMenuToggle(boolean opened) {
                    menu.setOnMenuToggleListener(null);
                    if (!opened) {
                        onAutoPlay(view);
                    }
                }
            });
            menu.close(true);
        }
        else {
            onAutoPlay(view);
        }
    }

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
        Log.d(TAG, "User logged in");
    }

    @Override
    public void onLoggedOut() {
        Log.d(TAG, "User logged out");
    }

    @Override
    public void onLoginFailed(Throwable error) {
        Log.d(TAG, "Login failed");
    }

    @Override
    public void onTemporaryError() {
        Log.d(TAG, "Temporary error occurred");
    }

    @Override
    public void onConnectionMessage(String message) {
        Log.d(TAG, "Received connection message: " + message);
    }

    @Override
    public void onPlaybackEvent(EventType eventType, PlayerState playerState) {
        Log.d(TAG, "Playback event received: " + eventType.name());
        switch (eventType) {
            // Handle event type as necessary
            default:
                break;
        }
    }

    @Override
    public void onPlaybackError(ErrorType errorType, String errorDetails) {
        Log.d(TAG, "Playback error received: " + errorType.name());
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

    private void alphaAnimationCreator(final View view, final boolean visible) {
        final float alpha = visible?0.0f:1.0f;

        view.setVisibility(View.VISIBLE);
        AlphaAnimation fade = new AlphaAnimation(alpha, 1-alpha);
        fade.setAnimationListener(
                new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        lock = false;
                        view.setVisibility(visible?View.VISIBLE:View.INVISIBLE);
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
                menu.setOnMenuToggleListener(new FloatingActionMenu.OnMenuToggleListener() {
                    @Override
                    public void onMenuToggle(boolean opened) {
                        menu.setOnMenuToggleListener(null);
                        if (!opened) {
                            Intent intent = new Intent(TimelineActivity.this, NewMomentActivity.class);
                            startActivityForResult(intent, ADD_MOMENT_REQUEST_CODE);
                        }
                    }
                });
                menu.close(true);
            }
        });

        miMusic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                menu.setOnMenuToggleListener(new FloatingActionMenu.OnMenuToggleListener() {
                    @Override
                    public void onMenuToggle(boolean opened) {
                        menu.setOnMenuToggleListener(null);
                        if (!opened) {
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
                                if (mPlayer != null) {
                                    mPlayer.pause();
                                }
                                miMusic.setLabelText("Spotify story");
                            }
                        }
                    }
                });
                menu.close(true);
            }
        });

        miShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                menu.setOnMenuToggleListener(new FloatingActionMenu.OnMenuToggleListener() {
                    @Override
                    public void onMenuToggle(boolean opened) {
                        menu.setOnMenuToggleListener(null);
                        if (!opened) {
                            // check if coming from user's stories - enable sharing
                            // if not - alert and disable
                            String code = getIntent().getStringExtra("code");
//                Snackbar.make(findViewById(android.R.id.content),
//                        code,
//                        Snackbar.LENGTH_SHORT).show();

                            if (storyHtmlSummaryUrl != null) {
                                Intent intent = new Intent(TimelineActivity.this, ShareStoryActivity.class);
                                intent.putExtra(AppConstants.STORY_HTML_SUMMARY_URL, storyHtmlSummaryUrl);
                                intent.putExtra(AppConstants.STORY_TITLE, storyTitle);
                                startActivity(intent);
                            }

                            // Todo: getting name doesn't quite work
                            if (code.contains("UsersStoriesFragment")) {
                                // Todo: add sharing functionality

                            } else {
//                    Snackbar.make(findViewById(android.R.id.content),
//                            "Bummer, but you can't share your friend's stories!",
//                            Snackbar.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
                menu.close(true);
            }
        });

        miSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Close the fab menu
                menu.close(true);

                if (pinch_zoom_index == 1) {
                    reverseRecyclerView(rvMomentsChat, mAdapterChat, mMomentChatList);
                }
                else if (pinch_zoom_index == 2) {
                    reverseRecyclerView(rvMoments, mAdapter, mMomentList);
                    mAdapterTwoColumns.notifyDataSetChanged();
                    // rvMomentsTwoColumns.scrollToPosition(0);
                }
                else if (pinch_zoom_index == 3) {
                    reverseRecyclerView(rvMomentsTwoColumns, mAdapterTwoColumns, mMomentList);
                    mAdapter.notifyDataSetChanged();
                    // rvMoments.scrollToPosition(0);
                }
            }
        });
    }

    private void reverseRecyclerView(RecyclerView rv,
                                     final MomentsHeaderAdapter adapter,
                                     List<Moment> list) {
        // http://stackoverflow.com/questions/26682277/how-do-i-get-the-position-selected-in-a-recyclerview
        // http://stackoverflow.com/questions/24989218/get-visible-items-in-recyclerview
        // int firstVisiblePosition = layoutManager.findFirstCompletelyVisibleItemPosition();
        Collections.reverse(list);
        final int end = list.size() - 1;
        for (int position = 0; position < end; position++) {
            // test_function(adapter, position, end);
            adapter.notifyItemMoved(0, end - position);
        }
        // TODO: not sure the spec about this
        // TODO: scroll to top for now
        rv.scrollToPosition(0);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                exitAnimation();
                return true; // instead of super.onOptionsItemSelected(item)
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        exitAnimation();
    }

    private void exitAnimation() {
        if (menu.isOpened()) {
            // Log.d("exitAnimation", "isOpened()");
            menu.setOnMenuToggleListener(new FloatingActionMenu.OnMenuToggleListener() {
                @Override
                public void onMenuToggle(boolean opened) {
                    // Log.d("exitAnimation", "onMenuToggle()");
                    menu.setOnMenuToggleListener(null);
                    if (!opened) {
                        // Log.d("exitAnimation", "!opened");
                        // Finish the activity after the exit transition completes.
                        supportFinishAfterTransition();
                    }
                }
            });
            menu.close(true);
        }
        else {
            // Log.d("exitAnimation", "!isOpened()");
            // Finish the activity after the exit transition completes.
            supportFinishAfterTransition();
        }
    }
}
