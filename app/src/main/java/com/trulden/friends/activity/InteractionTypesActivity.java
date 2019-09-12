package com.trulden.friends.activity;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ActionMode;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.trulden.friends.R;
import com.trulden.friends.activity.dialogs.EditInteractionTypeDialog;
import com.trulden.friends.activity.interfaces.ActivityWithSelection;
import com.trulden.friends.activity.interfaces.EditInteractionType;
import com.trulden.friends.adapter.InteractionTypeAdapter;
import com.trulden.friends.database.FriendsViewModel;
import com.trulden.friends.database.entity.InteractionType;

import java.util.HashSet;
import java.util.List;

public class InteractionTypesActivity
        extends AppCompatActivity
        implements
            ActivityWithSelection,
            EditInteractionType {

    private FriendsViewModel mFriendsViewModel;

    private InteractionTypeAdapter mInteractionTypeAdapter;

    private ActionModeCallback mActionModeCallback;
    private ActionMode mActionMode;

    private HashSet<Integer> selectedPositions = new HashSet<>();
    public static final String SELECTED_TYPES_POSITIONS = "SELECTED_TYPES_POSITIONS";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_interaction_types);

        mFriendsViewModel = ViewModelProviders.of(this).get(FriendsViewModel.class);

        if(savedInstanceState!= null && savedInstanceState.containsKey(SELECTED_TYPES_POSITIONS)){
            selectedPositions = (HashSet<Integer>) savedInstanceState.getSerializable(SELECTED_TYPES_POSITIONS);
        }

        RecyclerView recyclerView = findViewById(R.id.interaction_type_recyclerview);
        mInteractionTypeAdapter = new InteractionTypeAdapter(this, selectedPositions);
        recyclerView.setAdapter(mInteractionTypeAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        mFriendsViewModel.getAllInteractionTypes().observe(this, new Observer<List<InteractionType>>() {
            @Override
            public void onChanged(List<InteractionType> interactionTypes) {
                mInteractionTypeAdapter.setInteractionTypes(interactionTypes);
                mInteractionTypeAdapter.notifyDataSetChanged();
            }
        });

        mInteractionTypeAdapter.setOnClickListener(new InteractionTypeAdapter.OnClickListener() {
            @Override
            public void onItemClick(View view, InteractionType obj, int pos) {
                if(mInteractionTypeAdapter.getSelectedItemCount() > 0){
                    enableActionMode(pos);
                }
            }

            @Override
            public void onItemLongClick(View view, InteractionType obj, int pos) {
                enableActionMode(pos);
            }
        });

        mActionModeCallback = new ActionModeCallback();

        if(selectedPositions.size() > 0)
            enableActionMode(-1);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(SELECTED_TYPES_POSITIONS, selectedPositions);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_add, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if(item.getItemId() == R.id.add){
            new EditInteractionTypeDialog(null).show(getSupportFragmentManager(), "editInteractionTypeDialog");
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void enableActionMode(int pos){
        if(mActionMode == null){
            mActionMode = startSupportActionMode(mActionModeCallback); // FIXME turns out to be null
        }
        toggleSelection(pos);
    }

    private void toggleSelection(int pos) {
        if(pos != -1) {
            mInteractionTypeAdapter.toggleSelection(pos);
        }

        int count = mInteractionTypeAdapter.getSelectedItemCount();

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
    protected void onDestroy() {
        if(mActionMode != null){
            mActionMode.finish();
        }
        super.onDestroy();
    }

    @Override
    public void editSelection() {
        // TODO

        InteractionType interactionType = mInteractionTypeAdapter.getSelectedTypes().get(0);

        new EditInteractionTypeDialog(interactionType).show(getSupportFragmentManager(), "editInteractionType");
    }

    @Override
    public void deleteSelection() {
        for(InteractionType interactionType : mInteractionTypeAdapter.getSelectedTypes()){
            mFriendsViewModel.delete(interactionType);
            // TODO manage interactions delete?
        }
    }

    @Override
    public boolean typeExists(String typeName) {

        for(InteractionType interactionType : mInteractionTypeAdapter.getTypes()){
            if(interactionType.getInteractionTypeName().equals(typeName))
                return true;
        }
        return false;
    }

    @Override
    public void saveType(InteractionType interactionType) {
        if(interactionType.getId() == 0){
            mFriendsViewModel.add(interactionType);
        } else {
            mFriendsViewModel.update(interactionType);
        }
    }

    // FIXME can this be turned into outer class?
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
            mInteractionTypeAdapter.clearSelections();
            mActionMode = null;
        }
    }
}
