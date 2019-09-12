package com.trulden.friends.adapter;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.trulden.friends.activity.LastInteractionsTabFragment;
import com.trulden.friends.database.entity.InteractionType;

import java.util.List;

public class PagerAdapter extends FragmentStatePagerAdapter {
    private List<InteractionType> types;

    public PagerAdapter(FragmentManager fm, List<InteractionType> types) {
        super(fm);
        this.types = types;
    }

    @Override
    public Fragment getItem(int position) {
        return LastInteractionsTabFragment.newInstance(types.get(position).getInteractionTypeName());
    }

    @Override
    public int getCount() {
        return types.size();
    }
}
