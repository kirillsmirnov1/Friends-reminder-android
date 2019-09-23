package com.trulden.friends.activity;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.trulden.friends.R;
import com.trulden.friends.adapter.LastInteractionsPagerAdapter;
import com.trulden.friends.database.FriendsViewModel;
import com.trulden.friends.database.entity.InteractionType;
import com.trulden.friends.database.entity.LastInteraction;
import com.trulden.friends.view.TabCounterView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

/**
 * Holds {@link LastInteractionsTabFragment}.
 */
public class LastInteractionsFragment extends Fragment {

    public static final String LOG_TAG = LastInteractionsFragment.class.getSimpleName();

    private FriendsViewModel friendsViewModel;

    private List<InteractionType> types = new ArrayList<>();
    private HashMap<String, ArrayList<LastInteraction>> lastInteractionsMap = new HashMap<>();
    private HashMap<String, Integer> counterMap = new HashMap<>();

    public LastInteractionsFragment() {
        // Required empty public constructor
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

        friendsViewModel = ViewModelProviders.of(Objects.requireNonNull(getActivity()))
                .get(FriendsViewModel.class);

        friendsViewModel.getAllInteractionTypes().observe(this, new Observer<List<InteractionType>>() {
            @Override
            public void onChanged(List<InteractionType> interactionTypes) {
                types = interactionTypes;

                for(InteractionType type : types){
                    lastInteractionsMap.put(type.getInteractionTypeName(), new ArrayList<LastInteraction>());
                }

                friendsViewModel.getLastInteractions(/*Calendar.getInstance().getTimeInMillis()*/)
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

                            lastInteractionsMap.get(currentType).add(interaction);
                            if(interaction.itsTime()){
                                counterMap.put(currentType, counterMap.get(currentType) + 1);
                            }
                        }

                        initTabsAndPageViewer(view);
                    }
                });


            }
        });


    }

    private void initTabsAndPageViewer(View view) {
        TabLayout tabLayout = view.findViewById(R.id.last_interactions_tab_layout);
        tabLayout.removeAllTabs();

        for(InteractionType type : types){
            TabCounterView tcv = new TabCounterView(getContext(),
                    type.getInteractionTypeName(), counterMap.get(type.getInteractionTypeName()));

            tabLayout.addTab(tabLayout.newTab().setCustomView(tcv));
        }

        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        final ViewPager viewPager = view.findViewById(R.id.last_interactions_pager);
        final LastInteractionsPagerAdapter adapter = new LastInteractionsPagerAdapter(getFragmentManager(), types, lastInteractionsMap);

        viewPager.setAdapter(adapter);

        viewPager.addOnPageChangeListener(new
                TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
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

        // FIXME Is it enough, or should I do it for every tab?
        adapter.notifyDataSetChanged();
    }

}
