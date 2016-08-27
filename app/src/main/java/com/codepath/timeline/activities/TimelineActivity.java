package com.codepath.timeline.activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.codepath.timeline.R;
import com.codepath.timeline.adapters.MomentsHeaderAdapter;
import com.codepath.timeline.fragments.DetailDialogFragment;
import com.codepath.timeline.models.Moment;
import com.codepath.timeline.models.Story;
import com.codepath.timeline.network.TimelineClient;
import com.codepath.timeline.util.view.ItemClickSupport;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersDecoration;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class TimelineActivity extends AppCompatActivity {
    // TimelineActivity calls showDetailDialog() to generate DetailDialogFragment
    // DetailDialogFragment creates R.layout.fragment_moment_detail and ScreenSlidePagerAdapter

    static int SOURCE_MODE = 1;
    // 0: R.drawable.image_test2
    // 1: getIntent().getStringExtra("imageUrl")

    private static final String TAG = TimelineActivity.class.getSimpleName();
    @BindView(R.id.appbar)
    AppBarLayout appbar;
    @BindView(R.id.collapsing_toolbar)
    CollapsingToolbarLayout collapsing_toolbar;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.rvMoments)
    RecyclerView rvMoments;
    @BindView(R.id.ivAutoPlay)
    ImageView ivAutoPlay;
    @BindView(R.id.addBtn)
    FloatingActionButton add;

    Story story;
    String imageUrl;

    private List<Moment> mMomentList;
    private MomentsHeaderAdapter mAdapter;
    private int REQUEST_CODE = 6;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);
        ButterKnife.bind(this);

        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        initList();
    }

    private void initList() {
        mMomentList = new ArrayList<>();
        mAdapter = new MomentsHeaderAdapter(this, mMomentList);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rvMoments.setLayoutManager(linearLayoutManager);
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

        ItemClickSupport.addTo(rvMoments).setOnItemClickListener(
                new ItemClickSupport.OnItemClickListener() {
                    @Override
                    public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                        showDetailDialog(position);
                    }
                });

        // extract from the intent
        // load the image url for the background of the story into the image view
        if (SOURCE_MODE == 0) {
            story = null;
            imageUrl = null;
            collapsing_toolbar.setTitle("Baby Matthew Smith");
            collapsing_toolbar.setCollapsedTitleTextColor(Color.WHITE);
            Glide.with(this)
                    .load(R.drawable.image_test2)
                    .centerCrop()
                    .into(ivAutoPlay);
        } else if (SOURCE_MODE == 1) {
            story = (Story) Parcels.unwrap(getIntent().getParcelableExtra("story"));
            imageUrl = getIntent().getStringExtra("imageUrl");
            Log.d("DEBUG", story.toString());
            collapsing_toolbar.setTitle(story.getTitle());
            collapsing_toolbar.setCollapsedTitleTextColor(Color.WHITE);
            Glide.with(this)
                    .load(imageUrl)
                    .centerCrop()
                    .into(ivAutoPlay);
        }

        getMomentList();

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Todo: add a new moment
                Intent intent = new Intent(TimelineActivity.this, NewMomentActivity.class);
                startActivityForResult(intent, REQUEST_CODE);
            }
        });
    }

    // TODO: Change the momentId when making network request
    private void getMomentList() {
        mMomentList.addAll(TimelineClient.getInstance().getMomentsList(this, -1));
        mAdapter.notifyItemRangeInserted(0, mMomentList.size());
    }

    private void showDetailDialog(int position) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        DetailDialogFragment composeDialogFragment = DetailDialogFragment.newInstance(mMomentList, position);
        composeDialogFragment.show(fragmentManager, "fragment_compose");
    }

    @OnClick(R.id.ivAutoPlay)
    public void onAutoPlay(View view) {
        // TEMPORARY PLACEHOLDER
        Intent intent = new Intent(TimelineActivity.this, AutoPlayActivity.class);
        intent.putExtra("story", Parcels.wrap(story));
        intent.putExtra("imageUrl", imageUrl);
        startActivity(intent);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request it is that we're responding to
        if (requestCode == REQUEST_CODE && resultCode == 1) {
            // Get the URI that points to the selected contact
            Moment moment = Parcels.unwrap(data.getParcelableExtra("moment"));
            Log.d("DEBUG", moment.toString());
            Snackbar.make(findViewById(android.R.id.content), moment.toString(), Snackbar.LENGTH_SHORT).show();
            // Todo: add a moment into the RV
        }
    }
}
