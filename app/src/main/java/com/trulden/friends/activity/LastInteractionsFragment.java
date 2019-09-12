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
import com.trulden.friends.adapter.PagerAdapter;
import com.trulden.friends.adapter.TabCounterView;
import com.trulden.friends.database.FriendsViewModel;
import com.trulden.friends.database.entity.InteractionType;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class LastInteractionsFragment extends Fragment {

    private FriendsViewModel friendsViewModel;
    private List<InteractionType> types = new ArrayList<>();

    private TabLayout mTabLayout;

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
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        friendsViewModel = ViewModelProviders.of(getActivity()).get(FriendsViewModel.class);

        friendsViewModel.getAllInteractionTypes().observe(this, new Observer<List<InteractionType>>() {
            @Override
            public void onChanged(List<InteractionType> interactionTypes) {
                types = interactionTypes;
            }
        });

        initTabsAndPageViewer(view);
    }

    private void initTabsAndPageViewer(View view) {
        mTabLayout = view.findViewById(R.id.last_interactions_tab_layout);

        TabCounterView tcv0 = new TabCounterView(getContext(), "Meetings", getResources().getStringArray(R.array.meetings_a_while_ago).length);
        TabCounterView tcv1 = new TabCounterView(getContext(), "Texting", getResources().getStringArray(R.array.texting_a_while_ago).length);
        TabCounterView tcv2 = new TabCounterView(getContext(), "Calls", getResources().getStringArray(R.array.call_a_while_ago).length);

        mTabLayout.addTab(mTabLayout.newTab().setCustomView(tcv0));
        mTabLayout.addTab(mTabLayout.newTab().setCustomView(tcv1));
        mTabLayout.addTab(mTabLayout.newTab().setCustomView(tcv2));

        mTabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        final ViewPager viewPager = view.findViewById(R.id.last_interactions_pager);
        final PagerAdapter adapter = new PagerAdapter(getFragmentManager(), mTabLayout.getTabCount());

        viewPager.setAdapter(adapter);

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

}
