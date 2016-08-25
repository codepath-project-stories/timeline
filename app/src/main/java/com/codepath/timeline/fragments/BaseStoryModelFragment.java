package com.codepath.timeline.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.codepath.timeline.R;
import com.codepath.timeline.activities.NewStoryActivity;
import com.codepath.timeline.adapters.StoriesAdapter;
import com.codepath.timeline.models.Story;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

abstract public class BaseStoryModelFragment extends Fragment {
    // BaseStoryModelFragment calls TimelineActivity

    protected ArrayList<Story> stories;
    protected StoriesAdapter adaptStories;
    @BindView(R.id.addBtn) FloatingActionButton add;
    @BindView(R.id.rvStories) RecyclerView rvStories;
    private Unbinder unbinder;
    private int REQUEST_CODE = 5;
    private boolean clicked = false;

    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable final ViewGroup parent, @Nullable Bundle savedInstanceState) {

        // setup layout manager
        final View view = inflater.inflate(R.layout.fragment_base_model_story, parent, false);
        unbinder = ButterKnife.bind(this, view);

        final LinearLayoutManager layoutManagerList = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        // for switching between layouts (grid -> list)
        final StaggeredGridLayoutManager layoutManagerGrid = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);

        stories = new ArrayList<>();
        adaptStories = new StoriesAdapter(stories);
        setupLayout(layoutManagerList);

        // abstract method call
        populateList();

        // Todo: on click listener is added directly in the adapter, modify it later to include the title of a story as well
        // Todo: the code below is not needed then, remove when sure
//        ItemClickSupport
//                .addTo(rvStories)
//                .setOnItemClickListener(
//                        new ItemClickSupport.OnItemClickListener() {
//                            @Override
//                            public void onItemClicked(RecyclerView recyclerView,
//                                                      int position,
//                                                      View v) {
//                                Intent i = new Intent(getContext(), TimelineActivity.class);
//                                Story story = stories.get(position);
//                                i.putExtra("story", Parcels.wrap(story));
//                                startActivity(i);
//                            }
//                        }
//                );

        //
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Todo: add a new story
                Intent intent = new Intent(getActivity(), NewStoryActivity.class);
                startActivityForResult(intent, REQUEST_CODE);
            }
        });

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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request it is that we're responding to
        if (requestCode == REQUEST_CODE && resultCode == 1) {
            // Get the URI that points to the selected contact
            Story story = Parcels.unwrap(data.getParcelableExtra("story"));
            Snackbar.make(getView(), story.toString(), Snackbar.LENGTH_SHORT).show();
            addNew(story);
        }
    }

    protected void addNew(Story story) {
        stories.add(story);
        adaptStories.notifyItemInserted(0);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
