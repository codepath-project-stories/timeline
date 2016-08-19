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
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

public class MomentDetailFragment extends Fragment {
  private static final String TAG = MomentDetailFragment.class.getSimpleName();

  @BindView(R.id.ivProfilePhoto)
  ImageView ivProfilePhoto;
  @BindView(R.id.ivMedia)
  ImageView ivMedia;

  private Moment mMoment;

  public MomentDetailFragment() {
    // Empty constructor is required for DialogFragment
    // Make sure not to add arguments to the constructor
    // Use `newInstance` instead as shown below
  }

  public static MomentDetailFragment newInstance(Moment moment) {
    MomentDetailFragment frag = new MomentDetailFragment();
    Bundle args = new Bundle();
    args.putParcelable(AppConstants.MOMENT_EXTRA, Parcels.wrap(moment));
    frag.setArguments(args);

    return frag;
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_moment_detail, container, false);
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

    if (mMoment.getUser() != null) {
      Glide.with(this).load(mMoment.getUser().getProfileImageUrl())
          .fitCenter()
          .bitmapTransform(new RoundedCornersTransformation(getActivity(), 25, 0))
          .into(ivProfilePhoto);
    }

    if (mMoment.getResourceUrl() != null) {
      Glide.with(this).load(mMoment.getResourceUrl())
          .centerCrop()
          .into(ivMedia);
    }
  }
}
