package com.codepath.timeline.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.codepath.timeline.R;
import com.codepath.timeline.models.Moment;
import com.codepath.timeline.util.AppConstants;

import org.parceler.Parcels;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AutoPlayTopFragment extends Fragment {
  private static final String TAG = AutoPlayTopFragment.class.getSimpleName();

  @BindView(R.id.ivMedia)
  ImageView ivMedia;

  private Moment mMoment;

  public AutoPlayTopFragment() {
  }

  public static AutoPlayTopFragment newInstance(Moment moment) {
    AutoPlayTopFragment frag = new AutoPlayTopFragment();
    Bundle args = new Bundle();
    args.putParcelable(AppConstants.MOMENT_EXTRA, Parcels.wrap(moment));
    frag.setArguments(args);

    return frag;
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_auto_play_top, container, false);
    ButterKnife.bind(this, view);
    return view;
  }

  @Override
  public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    mMoment = Parcels.unwrap(getArguments().getParcelable(AppConstants.MOMENT_EXTRA));
    if (mMoment == null) {
      Log.d(TAG, "Moment extra is NULL");
    }

    if (mMoment.getMediaUrl() != null) {
      Glide.with(this).load(mMoment.getMediaUrl())
          .centerCrop()
          .into(ivMedia);
    }
  }
}