package com.trulden.friends.activity;


import android.os.Bundle;
import androidx.appcompat.view.ActionMode;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.trulden.friends.R;
import com.trulden.friends.activity.interfaces.LastInteractionsSelection;
import com.trulden.friends.adapter.LastInteractionsAdapter;
import com.trulden.friends.adapter.base.OnClickListener;
import com.trulden.friends.adapter.base.SelectionCallback;
import com.trulden.friends.database.wrappers.LastInteractionWrapper;
import com.trulden.friends.util.Util;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Objects;


/**
 * Holds {@link LastInteractionWrapper} entries of specific type
 */
public class LastInteractionsTabFragment extends Fragment implements LastInteractionsSelection {

    private ArrayList<LastInteractionWrapper> mLastInteractions = new ArrayList<>();
    private static final String SELECTED_LAST_INTERACTIONS_POSITIONS = "SELECTED_LAST_INTERACTIONS_POSITIONS";

    private HashSet<Integer> mSelectedPositions = new HashSet<>();
    private SelectionCallback mSelectionCallback;
    private ActionMode mActionMode;
    private LastInteractionsAdapter mAdapter;

    public LastInteractionsTabFragment() {
        // Required empty public constructor
    }

    public static LastInteractionsTabFragment newInstance(ArrayList<LastInteractionWrapper> lastInteractions){
        LastInteractionsTabFragment tr = new LastInteractionsTabFragment();

        tr.setLastInteractions(lastInteractions);

        return tr;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    private void setLastInteractions(ArrayList<LastInteractionWrapper> lastInteractions) {
        mLastInteractions = lastInteractions;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.page_last_interaction_content, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if(savedInstanceState != null && savedInstanceState.containsKey(SELECTED_LAST_INTERACTIONS_POSITIONS)){
            mSelectedPositions = (HashSet<Integer>) savedInstanceState.getSerializable(SELECTED_LAST_INTERACTIONS_POSITIONS);
        }


        RecyclerView recyclerView = view.findViewById(R.id.pli_recycler_view);
        RecyclerView.LayoutManager mLayout = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mLayout);

        mAdapter = new LastInteractionsAdapter(getContext(), mSelectedPositions);
        mAdapter.setEntries(mLastInteractions);
        recyclerView.setAdapter(mAdapter);

        // Might be unnecessary, might fix issue with empty LI tab
        mAdapter.notifyDataSetChanged();

        mAdapter.setOnClickListener(new OnClickListener<LastInteractionWrapper>() {
            @Override
            public void onItemClick(View view, LastInteractionWrapper obj, int pos) {
                if(mAdapter.getSelectedItemCount() > 0){
                    enableActionMode(pos);
                }
            }

            @Override
            public void onItemLongClick(View view, LastInteractionWrapper obj, int pos) {
                enableActionMode(pos);
            }
        });

        mSelectionCallback = new SelectionCallback(this, mAdapter);

        if(mSelectedPositions.size() > 0){
            enableActionMode(-1);
        }
    }

    @Override
    public void onDetach() {
        if(MainActivity.getFragmentToLoad() != MainActivity.FragmentToLoad.LAST_INTERACTIONS_FRAGMENT) {
            if(mActionMode != null){
                mActionMode.finish();
            }
        }
        super.onDetach();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(SELECTED_LAST_INTERACTIONS_POSITIONS, mSelectedPositions);
    }

    @Override
    public void finishActionMode() {
        if(mActionMode != null){
            mActionMode.finish();
        }
    }

    @Override
    public void nullifyActionMode() {
        if(mActionMode != null){
            mActionMode = null;
        }
    }

    @Override
    public void enableActionMode(int pos) {
        if(mActionMode == null){
            mActionMode = ((AppCompatActivity) Objects.requireNonNull(getActivity()))
                    .startSupportActionMode(mSelectionCallback);
        }
        toggleSelection(pos);
    }

    @Override
    public void toggleSelection(int pos) {
        if(pos != -1){
            mAdapter.toggleSelection(pos);
        }

        int count = mAdapter.getSelectedItemCount();

        if(count == 0){
            mActionMode.finish();
        } else {
            mActionMode.setTitle(String.valueOf(count));
        }
    }

    @Override
    public void hideSelection() {
        // TODO
        Util.makeToast(getActivity(), "TODO hide");
    }

    @Override
    public void unhideSelection() {
        // TODO
        Util.makeToast(getActivity(), "TODO unhide");
    }
}
