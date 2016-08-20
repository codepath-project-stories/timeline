package com.codepath.timeline.activities;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.ToxicBakery.viewpager.transforms.CubeOutTransformer;
import com.codepath.timeline.R;
import com.codepath.timeline.fragments.MomentDetailFragment;
import com.codepath.timeline.models.Moment;
import com.codepath.timeline.util.AppConstants;

import org.parceler.Parcels;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MomentDetailActivity extends AppCompatActivity {
  // MomentDetailActivity is not used

  @BindView(R.id.vpMoment)
  ViewPager vpMoment;

  private ScreenSlidePagerAdapter mPagerAdapter;
  private List<Moment> mMomentList;
  private Moment mMoment;
  private int index;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_moment_detail);
    ButterKnife.bind(this);

    mMomentList = Parcels.unwrap(getIntent().getParcelableExtra(AppConstants.MOMENT_LIST_EXTRA));
    index = getIntent().getIntExtra(AppConstants.INDEX, -1);
    if (index != -1) {
      mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
      vpMoment.setAdapter(mPagerAdapter);
      vpMoment.setPageTransformer(true, new CubeOutTransformer());
    }
  }

  private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
    public ScreenSlidePagerAdapter(FragmentManager fm) {
      super(fm);
    }

    @Override
    public Fragment getItem(int position) {
      mMoment = mMomentList.get(position+index);
      return MomentDetailFragment.newInstance(mMoment);
    }

    @Override
    public int getCount() {
      return mMomentList.size()-index;
    }
  }
}
