package com.codepath.timeline.activities;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.FrameLayout;

import com.codepath.timeline.R;
import com.codepath.timeline.fragments.MomentDetailFragment;
import com.codepath.timeline.models.Moment;
import com.codepath.timeline.util.AppConstants;
import com.codepath.timeline.util.MockResponseGenerator;

import org.parceler.Parcels;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MomentDetailActivity extends AppCompatActivity {
  @BindView(R.id.flMoment)
  FrameLayout flMoment;

  private Moment mMoment;
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_moment_detail);
    ButterKnife.bind(this);

    mMoment = Parcels.unwrap(getIntent().getParcelableExtra(AppConstants.MOMENT_EXTRA));
    if(mMoment != null) {
      initFragment();
    }
  }

  private void initFragment() {
    FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
    Fragment fragment = MomentDetailFragment.newInstance(mMoment);
    ft.replace(R.id.flMoment, fragment);
    ft.commit();
  }
}
