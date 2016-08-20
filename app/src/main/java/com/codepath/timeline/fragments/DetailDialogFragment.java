package com.codepath.timeline.fragments;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ToxicBakery.viewpager.transforms.CubeOutTransformer;
import com.codepath.timeline.R;
import com.codepath.timeline.adapters.SmartFragmentStatePagerAdapter;
import com.codepath.timeline.models.Moment;
import com.codepath.timeline.util.AppConstants;
import com.codepath.timeline.util.MockResponseGenerator;
import com.codepath.timeline.util.view.DepthPageTransformer;

import org.parceler.Parcels;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DetailDialogFragment extends DialogFragment implements MomentDetailFragment.MomentDetailListener {
  @BindView(R.id.vpMoment)
  ViewPager vpMoment;

  private ScreenSlidePagerAdapter mPagerAdapter;
  private List<Moment> mMomentList;
  private Moment mMoment;
  private int index;



  public static DetailDialogFragment newInstance(List<Moment> momentList, int index) {
    DetailDialogFragment frag = new DetailDialogFragment();
    Bundle args = new Bundle();
    args.putParcelable(AppConstants.MOMENT_LIST_EXTRA, Parcels.wrap(momentList));
    args.putInt(AppConstants.INDEX, index);
    frag.setArguments(args);

    return frag;
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_detail_dialog, container);
    ButterKnife.bind(this, view);
    return view;
  }


  @Override
  public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    mMomentList = Parcels.unwrap(getArguments().getParcelable(AppConstants.MOMENT_LIST_EXTRA));
    index = getArguments().getInt(AppConstants.INDEX);
    if (mMomentList != null && index != -1) {
      initDialog();
    }
  }

  @Override
  public void onStart() {
    super.onStart();
    Dialog dialog = getDialog();
    if (dialog != null) {
      // DialogFragment is not taking up the whole screen
      // http://stackoverflow.com/a/26163346
      int width = ViewGroup.LayoutParams.MATCH_PARENT;
      int height = ViewGroup.LayoutParams.MATCH_PARENT;
      dialog.getWindow().setLayout(width, height);
    }
  }

  private void initDialog() {
    mMoment = mMomentList.get(index);
    mPagerAdapter = new ScreenSlidePagerAdapter(getChildFragmentManager());
    vpMoment.setAdapter(mPagerAdapter);
    vpMoment.setPageTransformer(true, new DepthPageTransformer());


  }

  @Override
  public void onCloseButtonClicked() {
    dismiss();
  }

  private class ScreenSlidePagerAdapter extends SmartFragmentStatePagerAdapter {
    public ScreenSlidePagerAdapter(FragmentManager fm) {
      super(fm);
    }

    @Override
    public Fragment getItem(int position) {
      mMoment = mMomentList.get(position + index);
      return MomentDetailFragment.newInstance(mMoment);
    }

    @Override
    public int getCount() {
      return mMomentList.size() - index;
    }
  }
}