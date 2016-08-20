package com.codepath.timeline.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.codepath.timeline.R;
import com.codepath.timeline.activities.TimelineActivity;
import com.codepath.timeline.adapters.StoriesAdapter;
import com.codepath.timeline.models.Story;
import com.codepath.timeline.util.view.DividerItemDecoration;
import com.codepath.timeline.util.view.ItemClickSupport;

import org.parceler.Parcels;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

abstract public class BaseStoryModelFragment extends Fragment {

    protected ArrayList<Story> stories;
    protected StoriesAdapter adaptStories;
    @BindView(R.id.rvStories)
    RecyclerView rvStories;
    @BindView(R.id.fab)
    FloatingActionButton fab;
    private Unbinder unbinder;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup parent, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_base_model_story, parent, false);
        unbinder = ButterKnife.bind(this, view);
        setupViews();
        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void setupViews() {
        stories = new ArrayList<>();
        adaptStories = new StoriesAdapter(stories);
        rvStories.setAdapter(adaptStories);

        // setup layout manager
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayout.VERTICAL, false);
        rvStories.setLayoutManager(layoutManager);
        layoutManager.scrollToPosition(0);

        // setup visual line divider
        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST);
        rvStories.addItemDecoration(itemDecoration);
        rvStories.setHasFixedSize(false);

        // abstract method call
        populateList();

        ItemClickSupport.addTo(rvStories).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
                                                                     @Override
                                                                     public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                                                                         Intent i = new Intent(getContext(), TimelineActivity.class);
                                                                         Story story = stories.get(position);
                                                                         i.putExtra("story", Parcels.wrap(story));
                                                                         startActivity(i);
                                                                     }
                                                                 }
        );
    }

    protected abstract void populateList();

    protected void addAll(ArrayList<Story> newStories) {
        int curSize = adaptStories.getItemCount();
        stories.clear();
        adaptStories.notifyItemRangeRemoved(0, curSize);
        stories.addAll(newStories);
        // curSize should represent the first element that got added, newItems.size() represents the itemCount
        adaptStories.notifyItemRangeInserted(curSize, newStories.size());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
