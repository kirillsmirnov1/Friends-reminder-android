package com.trulden.friends.activity;


import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ActionMode;
import androidx.collection.LongSparseArray;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.trulden.friends.R;
import com.trulden.friends.activity.interfaces.EditAndDeleteSelection;
import com.trulden.friends.adapter.InteractionsRecyclerViewAdapter;
import com.trulden.friends.adapter.base.OnClickListener;
import com.trulden.friends.adapter.base.SelectionCallback;
import com.trulden.friends.database.FriendsViewModel;
import com.trulden.friends.database.entity.Interaction;
import com.trulden.friends.database.entity.InteractionType;
import com.trulden.friends.database.wrappers.FriendName;
import com.trulden.friends.database.wrappers.InteractionWithFriendIDs;

import java.util.HashSet;

import static com.trulden.friends.util.Util.EXTRA_INTERACTION_COMMENT;
import static com.trulden.friends.util.Util.EXTRA_INTERACTION_DATE;
import static com.trulden.friends.util.Util.EXTRA_INTERACTION_FRIEND_NAMES;
import static com.trulden.friends.util.Util.EXTRA_INTERACTION_ID;
import static com.trulden.friends.util.Util.EXTRA_INTERACTION_TYPE_NAME;
import static com.trulden.friends.util.Util.UPDATE_INTERACTION_REQUEST;
import static com.trulden.friends.util.Util.makeToast;

/**
 * Holds selectable {@link Interaction} entries.
 */
public class InteractionsFragment extends Fragment implements EditAndDeleteSelection {

    private FriendsViewModel mViewModel;
    private InteractionsRecyclerViewAdapter mRecyclerViewAdapter;

    private SelectionCallback mSelectionCallback;
    private ActionMode mActionMode;

    private HashSet<Integer> mSelectedPositions = new HashSet<>();
    private LongSparseArray<String> mTypes;

    public InteractionsFragment() {
        // Fragments require public constructor with no args
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_interactions, container, false);
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mViewModel = ViewModelProviders.of(getActivity()).get(FriendsViewModel.class);

        mSelectedPositions = mViewModel.getSelectedPositions(InteractionsFragment.class.getName());

        RecyclerView recyclerView = view.findViewById(R.id.fi_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        mRecyclerViewAdapter = new InteractionsRecyclerViewAdapter(getActivity(), mSelectedPositions);

        recyclerView.setAdapter(mRecyclerViewAdapter);

        mViewModel.getFriendNames().observe(getViewLifecycleOwner(), friendNamesList -> {
            LongSparseArray<String> friendNamesLSA = new LongSparseArray<>();

            for(FriendName friendName : friendNamesList){
                friendNamesLSA.put(friendName.id, friendName.name);
            }

            mRecyclerViewAdapter.setFriends(friendNamesLSA);
            mRecyclerViewAdapter.notifyDataSetChanged();
        });

        mViewModel.getAllInteractionTypes().observe(getViewLifecycleOwner(), interactionTypes -> {
            mTypes = new LongSparseArray<>();

            for(InteractionType type : interactionTypes){
                mTypes.put(type.getId(), type.getInteractionTypeName());
            }
            mRecyclerViewAdapter.setInteractionTypes(mTypes);
            mRecyclerViewAdapter.notifyDataSetChanged();
        });

        mViewModel.getInteractionsWithFriendIDs().observe(getViewLifecycleOwner(), interactionWithFriendIDs -> {

            view.findViewById(R.id.fi_no_data)
                .setVisibility(
                    interactionWithFriendIDs == null || interactionWithFriendIDs.size() < 1
                    ? View.VISIBLE
                    : View.GONE
            );

            mRecyclerViewAdapter.setItems(interactionWithFriendIDs);
            mRecyclerViewAdapter.notifyDataSetChanged();
        });

        mRecyclerViewAdapter.setOnClickListener(new OnClickListener<InteractionWithFriendIDs>() {
            @Override
            public void onItemClick(View view, InteractionWithFriendIDs obj, int pos) {
                if(mRecyclerViewAdapter.getSelectedItemCount() > 0){
                    enableActionMode(pos);
                }
            }

            @Override
            public void onItemLongClick(View view, InteractionWithFriendIDs obj, int pos) {
                enableActionMode(pos);
            }
        });

        mSelectionCallback = new SelectionCallback(this, mRecyclerViewAdapter);

        if(mSelectedPositions.size() > 0)
            enableActionMode(-1);
    }

    @Override
    public void enableActionMode(int pos) {
        if(mActionMode == null){
            mActionMode = ((AppCompatActivity)getActivity()).startSupportActionMode(mSelectionCallback);
        }
        toggleSelection(pos);
    }

    @Override
    public void toggleSelection(int pos) {
        if(pos != -1) {
            mRecyclerViewAdapter.toggleSelection(pos);
        }

        int count = mRecyclerViewAdapter.getSelectedItemCount();

        if(count == 0){
            finishActionMode();
        } else {
            mActionMode.setTitle(String.valueOf(count));
            mActionMode.invalidate();

            if(count == 1){
                mActionMode.getMenu().findItem(R.id.ms_edit).setVisible(true);
            } else {
                mActionMode.getMenu().findItem(R.id.ms_edit).setVisible(false);
            }

        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        mViewModel.setSelectedPositions(InteractionsFragment.class.getName(), mSelectedPositions);
    }

    @Override
    public void editSelection() {
        Intent intent = new Intent(getActivity(), EditInteractionActivity.class);
        InteractionWithFriendIDs iwfids = mRecyclerViewAdapter.getSelectedItems().get(0);
        Interaction interaction = iwfids.interaction;

        intent.putExtra(EXTRA_INTERACTION_ID, interaction.getId());
        intent.putExtra(EXTRA_INTERACTION_TYPE_NAME, mTypes.get(interaction.getInteractionTypeId()));
        intent.putExtra(EXTRA_INTERACTION_COMMENT, interaction.getComment());
        intent.putExtra(EXTRA_INTERACTION_DATE, interaction.getDate());
        intent.putExtra(EXTRA_INTERACTION_FRIEND_NAMES, mRecyclerViewAdapter.generateNameString(iwfids.friendIDs));

        getActivity().startActivityForResult(intent, UPDATE_INTERACTION_REQUEST);
    }

    @Override
    public void deleteSelection() {
        for(InteractionWithFriendIDs interactionWithFriendIDs : mRecyclerViewAdapter.getSelectedItems()) {
            HashSet ids = new HashSet();
            ids.addAll(interactionWithFriendIDs.friendIDs);
            mViewModel.delete(interactionWithFriendIDs.interaction, ids);
        }
        makeToast(getContext(), getString(R.string.toast_notice_interactions_deleted));
    }

    @Override
    public void finishActionMode() {
        mViewModel.clearSelectedPositions();
        if(mActionMode != null) {
            mActionMode.finish();
        }
    }

    @Override
    public void nullifyActionMode() {
        if(mActionMode != null) {
            mActionMode = null;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if(MainActivity.getFragmentToLoad() != MainActivity.FragmentToLoad.INTERACTIONS_FRAGMENT) {
            finishActionMode();
        }
    }
}
