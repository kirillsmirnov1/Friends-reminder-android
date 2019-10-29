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
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.trulden.friends.R;
import com.trulden.friends.activity.interfaces.LastInteractionsSelection;
import com.trulden.friends.adapter.LastInteractionsRecyclerViewAdapter;
import com.trulden.friends.adapter.base.OnClickListener;
import com.trulden.friends.adapter.base.SelectionCallback;
import com.trulden.friends.database.FriendsViewModel;
import com.trulden.friends.database.entity.LastInteraction;
import com.trulden.friends.database.wrappers.LastInteractionWrapper;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Objects;

import static com.trulden.friends.adapter.LastInteractionsRecyclerViewAdapter.TrackerMode.SHOW_FRIEND_NAME;


/**
 * Holds {@link LastInteractionWrapper} entries of specific type
 */
public class LastInteractionsTabFragment extends Fragment implements LastInteractionsSelection {

    private FriendsViewModel mViewModel;

    private ArrayList<LastInteractionWrapper> mLastInteractions = new ArrayList<>();
    private String mTypeName;

    private String SELECTED_LAST_INTERACTIONS_POSITIONS = "SELECTED_LAST_INTERACTIONS_POSITIONS";

    private HashSet<Integer> mSelectedPositions = new HashSet<>();
    private SelectionCallback mSelectionCallback;
    private ActionMode mActionMode;
    private LastInteractionsRecyclerViewAdapter mRecyclerViewAdapter;

    public LastInteractionsTabFragment() {
        // Required empty public constructor
    }

    public static LastInteractionsTabFragment newInstance(String typeName, ArrayList<LastInteractionWrapper> lastInteractions){
        LastInteractionsTabFragment tr = new LastInteractionsTabFragment();

        tr.setTypeName(typeName);
        tr.setLastInteractions(lastInteractions);

        return tr;
    }

    private void setTypeName(String typeName) {
        mTypeName = typeName;
        SELECTED_LAST_INTERACTIONS_POSITIONS += mTypeName;
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
        return inflater.inflate(R.layout.fragment_last_interaction_tab, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        view.findViewById(R.id.flit_no_data)
            .setVisibility(
                mLastInteractions == null || mLastInteractions.size() < 1
                ? View.VISIBLE
                : View.GONE
            );

        mViewModel = ViewModelProviders.of(getActivity()).get(FriendsViewModel.class);

        if(mTypeName != null){
            mSelectedPositions = mViewModel.getLITFSelections(mTypeName);
        }


        RecyclerView recyclerView = view.findViewById(R.id.flit_recycler_view);
        RecyclerView.LayoutManager mLayout = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mLayout);

        mRecyclerViewAdapter = new LastInteractionsRecyclerViewAdapter(getContext(), mSelectedPositions, SHOW_FRIEND_NAME);
        mRecyclerViewAdapter.setItems(mLastInteractions);
        recyclerView.setAdapter(mRecyclerViewAdapter);

        // Might be unnecessary, might fix issue with empty LI tab
        mRecyclerViewAdapter.notifyDataSetChanged();

        mRecyclerViewAdapter.setOnClickListener(new OnClickListener<LastInteractionWrapper>() {
            @Override
            public void onItemClick(View view, LastInteractionWrapper obj, int pos) {
                if(mRecyclerViewAdapter.getSelectedItemCount() > 0){
                    toggleSelection(pos);
                }
            }

            @Override
            public void onItemLongClick(View view, LastInteractionWrapper obj, int pos) {
                if(mRecyclerViewAdapter.getSelectedItemCount() > 0){
                    toggleSelection(pos);
                } else {
                    enableActionMode(pos);
                }
            }
        });

        mSelectionCallback = new SelectionCallback(this, mRecyclerViewAdapter);

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
        if(mTypeName!= null && !mTypeName.isEmpty()) {
            mViewModel.setLITF_selections(mTypeName, mSelectedPositions);
        }
    }

    @Override
    public void finishActionMode() {
        if(mActionMode != null){
            mActionMode.finish();
        }
    }

    @Override
    public void nullifyActionMode() {
        mViewModel.clearLITFSelections();
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

        mActionMode.getMenu().findItem(R.id.msli_unhide)
                .setVisible(mViewModel.getShowHiddenLIValue());

        toggleSelection(pos);
    }

    @Override
    public void toggleSelection(int pos) {
        if(pos != -1){
            mRecyclerViewAdapter.toggleSelection(pos);
        }

        int count = mRecyclerViewAdapter.getSelectedItemCount();

        if(count == 0){
            mActionMode.finish();
        } else {
            mActionMode.setTitle(String.valueOf(count));
        }
    }

    @Override
    public void hideSelection() {
        for(LastInteractionWrapper interactionWrapper : mRecyclerViewAdapter.getSelectedItems()){

            LastInteraction interaction = interactionWrapper.getLastInteraction();

            interaction.setStatus(LastInteractionWrapper.LastInteractionStatus.HIDDEN.ordinal());

            mViewModel.update(interaction);
        }
    }

    @Override
    public void unhideSelection() {
        for(LastInteractionWrapper interactionWrapper : mRecyclerViewAdapter.getSelectedItems()){
            LastInteraction interaction = interactionWrapper.getLastInteraction();

            interaction.setStatus(LastInteractionWrapper.LastInteractionStatus.DEFAULT.ordinal());

            mViewModel.update(interaction);
        }
    }
}
