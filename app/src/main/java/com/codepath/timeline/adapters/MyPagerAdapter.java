package com.codepath.timeline.adapters;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.codepath.timeline.fragments.SharedStoriesFragment;
import com.codepath.timeline.fragments.UserStoriesFragment;

public class MyPagerAdapter extends SmartFragmentStatePagerAdapter {
    // MyPagerAdapter creates UserStoriesFragment and SharedStoriesFragment
    // UserStoriesFragment extends BaseStoryModelFragment
    // BaseStoryModelFragment calls TimelineActivity

    final int PAGE_COUNT = 2;
    private String tabTitles[] = new String[] { "My stories", "Shared with me" };

    public MyPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return UserStoriesFragment.newInstance(0);
            case 1:
                return SharedStoriesFragment.newInstance(1);
            default:
                return null;
        }
    }

    @Override
    public CharSequence getPageTitle(int position) {
        // Generate title based on item position
        return tabTitles[position];
    }
}