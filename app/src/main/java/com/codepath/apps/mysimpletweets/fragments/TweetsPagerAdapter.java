package com.codepath.apps.mysimpletweets.fragments;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by skarwa on 10/5/17.
 */

public class TweetsPagerAdapter extends SmartFragmentStatePagerAdapter {
    private String tabTitles[] = new String[]{"Home","Mentions"};

    private static int NUM_ITEMS = 2;

    public TweetsPagerAdapter(FragmentManager fragmentManager) {
        super(fragmentManager);
    }

    // Returns total number of pages
    @Override
    public int getCount() {
        return NUM_ITEMS;
    }

    // Returns the fragment to display for that page
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case(0):// Fragment # 0 - This will show HomeTimelineFragment
                return HomeTimelineFragment.newInstance(0,tabTitles[0]);
            case(1)://Fragment #1 -This will show MentionsTimelineFragment
                return MentionsTimelineFragment.newInstance(1,tabTitles[1]);
            default:
                return null;
        }
    }

    // Returns the page title for the top indicator
    @Override
    public CharSequence getPageTitle(int position) {
        return tabTitles[position];
    }
}
