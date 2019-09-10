package com.trulden.friends.adapter;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import com.trulden.friends.activity.LastInteractionsTabFragment;

public class PagerAdapter extends FragmentStatePagerAdapter {
    private int mNumOfTabs;

    public PagerAdapter(FragmentManager fm, int numOfTabs) {
        super(fm);

        mNumOfTabs = numOfTabs;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0: return LastInteractionsTabFragment.newInstance(LastInteractionsTabFragment.MEETINGS_TAG);
            case 1: return LastInteractionsTabFragment.newInstance(LastInteractionsTabFragment.TEXTING_TAG);
            case 2: return LastInteractionsTabFragment.newInstance(LastInteractionsTabFragment.CALLS_TAG);
            default: return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}
