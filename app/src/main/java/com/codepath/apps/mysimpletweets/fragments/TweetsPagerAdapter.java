package com.codepath.apps.mysimpletweets.fragments;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by skarwa on 10/5/17.
 */

public class TweetsPagerAdapter extends FragmentPagerAdapter {
    private Context context;
    private String tabTitles[] = new String[]{"Home","Mentions"};

    public TweetsPagerAdapter(FragmentManager fm,Context context) {
        super(fm);
        this.context = context;
    }

    @Override
    public Fragment getItem(int position) {
        if(position == 0){
            return new HomeTimelineFragment();
        } else if(position == 1){
            return new MentionsTimelineFragment();
        }
        return null;
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tabTitles[position];
    }
}
