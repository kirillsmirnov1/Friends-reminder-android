package com.trulden.friends.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.trulden.friends.R;
import com.trulden.friends.adapter.LastInteractionsPagerAdapter;
import com.trulden.friends.database.FriendsViewModel;
import com.trulden.friends.database.entity.InteractionType;
import com.trulden.friends.database.wrappers.LastInteraction;
import com.trulden.friends.view.TabCounterView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import static android.content.Context.MODE_PRIVATE;

/**
 * Holds {@link LastInteractionsTabFragment}.
 */
public class LastInteractionsFragment extends Fragment {

    public static final String LOG_TAG = LastInteractionsFragment.class.getSimpleName();

    private FriendsViewModel mFriendsViewModel;

    private List<InteractionType> types = new ArrayList<>();
    private HashMap<String, ArrayList<LastInteraction>> lastInteractionsMap = new HashMap<>();
    private HashMap<String, Integer> counterMap = new HashMap<>();
    private TabLayout mTabLayout;
    private LastInteractionsPagerAdapter mPagerAdapter;

    LastInteractionsFragment(FriendsViewModel friendsViewModel) {
        mFriendsViewModel = friendsViewModel;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.fragment_last_interactions, container, false);
    }

    @Override
    public void onViewCreated(final @NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

//        mFriendsViewModel = ViewModelProviders.of(Objects.requireNonNull(getActivity()))
//                .get(FriendsViewModel.class);

        mFriendsViewModel.getAllInteractionTypes().observe(getViewLifecycleOwner(), new Observer<List<InteractionType>>() {
            @Override
            public void onChanged(List<InteractionType> interactionTypes) {
                types = interactionTypes;

                for(InteractionType type : types){
                    lastInteractionsMap.put(type.getInteractionTypeName(), new ArrayList<LastInteraction>());
                }

                initTabsAndPageViewer(view);

                mFriendsViewModel.getLastInteractions(/*Calendar.getInstance().getTimeInMillis()*/)
                        .observe(getViewLifecycleOwner(), new Observer<List<LastInteraction>>() {
                    @Override
                    public void onChanged(List<LastInteraction> lastInteractions) {

                        for(InteractionType type : types){
                            Objects.requireNonNull(
                                    lastInteractionsMap.get(type.getInteractionTypeName())).clear();
                            counterMap.put(type.getInteractionTypeName(), 0);
                        }

                        for(LastInteraction interaction : lastInteractions){
                            String currentType = interaction.getInteractionType().getInteractionTypeName();

                            Objects.requireNonNull(
                                    lastInteractionsMap.get(currentType)).add(interaction);

                            if(interaction.itsTime()){
                                counterMap.put(currentType, counterMap.get(currentType) + 1);
                            }
                        }

                        for(int i = 0; i < types.size(); ++i){
                            ((TabCounterView)mTabLayout.getTabAt(i).getCustomView())
                                    .setCounter(counterMap.get(types.get(i).getInteractionTypeName()));
                        }

                        mPagerAdapter.setLastInteractionsMap(lastInteractionsMap);
                        mPagerAdapter.notifyDataSetChanged();
                    }
                });


            }
        });
    }

    private void initTabsAndPageViewer(View view) {
        mTabLayout = view.findViewById(R.id.last_interactions_tab_layout);
        mTabLayout.removeAllTabs();

        for(InteractionType type : types){
            TabCounterView tcv = new TabCounterView(getContext(),
                    type.getInteractionTypeName(), 0);

            mTabLayout.addTab(mTabLayout.newTab().setCustomView(tcv));
        }

        mTabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        final ViewPager viewPager = view.findViewById(R.id.last_interactions_pager);
        mPagerAdapter = new LastInteractionsPagerAdapter(getFragmentManager(), types, lastInteractionsMap);

        viewPager.setAdapter(mPagerAdapter);

        viewPager.addOnPageChangeListener(new
                TabLayout.TabLayoutOnPageChangeListener(mTabLayout));

        mTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    int getSelectedTabPos(){
        return mTabLayout.getSelectedTabPosition();
    }

    void selectTab(int pos){
        if(mTabLayout != null && pos < mTabLayout.getTabCount() && pos != -1){
            mTabLayout.getTabAt(pos).select();
        }
    }

    void retrieveSelectedTab(){
        int savedPos = getActivity().getPreferences(MODE_PRIVATE)
                .getInt(getString(R.string.shared_pref_opened_LI_tab), -1);

        selectTab(savedPos);

        SharedPreferences.Editor editor = getActivity().getPreferences(MODE_PRIVATE).edit();
        editor.remove(getString(R.string.shared_pref_opened_LI_tab));
        editor.apply();
    }

}
