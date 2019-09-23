package com.trulden.friends.adapter;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.trulden.friends.activity.LastInteractionsTabFragment;
import com.trulden.friends.database.entity.InteractionType;
import com.trulden.friends.database.entity.LastInteraction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Handles fragments in tabs of LastInteractionsFragment.
 *
 * @see com.trulden.friends.activity.LastInteractionsFragment LastInteractionsFragment
 * @see FragmentStatePagerAdapter FragmentStatePagerAdapter
 */
public class LastInteractionsPagerAdapter extends FragmentStatePagerAdapter {
    private List<InteractionType> types;
    private HashMap<String, ArrayList<LastInteraction>> lastInteractionsMap;

    public LastInteractionsPagerAdapter(FragmentManager fm, List<InteractionType> types, HashMap<String, ArrayList<LastInteraction>> lastInteractionsMap) {
        super(fm);
        this.types = types;
        this.lastInteractionsMap = lastInteractionsMap;
    }

    @Override
    public Fragment getItem(int position) {
        return LastInteractionsTabFragment.newInstance(lastInteractionsMap.get(types.get(position).getInteractionTypeName()));
    }

    @Override
    public int getCount() {
        return types.size();
    }
}
