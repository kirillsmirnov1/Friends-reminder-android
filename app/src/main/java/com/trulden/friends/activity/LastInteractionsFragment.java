package com.trulden.friends.activity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProviders;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.trulden.friends.R;
import com.trulden.friends.activity.interfaces.RecyclerViewContainer;
import com.trulden.friends.activity.interfaces.SelectionHandler;
import com.trulden.friends.adapter.LastInteractionsPagerAdapter;
import com.trulden.friends.database.MainViewModel;
import com.trulden.friends.database.entity.InteractionType;
import com.trulden.friends.database.wrappers.LastInteractionWrapper;
import com.trulden.friends.view.TabLabelWithCounterView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

/**
 * Holds {@link LastInteractionsTabFragment}.
 */
public class LastInteractionsFragment
    extends Fragment
    implements
        SelectionHandler,
        RecyclerViewContainer {

    public static final String LOG_TAG = LastInteractionsFragment.class.getSimpleName();

    private MainViewModel mViewModel;

    private List<InteractionType> mTypes = new ArrayList<>();
    private HashMap<String, ArrayList<LastInteractionWrapper>> mLastInteractionsMap = new HashMap<>();
    private HashMap<String, Integer> mCounterMap = new HashMap<>();
    private TabLayout mTabLayout;
    private LastInteractionsPagerAdapter mPagerAdapter;
    private ViewPager mViewPager;

    public LastInteractionsFragment() {
        // Fragments require public constructor with no args
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

        mViewModel = ViewModelProviders.of(getActivity()).get(MainViewModel.class);

        mViewPager = view.findViewById(R.id.fli_view_pager);

        mViewModel.getAllInteractionTypes().observe(getViewLifecycleOwner(), interactionTypes -> {
            mTypes = interactionTypes;

            view.findViewById(R.id.fli_no_data)
                .setVisibility(
                    mTypes == null || mTypes.size() < 1
                    ? View.VISIBLE
                    : View.GONE
            );

            for(InteractionType type : mTypes){
                mLastInteractionsMap.put(type.getInteractionTypeName(), new ArrayList<>());
            }

            initTabsAndPageViewer(view);

            mViewModel.getShowHiddenLI().observe(getViewLifecycleOwner(), showHiddenLI -> {
                LiveData<List<LastInteractionWrapper>> lastInteractionsLiveData =
                    showHiddenLI
                    ? mViewModel.getLiveAllLastInteractionWrappers()
                    : mViewModel.getLiveVisibleLastInteractionWrappers();

                lastInteractionsLiveData.observe(getViewLifecycleOwner(), lastInteractions -> {

                    // FIXME that map shuffling should be done in VM

                    for(InteractionType type : mTypes){
                        Objects.requireNonNull(
                                mLastInteractionsMap.get(type.getInteractionTypeName())).clear();
                        mCounterMap.put(type.getInteractionTypeName(), 0);
                    }

                    for(LastInteractionWrapper interaction : lastInteractions){
                        String currentType = interaction.getTypeName();

                        Objects.requireNonNull(
                                mLastInteractionsMap.get(currentType)).add(interaction);

                        if(interaction.itsTime()){
                            mCounterMap.put(currentType, mCounterMap.get(currentType) + 1);
                        }
                    }

                    for(int i = 0; i < mTypes.size(); ++i){
                        ((TabLabelWithCounterView)mTabLayout.getTabAt(i).getCustomView())
                                .setCounter(mCounterMap.get(mTypes.get(i).getInteractionTypeName()));
                    }

                    mPagerAdapter.setLastInteractionsMap(mLastInteractionsMap);
                    mPagerAdapter.notifyDataSetChanged();
                });

            });
        });

        retrieveSelectedTab();
    }

    @Override
    public void onResume() {
        super.onResume();

        retrieveSelectedTab();
    }

    private void initTabsAndPageViewer(View view) {
        mTabLayout = view.findViewById(R.id.fli_tab_layout);
        mTabLayout.removeAllTabs();

        for(InteractionType type : mTypes){
            TabLabelWithCounterView tcv = new TabLabelWithCounterView(getContext(),
                    type.getInteractionTypeName(), 0);

            mTabLayout.addTab(mTabLayout.newTab().setCustomView(tcv));
        }

        mTabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        final ViewPager viewPager = view.findViewById(R.id.fli_view_pager);
        mPagerAdapter = new LastInteractionsPagerAdapter(getChildFragmentManager(), mTypes, mLastInteractionsMap);

        viewPager.setAdapter(mPagerAdapter);

        viewPager.addOnPageChangeListener(new
                TabLayout.TabLayoutOnPageChangeListener(mTabLayout));

        mTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
                saveSelectedTab();

                for(int i = 0; i < mTypes.size(); ++i){
                    getTabFragment(i).finishActionMode();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    private void selectTab(int pos){
        if(mTabLayout != null && pos < mTabLayout.getTabCount() && pos != -1){
            mTabLayout.getTabAt(pos).select();
        }
    }

    /**
     * Select tab opened before
     */
    void retrieveSelectedTab(){
        selectTab(mViewModel.getSelectedLITabPos());
    }

    /**
     * Save selected tab position so it can be reopened later
     */
    void saveSelectedTab(){
        int tabPos = mTabLayout == null ? 0 : mTabLayout.getSelectedTabPosition();

        mViewModel.setSelectedLITabPos(tabPos);
    }

    @Override
    public void onDetach() {
        if(MainActivity.getFragmentToLoad() != MainActivity.FragmentToLoad.LAST_INTERACTIONS_FRAGMENT) {
            finishActionMode();
        }

        saveSelectedTab();

        super.onDetach();
    }

    @Override
    public void finishActionMode() {
        LastInteractionsTabFragment fragment = getTabFragment();

        if(fragment != null) {
            fragment.finishActionMode();
        }
    }

    @Override
    public void onActionModeFinished() {
        LastInteractionsTabFragment fragment = getTabFragment();

        if(fragment != null) {
            fragment.onActionModeFinished();
        }
    }

    private LastInteractionsTabFragment getTabFragment(int pos){
        if(pos >= 0) {
            return (LastInteractionsTabFragment) mPagerAdapter.instantiateItem(mViewPager, pos);
        } else {
            return null;
        }
    }

    private LastInteractionsTabFragment getTabFragment(){
        return getTabFragment(mTabLayout.getSelectedTabPosition());
    }

    String getSelectedTabName(){
        return mTypes.get(mTabLayout.getSelectedTabPosition()).getInteractionTypeName();
    }

    @Override
    public void scrollUp() {
        ((RecyclerViewContainer)getTabFragment()).scrollUp();
    }
}
