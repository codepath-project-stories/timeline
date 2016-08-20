package com.codepath.timeline.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.codepath.timeline.models.Moment;
import com.codepath.timeline.util.AppConstants;
import com.qslll.library.fragments.ExpandingFragment;

import org.parceler.Parcels;

public class AutoPlayFragment extends ExpandingFragment {
  private Moment mMoment;


  public static AutoPlayFragment newInstance(Moment moment) {
    AutoPlayFragment fragment = new AutoPlayFragment();
    Bundle args = new Bundle();
    args.putParcelable(AppConstants.MOMENT_EXTRA, Parcels.wrap(moment));
    fragment.setArguments(args);
    return fragment;
  }

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    Bundle args = getArguments();
    if (args != null) {
      mMoment = Parcels.unwrap(args.getParcelable(AppConstants.MOMENT_EXTRA));
    }
  }

  @Override
  public Fragment getFragmentTop() {
    return AutoPlayTopFragment.newInstance(mMoment);
  }

  @Override
  public Fragment getFragmentBottom() {
    return AutoPlayBottomFragment.newInstance(mMoment);
  }
}
