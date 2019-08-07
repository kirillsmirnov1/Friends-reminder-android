package com.trulden.friends.adapter;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import com.trulden.friends.activity.TabReminder;

public class PagerAdapter extends FragmentStatePagerAdapter {
    private int mNumOfTabs;

    public PagerAdapter(FragmentManager fm, int numOfTabs) {
        super(fm);

        mNumOfTabs = numOfTabs;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0: return TabReminder.newInstance(TabReminder.MEETINGS_TAG);
            case 1: return TabReminder.newInstance(TabReminder.TEXTING_TAG);
            case 2: return TabReminder.newInstance(TabReminder.CALLS_TAG);
            default: return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}
