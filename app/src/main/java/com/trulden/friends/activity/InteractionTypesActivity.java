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
import com.trulden.friends.activity.interfaces.EditAndDeleteSelection;
import com.trulden.friends.activity.interfaces.EditInteractionType;
import com.trulden.friends.adapter.InteractionTypeAdapter;
import com.trulden.friends.adapter.base.OnClickListener;
import com.trulden.friends.adapter.base.SelectionCallback;
import com.trulden.friends.database.FriendsViewModel;
import com.trulden.friends.database.entity.InteractionType;

import java.util.HashSet;
import java.util.List;

/**
 * Activity in which user can edit and create InteractionTypes
 */
public class InteractionTypesActivity
        extends AppCompatActivity
        implements
        EditAndDeleteSelection,
            EditInteractionType {

    private FriendsViewModel mViewModel;

    private InteractionTypeAdapter mInteractionTypeAdapter;

    private SelectionCallback mSelectionCallback;
    private ActionMode mActionMode;

    private HashSet<Integer> mSelectedPositions = new HashSet<>();
    public static final String SELECTED_TYPES_POSITIONS = "SELECTED_TYPES_POSITIONS";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_interaction_types);

        mViewModel = ViewModelProviders.of(this).get(FriendsViewModel.class);

        if(savedInstanceState!= null && savedInstanceState.containsKey(SELECTED_TYPES_POSITIONS)){
            mSelectedPositions = (HashSet<Integer>) savedInstanceState.getSerializable(SELECTED_TYPES_POSITIONS);
        }

        RecyclerView recyclerView = findViewById(R.id.ait_recycler_view);
        mInteractionTypeAdapter = new InteractionTypeAdapter(this, mSelectedPositions);
        recyclerView.setAdapter(mInteractionTypeAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        mViewModel.getAllInteractionTypes().observe(this, new Observer<List<InteractionType>>() {
            @Override
            public void onChanged(List<InteractionType> interactionTypes) {
                mInteractionTypeAdapter.setEntries(interactionTypes);
                mInteractionTypeAdapter.notifyDataSetChanged();
            }
        });

        mInteractionTypeAdapter.setOnClickListener(new OnClickListener<InteractionType>() {
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

        mSelectionCallback = new SelectionCallback(this, mInteractionTypeAdapter);

        if(mSelectedPositions.size() > 0)
            enableActionMode(-1);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        HashSet<Integer> copyOfSelectedPositions = new HashSet<>();
        copyOfSelectedPositions.addAll(mSelectedPositions);

        outState.putSerializable(SELECTED_TYPES_POSITIONS, copyOfSelectedPositions);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_add, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.ma_add: {
                new EditInteractionTypeDialog(null).show(getSupportFragmentManager(), "editInteractionTypeDialog");
                return true;
            }
            case android.R.id.home: {
                setResult(RESULT_CANCELED, null);
                finish();
                return true;
            }
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void enableActionMode(int pos){
        if(mActionMode == null){
            mActionMode = startSupportActionMode(mSelectionCallback);
        }
        toggleSelection(pos);
    }

    @Override
    public void toggleSelection(int pos) {
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
                mActionMode.getMenu().findItem(R.id.msed_edit).setVisible(true);
            } else {
                mActionMode.getMenu().findItem(R.id.msed_edit).setVisible(false);
            }

        }
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
    protected void onDestroy() {
        if(mActionMode != null){
            mActionMode.finish();
        }
        super.onDestroy();
    }

    @Override
    public void editSelection() {
        InteractionType interactionType = mInteractionTypeAdapter.getSelectedItems().get(0);

        new EditInteractionTypeDialog(interactionType).show(getSupportFragmentManager(), "editInteractionType");
    }

    @Override
    public void deleteSelection() {
        for(InteractionType interactionType : mInteractionTypeAdapter.getSelectedItems()){
            mViewModel.delete(interactionType);
        }
    }

    @Override
    public boolean typeExists(String typeName) {

        for(InteractionType interactionType : mInteractionTypeAdapter.getEntries()){
            if(interactionType.getInteractionTypeName().equals(typeName))
                return true;
        }
        return false;
    }

    @Override
    public void saveType(InteractionType interactionType) {
        if(interactionType.getId() == 0){
            mViewModel.add(interactionType);
        } else {
            mViewModel.update(interactionType);
        }
    }
}
