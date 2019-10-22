package com.trulden.friends.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.trulden.friends.R;
import com.trulden.friends.activity.interfaces.SelectionHandler;
import com.trulden.friends.adapter.LastInteractionsPagerAdapter;
import com.trulden.friends.database.FriendsViewModel;
import com.trulden.friends.database.entity.InteractionType;
import com.trulden.friends.database.wrappers.LastInteractionWrapper;
import com.trulden.friends.view.TabLabelWithCounterView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import static android.content.Context.MODE_PRIVATE;

/**
 * Holds {@link LastInteractionsTabFragment}.
 */
public class LastInteractionsFragment extends Fragment implements SelectionHandler {

    public static final String LOG_TAG = LastInteractionsFragment.class.getSimpleName();

    private FriendsViewModel mViewModel;

    private List<InteractionType> types = new ArrayList<>();
    private HashMap<String, ArrayList<LastInteractionWrapper>> lastInteractionsMap = new HashMap<>();
    private HashMap<String, Integer> counterMap = new HashMap<>();
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

        mViewModel = ViewModelProviders.of(getActivity()).get(FriendsViewModel.class);

        mViewPager = view.findViewById(R.id.fli_view_pager);

        mViewModel.getAllInteractionTypes().observe(getViewLifecycleOwner(), new Observer<List<InteractionType>>() {
            @Override
            public void onChanged(List<InteractionType> interactionTypes) {
                types = interactionTypes; // TODO rename

                view.findViewById(R.id.fli_no_data)
                    .setVisibility(
                        types == null || types.size() < 1
                        ? View.VISIBLE
                        : View.GONE
                );

                for(InteractionType type : types){
                    lastInteractionsMap.put(type.getInteractionTypeName(), new ArrayList<LastInteractionWrapper>());
                }

                initTabsAndPageViewer(view);

                mViewModel.getShowHiddenLI().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
                    @Override
                    public void onChanged(Boolean showHiddenLI) {
                        LiveData<List<LastInteractionWrapper>> lastInteractions =
                            showHiddenLI
                            ? mViewModel.getAllLastInteractions()
                            : mViewModel.getVisibleLastInteractions();

                        lastInteractions.observe(getViewLifecycleOwner(), new Observer<List<LastInteractionWrapper>>() {
                                    @Override
                                    public void onChanged(List<LastInteractionWrapper> lastInteractions) {
                            for(InteractionType type : types){
                                Objects.requireNonNull(
                                        lastInteractionsMap.get(type.getInteractionTypeName())).clear();
                                counterMap.put(type.getInteractionTypeName(), 0);
                            }

                            for(LastInteractionWrapper interaction : lastInteractions){
                                String currentType = interaction.getType().getInteractionTypeName();

                                Objects.requireNonNull(
                                        lastInteractionsMap.get(currentType)).add(interaction);

                                if(interaction.itsTime()){
                                    counterMap.put(currentType, counterMap.get(currentType) + 1);
                                }
                            }

                            for(int i = 0; i < types.size(); ++i){
                                ((TabLabelWithCounterView)mTabLayout.getTabAt(i).getCustomView())
                                        .setCounter(counterMap.get(types.get(i).getInteractionTypeName()));
                            }

                            mPagerAdapter.setLastInteractionsMap(lastInteractionsMap);
                            mPagerAdapter.notifyDataSetChanged();
                        }
                    });

                    }
                });
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();

        retrieveSelectedTab();
    }

    private void initTabsAndPageViewer(View view) {
        mTabLayout = view.findViewById(R.id.fli_tab_layout);
        mTabLayout.removeAllTabs();

        for(InteractionType type : types){
            TabLabelWithCounterView tcv = new TabLabelWithCounterView(getContext(),
                    type.getInteractionTypeName(), 0);

            mTabLayout.addTab(mTabLayout.newTab().setCustomView(tcv));
        }

        mTabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        final ViewPager viewPager = view.findViewById(R.id.fli_view_pager);
        mPagerAdapter = new LastInteractionsPagerAdapter(getFragmentManager(), types, lastInteractionsMap);

        viewPager.setAdapter(mPagerAdapter);

        viewPager.addOnPageChangeListener(new
                TabLayout.TabLayoutOnPageChangeListener(mTabLayout));

        mTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());

                for(int i = 0; i < types.size(); ++i){
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
        int savedPos = getActivity().getPreferences(MODE_PRIVATE)
                .getInt(getString(R.string.shared_pref_opened_LI_tab), -1);

        selectTab(savedPos);

        SharedPreferences.Editor editor = getActivity().getPreferences(MODE_PRIVATE).edit();
        editor.remove(getString(R.string.shared_pref_opened_LI_tab));
        editor.apply();
    }

    /**
     * Save selected tab position so it can be reopened later
     */
    void saveSelectedTab(){
        int tabPos = mTabLayout.getSelectedTabPosition();

        SharedPreferences preferences = getActivity().getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(getString(R.string.shared_pref_opened_LI_tab), tabPos);
        editor.apply();
    }

    @Override
    public void onDetach() {
        if(MainActivity.getFragmentToLoad() != MainActivity.FragmentToLoad.LAST_INTERACTIONS_FRAGMENT) {
            finishActionMode();
        }
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
    public void nullifyActionMode() {
        getTabFragment().nullifyActionMode();
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
}
