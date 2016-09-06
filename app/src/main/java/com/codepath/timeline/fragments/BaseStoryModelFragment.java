package com.codepath.timeline.fragments;


import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.codepath.timeline.R;
import com.codepath.timeline.adapters.StoriesAdapter;
import com.codepath.timeline.models.Story;
import com.codepath.timeline.network.ParseApplication;
import com.codepath.timeline.util.ScreenUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

abstract public class BaseStoryModelFragment extends Fragment {
    // BaseStoryModelFragment is a parent class for UserStoriesFragment and SharedStoriesFragment
    // calls TimelineActivity

    private static final String TAG = "TimelineLog: " + BaseStoryModelFragment.class.getSimpleName();

    protected ArrayList<Story> stories;
    protected StoriesAdapter adaptStories;
    private Unbinder unbinder;
    SearchView searchView;
    MenuItem searchItem;
    Context context;
    boolean pendingIntroAnimation;
    private static final int ANIM_DURATION_TOOLBAR = 300;
    boolean searched = false;

    @BindView(R.id.rvStories) RecyclerView rvStories;
    @BindView(R.id.avi) com.wang.avi.AVLoadingIndicatorView avi;

    // TODO: use push notification instead of pulling and auto refresh every a few seconds
    private Handler mHandler;
    private Runnable mRunnablePopulateList = new Runnable() {
        @Override
        public void run() {
            Log.d(TAG, "mRunnablePopulateList");
            populateList();
            mHandler.postDelayed(mRunnablePopulateList, ParseApplication.REFRESH_INTERVAL);
        }
    };

    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable final ViewGroup parent, @Nullable Bundle savedInstanceState) {

        // Todo: finish up the animation http://frogermcs.github.io/Instagram-with-Material-Design-concept-is-getting-real/
        if (savedInstanceState == null) {
            pendingIntroAnimation = true;
        }
        // setup layout manager
        final View view = inflater.inflate(R.layout.fragment_base_model_story, parent, false);
        unbinder = ButterKnife.bind(this, view);

        final LinearLayoutManager layoutManagerList =
                new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
        // for switching between layouts (grid -> list)
        final StaggeredGridLayoutManager layoutManagerGrid =
                new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);

        stories = new ArrayList<>();
        adaptStories = new StoriesAdapter(stories, getChildFragmentManager());
        setupLayout(layoutManagerList);

        // abstract method call
        populateList();

        // Todo: toggle between different layouts, if necessary:
//        toggle.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (!clicked) {
//                    setupLayout(layoutManagerGrid);
//                    clicked = true;
//                } else {
//                    setupLayout(layoutManagerList);
//                    clicked = false;
//                }
//            }
//        });

        mHandler = new Handler(Looper.getMainLooper());
        mHandler.postDelayed(mRunnablePopulateList, ParseApplication.REFRESH_INTERVAL);
        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main, menu);
        searchItem = menu.findItem(R.id.miSearch);
        searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.requestFocus();
        if (pendingIntroAnimation) {
            pendingIntroAnimation = false;
            startIntroAnimation();
        }

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }

            public boolean onQueryTextSubmit(String searchQuery) {
                // in some cases text submit fires several times, clear the focus
                searched = true;
                searchView.clearFocus();
                String lowerCaseQuery = searchQuery.toLowerCase();
                List<Story> matchingQueryStory = new ArrayList<Story>();
                for (int i = 0; i < stories.size(); i++) {
                    Story story = stories.get(i);
                    if (story.getTitle().toLowerCase().contains(lowerCaseQuery)) {
                        matchingQueryStory.add(story);
                    }
                }

                if (matchingQueryStory.size() == 0) {
                    if (getView() != null) {
                        Snackbar.make(getView(), "No stories found", Snackbar.LENGTH_SHORT).show();
                    }
                } else {
                    stories.clear();
                    stories.addAll(matchingQueryStory);
                    adaptStories.notifyDataSetChanged();
                }

                return true;
            }
        });

        MenuItemCompat.setOnActionExpandListener(searchItem, new MenuItemCompat.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                if (searched) {
                    populateList();
                    searched = false;
                }
                return true;
            }
        });
    }

    private void startIntroAnimation() {

        searchItem.getActionView().setTranslationY(-ScreenUtil.dpToPx(56));

        searchItem.getActionView().animate()
                .translationY(0)
                .setDuration(ANIM_DURATION_TOOLBAR)
                .setStartDelay(500)
                .start();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    protected void setupLayout(RecyclerView.LayoutManager layout) {
        rvStories.setLayoutManager(layout);
        layout.scrollToPosition(0);
        rvStories.setAdapter(adaptStories);
    }

    protected abstract void populateList();

    // TODO: only need to add new items instead of clear()
    protected void addAll(List<Story> newStories) {
        // TODO: Hacky but check the size so we don't update the list everytime it's auto-refreshing
        if(newStories.size() != stories.size()) {
            Log.d(TAG, "Updating stories list");
            stories.clear();
            stories.addAll(newStories);
            adaptStories.notifyDataSetChanged();
        } else {
            Log.d(TAG, "Same stories list");
        }
    }

    protected void addNew(Story story) {
        stories.add(0, story);
        adaptStories.notifyItemInserted(0);
        rvStories.smoothScrollToPosition(0);
    }

    protected void startAnim() {
        Log.d(TAG, "startAnim");
        avi.show();
    }

    protected void stopAnim() {
        Log.d(TAG, "stopAnim");
        if (avi.isShown()) {
            avi.hide();
        }
    }

    @Override
    public void onDestroyView() {
        if (mHandler != null) {
            mHandler.removeCallbacks(mRunnablePopulateList);
        }
        unbinder.unbind();
        super.onDestroyView();
    }
}
