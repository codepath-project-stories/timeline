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
import com.codepath.timeline.network.TimelineClient;
import com.codepath.timeline.network.UserClient;
import com.codepath.timeline.util.AppConstants;

import butterknife.BindView;
import butterknife.ButterKnife;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

public class AutoPlayBottomFragment extends Fragment {
  private static final String TAG = AutoPlayBottomFragment.class.getSimpleName();

  @BindView(R.id.ivProfilePhoto)
  ImageView ivProfilePhoto;

  private String mMomentObjectId;

  public AutoPlayBottomFragment() {
  }

  public static AutoPlayBottomFragment newInstance(String momentObjectId) {
    AutoPlayBottomFragment frag = new AutoPlayBottomFragment();
    Bundle args = new Bundle();
    args.putString(AppConstants.OBJECT_ID, momentObjectId);
    frag.setArguments(args);

    return frag;
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_auto_play_bottom, container, false);
    ButterKnife.bind(this, view);
    return view;
  }

  @Override
  public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    mMomentObjectId = getArguments().getString(AppConstants.OBJECT_ID, null);
    if (mMomentObjectId == null) {
      Log.e(TAG, "Moment OBJECT_ID is NULL");
      return;
    }

    TimelineClient.getInstance().getMoment(mMomentObjectId, new TimelineClient.TimelineClientGetMomentListener() {
      @Override
      public void onGetMomentListener(Moment moment) {
        updateMoment(moment);
      }
    });
  }

  private void updateMoment(Moment moment) {
    if (moment.getAuthor() != null) {
      Glide.with(this).load(UserClient.getProfileImageUrl(moment.getAuthor()))
          .fitCenter()
          .bitmapTransform(new RoundedCornersTransformation(getActivity(), 25, 0))
          .into(ivProfilePhoto);
    }
  }
}