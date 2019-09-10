package com.trulden.friends.activity;


import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ActionMode;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.trulden.friends.R;
import com.trulden.friends.adapter.LogAdapter;
import com.trulden.friends.database.FriendsViewModel;
import com.trulden.friends.database.entity.Interaction;
import com.trulden.friends.database.entity.InteractionType;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class LogFragment extends Fragment implements FragmentWithSelection{

    public static final String SELECTED_INTERACTIONS_POSITIONS = "SELECTED_INTERACTIONS_POSITIONS";

    private FriendsViewModel mFriendsViewModel;
    private LogAdapter mLogAdapter;

    private ActionModeCallback mActionModeCallback;
    private ActionMode mActionMode;

    private HashSet<Integer> selectedInteractionsPositions = new HashSet<>();

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

        mLogAdapter = new LogAdapter(getActivity(), selectedInteractionsPositions);

        recyclerView.setAdapter(mLogAdapter);

        mFriendsViewModel = ViewModelProviders.of(getActivity()).get(FriendsViewModel.class);

        mFriendsViewModel.getAllInteractionTypes().observe(this, new Observer<List<InteractionType>>() {
            @Override
            public void onChanged(List<InteractionType> interactionTypes) {
                HashMap<Long, String> typeMap = new HashMap<>();

                for(InteractionType type : interactionTypes){
                    typeMap.put(type.getId(), type.getInteractionTypeName());
                }
                mLogAdapter.setInteractionTypes(typeMap);
                mLogAdapter.notifyDataSetChanged();
            }
        });

        mFriendsViewModel.getAllInteractions().observe(this, new Observer<List<Interaction>>() {
            @Override
            public void onChanged(List<Interaction> interactions) {
                mLogAdapter.setInteractions(interactions);
                mLogAdapter.notifyDataSetChanged();
            }
        });

        mLogAdapter.setOnClickListener(new LogAdapter.OnClickListener() {
            @Override
            public void onItemClick(View view, Interaction obj, int pos) {
                if(mLogAdapter.getSelectedItemCount() > 0){
                    enableActionMode(pos);
                } else {
                    // TODO open log entry
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
            mLogAdapter.toggleSelection(pos);
        }

        int count = mLogAdapter.getSelectedItemCount();

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
        // TODO
    }

    @Override
    public void deleteSelection() {
        // TODO
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
            mLogAdapter.clearSelections();
            mActionMode = null;
        }
    }
}
