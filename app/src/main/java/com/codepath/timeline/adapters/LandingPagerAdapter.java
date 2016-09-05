package com.codepath.timeline.adapters;


import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.codepath.timeline.fragments.SharedStoriesFragment;
import com.codepath.timeline.fragments.UserStoriesFragment;

public class LandingPagerAdapter extends SmartFragmentStatePagerAdapter {
    // LandingPagerAdapter creates UserStoriesFragment and SharedStoriesFragment
    // UserStoriesFragment extends BaseStoryModelFragment
    // BaseStoryModelFragment calls TimelineActivity

    final int PAGE_COUNT = 2;
    private final Context context;

    public LandingPagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.context = context;
    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return UserStoriesFragment.newInstance(context, 0);
            case 1:
                return SharedStoriesFragment.newInstance(context, 1);
            default:
                return null;
        }
    }

//    @Override
//    public CharSequence getPageTitle(int position) {
        // Generate title based on item position
//        switch (position) {
//            case 0:
//                return context.getResources().getString(R.string.my_stories);
//            case 1:
//                return context.getResources().getString(R.string.friends_stories);
//            default:
//                return null;
//        }
//        Drawable image = ContextCompat.getDrawable(context, imageResId[position]);
//        image.setBounds(0, 0, image.getIntrinsicWidth(), image.getIntrinsicHeight());
//        SpannableString sb = new SpannableString(" ");
//        ImageSpan imageSpan = new ImageSpan(image, ImageSpan.ALIGN_BOTTOM);
//        sb.setSpan(imageSpan, 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//        return sb;
//    }
}