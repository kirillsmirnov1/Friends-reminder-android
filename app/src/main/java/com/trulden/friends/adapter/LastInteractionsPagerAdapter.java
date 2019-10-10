package com.trulden.friends.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.trulden.friends.activity.LastInteractionsTabFragment;
import com.trulden.friends.database.entity.InteractionType;
import com.trulden.friends.database.wrappers.LastInteractionWrapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Handles fragments in tabs of {@link com.trulden.friends.activity.LastInteractionsFragment LastInteractionsFragment}.
 */
public class LastInteractionsPagerAdapter extends FragmentStatePagerAdapter {
    private List<InteractionType> types;
    private HashMap<String, ArrayList<LastInteractionWrapper>> lastInteractionsMap;

    public LastInteractionsPagerAdapter(
            FragmentManager fm, List<InteractionType> types,
            HashMap<String, ArrayList<LastInteractionWrapper>> lastInteractionsMap) {
        super(fm);
        this.types = types;
        this.lastInteractionsMap = lastInteractionsMap;
    }

    @Override
    @NonNull
    public Fragment getItem(int position) {
        return LastInteractionsTabFragment.newInstance(lastInteractionsMap.get(types.get(position).getInteractionTypeName()));
    }

    @Override
    public int getCount() {
        return types.size();
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
        // I am honestly have no idea why, but without this thing notifydatasethaschanged simply doesn't work
        // Look up https://stackoverflow.com/a/36348078/11845909
        return POSITION_NONE;
    }

    public void setLastInteractionsMap(HashMap<String, ArrayList<LastInteractionWrapper>> lastInteractionsMap) {
        this.lastInteractionsMap = lastInteractionsMap;
    }
}
