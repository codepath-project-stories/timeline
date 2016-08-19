package com.codepath.timeline.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.codepath.timeline.R;
import com.codepath.timeline.adapters.MomentsAdapter;
import com.codepath.timeline.models.Moment;
import com.codepath.timeline.util.AppConstants;
import com.codepath.timeline.util.ItemClickSupport;
import com.codepath.timeline.util.MockResponseGenerator;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TimelineActivity extends AppCompatActivity {
  private static final String TAG = TimelineActivity.class.getSimpleName();
  @BindView(R.id.rvMoments)
  RecyclerView rvMoments;

  private List<Moment> mMomentList;
  private MomentsAdapter mAdapter;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_timeline);
    ButterKnife.bind(this);

    initList();
  }

  private void initList(){
    mMomentList = MockResponseGenerator.getInstance().getMomentList();
    mAdapter = new MomentsAdapter(this, mMomentList);
    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
    rvMoments.setLayoutManager(linearLayoutManager);
    rvMoments.setAdapter(mAdapter);

    ItemClickSupport.addTo(rvMoments).setOnItemClickListener(
        new ItemClickSupport.OnItemClickListener() {
          @Override
          public void onItemClicked(RecyclerView recyclerView, int position, View v) {
            Moment moment = mMomentList.get(position);
            Intent intent = new Intent(TimelineActivity.this, MomentDetailActivity.class);
            intent.putExtra(AppConstants.MOMENT_EXTRA, Parcels.wrap(moment));
            startActivity(intent);
          }
        });

    // TODO: Replace with real data later
//    setTestData();
  }

  private void setTestData() {
    mMomentList = MockResponseGenerator.getInstance().getMomentList();
    mAdapter.notifyItemRangeInserted(0, mMomentList.size());
  }
}
