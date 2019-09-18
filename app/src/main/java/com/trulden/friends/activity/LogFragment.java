package com.trulden.friends.activity;


import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ActionMode;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.trulden.friends.R;
import com.trulden.friends.activity.interfaces.ActivityWithSelection;
import com.trulden.friends.adapter.LogAdapter;
import com.trulden.friends.adapter.OnClickListener;
import com.trulden.friends.database.FriendsViewModel;
import com.trulden.friends.database.entity.Interaction;
import com.trulden.friends.database.entity.InteractionType;

import java.util.HashMap;
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
 * A simple {@link Fragment} subclass.
 */
public class LogFragment extends Fragment implements ActivityWithSelection {

    public static final String SELECTED_INTERACTIONS_POSITIONS = "SELECTED_INTERACTIONS_POSITIONS";

    private FriendsViewModel mFriendsViewModel;
    private LogAdapter mInteractionsAdapter;

    private ActionModeCallback mActionModeCallback;
    private ActionMode mActionMode;

    private HashSet<Integer> selectedInteractionsPositions = new HashSet<>();
    private HashMap<Long, String> mTypeMap;

    public LogFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_log, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if(savedInstanceState != null && savedInstanceState.containsKey(SELECTED_INTERACTIONS_POSITIONS)){
            selectedInteractionsPositions = (HashSet<Integer>) savedInstanceState.getSerializable(SELECTED_INTERACTIONS_POSITIONS);
        }

        RecyclerView recyclerView = view.findViewById(R.id.log_recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        mInteractionsAdapter = new LogAdapter(getActivity(), selectedInteractionsPositions);

        recyclerView.setAdapter(mInteractionsAdapter);

        mFriendsViewModel = ViewModelProviders.of(getActivity()).get(FriendsViewModel.class);

        mFriendsViewModel.getAllInteractionTypes().observe(this, new Observer<List<InteractionType>>() {
            @Override
            public void onChanged(List<InteractionType> interactionTypes) {
                mTypeMap = new HashMap<>();

                for(InteractionType type : interactionTypes){
                    mTypeMap.put(type.getId(), type.getInteractionTypeName());
                }
                mInteractionsAdapter.setInteractionTypes(mTypeMap);
                mInteractionsAdapter.notifyDataSetChanged();
            }
        });

        mFriendsViewModel.getAllInteractions().observe(this, new Observer<List<Interaction>>() {
            @Override
            public void onChanged(List<Interaction> interactions) {
                mInteractionsAdapter.setInteractions(interactions);
                mInteractionsAdapter.notifyDataSetChanged();
            }
        });

        mInteractionsAdapter.setOnClickListener(new OnClickListener<Interaction>() {
            @Override
            public void onItemClick(View view, Interaction obj, int pos) {
                if(mInteractionsAdapter.getSelectedItemCount() > 0){
                    enableActionMode(pos);
                }
            }

            @Override
            public void onItemLongClick(View view, Interaction obj, int pos) {
                enableActionMode(pos);
            }
        });

        mActionModeCallback = new ActionModeCallback();

        if(selectedInteractionsPositions.size() > 0)
            enableActionMode(-1);
    }

    private void enableActionMode(int pos) {
        if(mActionMode == null){
            mActionMode = ((AppCompatActivity)getActivity()).startSupportActionMode(mActionModeCallback);
        }
        toggleSelection(pos);
    }

    private void toggleSelection(int pos) {
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
                mActionMode.getMenu().findItem(R.id.edit_selection).setVisible(true);
            } else {
                mActionMode.getMenu().findItem(R.id.edit_selection).setVisible(false);
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
        Interaction interaction = mInteractionsAdapter.getSelectedInteractions().get(0);

        intent.putExtra(EXTRA_INTERACTION_ID, interaction.getId());
        intent.putExtra(EXTRA_INTERACTION_TYPE_NAME, mTypeMap.get(interaction.getInteractionTypeId()));
        intent.putExtra(EXTRA_INTERACTION_COMMENT, interaction.getComment());
        intent.putExtra(EXTRA_INTERACTION_DATE, interaction.getDate());
        intent.putExtra(EXTRA_INTERACTION_FRIEND_NAMES, interaction.getFriendNames());

        getActivity().startActivityForResult(intent, UPDATE_INTERACTION_REQUEST);
    }

    @Override
    public void deleteSelection() {
        for(Interaction interaction : mInteractionsAdapter.getSelectedInteractions()) {
            mFriendsViewModel.delete(interaction);
        }
        makeToast(getContext(), getString(R.string.toast_notice_interactions_deleted));
    }


    private class ActionModeCallback implements ActionMode.Callback{

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            mode.getMenuInflater().inflate(R.menu.selection_menu, menu);

            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {

            switch(item.getItemId()) {
                case R.id.delete_selection: {
                    deleteSelection();
                    mode.finish();
                    return true;
                }
                case R.id.edit_selection: {
                    editSelection();
                    mode.finish();
                    return true;
                }
            }

            return false;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            mInteractionsAdapter.clearSelections();
            mActionMode = null;
        }
    }
}
