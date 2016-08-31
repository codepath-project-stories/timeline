package com.codepath.timeline.fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.codepath.timeline.R;
import com.codepath.timeline.adapters.StoriesAdapter;
import com.codepath.timeline.models.Story;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

abstract public class BaseStoryModelFragment extends Fragment {
    // BaseStoryModelFragment is a parent class for UserStoriesFragment and SharedStoriesFragment
    // calls TimelineActivity

    protected ArrayList<Story> stories;
    protected StoriesAdapter adaptStories;
    private Unbinder unbinder;
    SearchView searchView;

    @BindView(R.id.rvStories) RecyclerView rvStories;
    @BindView(R.id.avi) com.wang.avi.AVLoadingIndicatorView avi;

    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable final ViewGroup parent, @Nullable Bundle savedInstanceState) {

        // setup layout manager
        final View view = inflater.inflate(R.layout.fragment_base_model_story, parent, false);
        unbinder = ButterKnife.bind(this, view);

        final LinearLayoutManager layoutManagerList = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        // for switching between layouts (grid -> list)
        final StaggeredGridLayoutManager layoutManagerGrid = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);

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

        MenuItem searchItem = menu.findItem(R.id.miSearch);
        searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.requestFocus();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }

            public boolean onQueryTextSubmit(String searchQuery) {
                // in some cases text submit fires several times, clear the focus
                searchView.clearFocus();
                for (Story story: stories) {
                    if (story.getTitle().equals(searchQuery)) {
                        stories.clear();
                        adaptStories.notifyItemRangeRemoved(0, adaptStories.getItemCount());
                        addNew(story);
                    }
                }
                return true;
            }
        });
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

    protected void addAll(List<Story> newStories) {
        int curSize = adaptStories.getItemCount();
        stories.clear();
        adaptStories.notifyItemRangeRemoved(0, curSize);
        stories.addAll(newStories);
        // curSize should represent the first element that got added, newItems.size() represents the itemCount
        adaptStories.notifyItemRangeInserted(curSize, newStories.size());
    }

    protected void addNew(Story story) {
        stories.add(story);
        adaptStories.notifyItemInserted(0);
        rvStories.smoothScrollToPosition(stories.size());
    }

    void startAnim() {
        avi.show();
    }

    void stopAnim() {
        avi.hide();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
