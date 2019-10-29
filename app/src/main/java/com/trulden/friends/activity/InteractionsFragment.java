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
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.trulden.friends.R;
import com.trulden.friends.activity.interfaces.EditAndDeleteSelection;
import com.trulden.friends.adapter.InteractionsAdapter;
import com.trulden.friends.adapter.base.OnClickListener;
import com.trulden.friends.adapter.base.SelectionCallback;
import com.trulden.friends.database.FriendsViewModel;
import com.trulden.friends.database.entity.Interaction;
import com.trulden.friends.database.entity.InteractionType;
import com.trulden.friends.database.wrappers.FriendName;
import com.trulden.friends.database.wrappers.InteractionWithFriendIDs;

import java.util.HashSet;
import java.util.List;

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
    private static final String SELECTED_INTERACTIONS_POSITIONS = "SELECTED_INTERACTIONS_POSITIONS";

    private FriendsViewModel mViewModel;
    private InteractionsAdapter mInteractionsAdapter;

    private SelectionCallback mSelectionCallback;
    private ActionMode mActionMode;

    private HashSet<Integer> selectedInteractionsPositions = new HashSet<>();
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

        if(savedInstanceState != null && savedInstanceState.containsKey(SELECTED_INTERACTIONS_POSITIONS)){
            selectedInteractionsPositions = (HashSet<Integer>) savedInstanceState.getSerializable(SELECTED_INTERACTIONS_POSITIONS);
        }

        RecyclerView recyclerView = view.findViewById(R.id.fi_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        mInteractionsAdapter = new InteractionsAdapter(getActivity(), selectedInteractionsPositions);

        recyclerView.setAdapter(mInteractionsAdapter);

        //mViewModel = ViewModelProviders.of(getActivity()).get(FriendsViewModel.class);

        mViewModel.getFriendNames().observe(getViewLifecycleOwner(), new Observer<List<FriendName>>() {
            @Override
            public void onChanged(List<FriendName> friendNamesList) {
                LongSparseArray<String> friendNamesLSA = new LongSparseArray<>();

                for(FriendName friendName : friendNamesList){
                    friendNamesLSA.put(friendName.id, friendName.name);
                }

                mInteractionsAdapter.setFriends(friendNamesLSA);
                mInteractionsAdapter.notifyDataSetChanged();
            }
        });

        mViewModel.getAllInteractionTypes().observe(getViewLifecycleOwner(), new Observer<List<InteractionType>>() {
            @Override
            public void onChanged(List<InteractionType> interactionTypes) {
                mTypes = new LongSparseArray<>();

                for(InteractionType type : interactionTypes){
                    mTypes.put(type.getId(), type.getInteractionTypeName());
                }
                mInteractionsAdapter.setInteractionTypes(mTypes);
                mInteractionsAdapter.notifyDataSetChanged();
            }
        });

        mViewModel.getInteractionsWithFriendIDs().observe(getViewLifecycleOwner(), new Observer<List<InteractionWithFriendIDs>>() {
            @Override
            public void onChanged(List<InteractionWithFriendIDs> interactionWithFriendIDs) {

                view.findViewById(R.id.fi_no_data)
                    .setVisibility(
                        interactionWithFriendIDs == null || interactionWithFriendIDs.size() < 1
                        ? View.VISIBLE
                        : View.GONE
                );

                mInteractionsAdapter.setItems(interactionWithFriendIDs);
                mInteractionsAdapter.notifyDataSetChanged();
            }
        });

        mInteractionsAdapter.setOnClickListener(new OnClickListener<InteractionWithFriendIDs>() {
            @Override
            public void onItemClick(View view, InteractionWithFriendIDs obj, int pos) {
                if(mInteractionsAdapter.getSelectedItemCount() > 0){
                    enableActionMode(pos);
                }
            }

            @Override
            public void onItemLongClick(View view, InteractionWithFriendIDs obj, int pos) {
                enableActionMode(pos);
            }
        });

        mSelectionCallback = new SelectionCallback(this, mInteractionsAdapter);

        if(selectedInteractionsPositions.size() > 0)
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
            mInteractionsAdapter.toggleSelection(pos);
        }

        int count = mInteractionsAdapter.getSelectedItemCount();

        if(count == 0){
            mActionMode.finish();
        } else {
            mActionMode.setTitle(String.valueOf(count));
            mActionMode.invalidate();

            if(count == 1){
                mActionMode.getMenu().findItem(R.id.msed_edit).setVisible(true);
            } else {
                mActionMode.getMenu().findItem(R.id.msed_edit).setVisible(false);
            }

        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(SELECTED_INTERACTIONS_POSITIONS, selectedInteractionsPositions);
    }

    @Override
    public void editSelection() {
        Intent intent = new Intent(getActivity(), EditInteractionActivity.class);
        InteractionWithFriendIDs iwfids = mInteractionsAdapter.getSelectedItems().get(0);
        Interaction interaction = iwfids.interaction;

        intent.putExtra(EXTRA_INTERACTION_ID, interaction.getId());
        intent.putExtra(EXTRA_INTERACTION_TYPE_NAME, mTypes.get(interaction.getInteractionTypeId()));
        intent.putExtra(EXTRA_INTERACTION_COMMENT, interaction.getComment());
        intent.putExtra(EXTRA_INTERACTION_DATE, interaction.getDate());
        intent.putExtra(EXTRA_INTERACTION_FRIEND_NAMES, mInteractionsAdapter.generateNameString(iwfids.friendIDs));

        getActivity().startActivityForResult(intent, UPDATE_INTERACTION_REQUEST);
    }

    @Override
    public void deleteSelection() {
        for(InteractionWithFriendIDs interactionWithFriendIDs : mInteractionsAdapter.getSelectedItems()) {
            HashSet ids = new HashSet();
            ids.addAll(interactionWithFriendIDs.friendIDs);
            mViewModel.delete(interactionWithFriendIDs.interaction, ids);
        }
        makeToast(getContext(), getString(R.string.toast_notice_interactions_deleted));
    }

    @Override
    public void finishActionMode() {
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
        if(mActionMode != null && MainActivity.getFragmentToLoad() != MainActivity.FragmentToLoad.INTERACTIONS_FRAGMENT) {
            mActionMode.finish();
        }
    }
}
