package com.codepath.timeline.fragments;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.codepath.timeline.R;
import com.codepath.timeline.adapters.SmartFragmentStatePagerAdapter;
import com.codepath.timeline.models.Comment;
import com.codepath.timeline.models.Moment;
import com.codepath.timeline.network.TimelineClient;
import com.codepath.timeline.network.UserClient;
import com.codepath.timeline.util.AppConstants;
import com.codepath.timeline.util.DateUtil;
import com.codepath.timeline.view.DepthPageTransformer;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class DetailDialogFragment extends DialogFragment {
  // DetailDialogFragment creates R.layout.fragment_moment_detail and ScreenSlidePagerAdapter

  private static final String TAG = DetailDialogFragment.class.getSimpleName();

  @BindView(R.id.vpMoment)
  ViewPager vpMoment;
  @BindView(R.id.ivClose)
  ImageView ivClose;
  @BindView(R.id.btPost)
  Button btPost;
  @BindView(R.id.etComment)
  EditText etComment;

  private ScreenSlidePagerAdapter mPagerAdapter;
  private List<Moment> mMomentList;
  private Moment mMoment;
  private int index;
  private String mStoryObjectId;

  public static DetailDialogFragment newInstance(String storyObjectId, int index) {
    DetailDialogFragment frag = new DetailDialogFragment();
    Bundle args = new Bundle();
    args.putString(AppConstants.OBJECT_ID, storyObjectId);
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

    index = getArguments().getInt(AppConstants.INDEX);
    mStoryObjectId = getArguments().getString(AppConstants.OBJECT_ID, null);
    if (mStoryObjectId == null) {
      Log.e(TAG, "Story OBJECT_ID is NULL");
      return;
    }

    TimelineClient.getInstance().getMomentList(mStoryObjectId, new TimelineClient.TimelineClientGetMomentListListener() {
      @Override
      public void onGetMomentList(List<Moment> itemList) {
        if (itemList != null) {
          mMomentList = new ArrayList<Moment>();
          mMomentList.addAll(itemList);
          initDialog();
        }
      }
      @Override
      public void onGetMomentChatList(List<Moment> itemList) {
      }
    });
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

    etComment.addTextChangedListener(new TextWatcher() {
      @Override
      public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        // do nothing
      }

      @Override
      public void onTextChanged(CharSequence s, int start, int before, int count) {
        // do nothing
      }

      @Override
      public void afterTextChanged(Editable s) {
        if (s.length() > 0) {
          btPost.setEnabled(true);
        } else {
          btPost.setEnabled(false);
        }
      }
    });
  }

  @OnClick(R.id.ivClose)
  public void onCloseButtonClicked() {
    Log.d(TAG, "---------- close ");
    dismiss();
  }

  @OnClick(R.id.btPost)
  public void onPostbuttonClicked() {
    Log.d(TAG, "---------- post");

    MomentDetailFragment fragment = (MomentDetailFragment) mPagerAdapter.getRegisteredFragment(vpMoment.getCurrentItem());
    if (fragment != null) {
      Comment comment = new Comment();
      comment.setCreatedAtReal(DateUtil.getCurrentDate());
      comment.setBody(etComment.getText().toString());
      comment.setAuthor(UserClient.getCurrentUser());

      fragment.addComment(comment);

      // If successful, clear the text and disable the button
      etComment.setText("");
      btPost.setEnabled(false);
    }
  }

  private class ScreenSlidePagerAdapter extends SmartFragmentStatePagerAdapter {
    public ScreenSlidePagerAdapter(FragmentManager fm) {
      super(fm);
    }

    @Override
    public Fragment getItem(int position) {
      mMoment = mMomentList.get(position + index);
      return MomentDetailFragment.newInstance(mMoment.getObjectId());
    }

    @Override
    public int getCount() {
      return mMomentList.size() - index;
    }
  }
}