package com.codepath.timeline.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;

import com.codepath.timeline.util.AppConstants;
import com.qslll.library.fragments.ExpandingFragment;

public class AutoPlayFragment extends ExpandingFragment {
    private static final String TAG = AutoPlayFragment.class.getSimpleName();
    private String mMomentObjectId;


    public static AutoPlayFragment newInstance(String momentObjectId) {
        AutoPlayFragment fragment = new AutoPlayFragment();
        Bundle args = new Bundle();
        args.putString(AppConstants.OBJECT_ID, momentObjectId);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null) {
            mMomentObjectId = args.getString(AppConstants.OBJECT_ID, null);
            if (mMomentObjectId == null) {
                Log.e(TAG, "Moment OBJECT_ID is NULL");
            }
        }
    }

    @Override
    public Fragment getFragmentTop() {
        return AutoPlayTopFragment.newInstance(mMomentObjectId);
    }

    @Override
    public Fragment getFragmentBottom() {
        return AutoPlayBottomFragment.newInstance(mMomentObjectId);
    }
}
