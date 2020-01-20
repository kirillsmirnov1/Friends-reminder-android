package com.trulden.friends.activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ActionMode;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.trulden.friends.R;
import com.trulden.friends.activity.dialogs.EditInteractionTypeDialog;
import com.trulden.friends.activity.interfaces.EditAndDeleteSelection;
import com.trulden.friends.activity.interfaces.EditInteractionType;
import com.trulden.friends.activity.interfaces.SelectionWithOnDeleteAlert;
import com.trulden.friends.adapter.InteractionTypeRecyclerViewAdapter;
import com.trulden.friends.adapter.base.OnClickListener;
import com.trulden.friends.adapter.base.SelectionCallback;
import com.trulden.friends.database.FriendsViewModel;
import com.trulden.friends.database.entity.InteractionType;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * Activity in which user can edit and create InteractionTypes
 */
public class InteractionTypesActivity
        extends AppCompatActivity
        implements
            EditAndDeleteSelection,
            EditInteractionType,
            SelectionWithOnDeleteAlert<InteractionType> {

    private FriendsViewModel mViewModel;

    private InteractionTypeRecyclerViewAdapter mRecyclerViewAdapter;

    private SelectionCallback mSelectionCallback;
    private ActionMode mActionMode;

    private HashSet<Integer> mSelectedPositions = new HashSet<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_interaction_types);

        mViewModel = ViewModelProviders.of(this).get(FriendsViewModel.class);

        mSelectedPositions = mViewModel.getSelectedPositions(InteractionTypesActivity.class.getName());

        RecyclerView recyclerView = findViewById(R.id.ait_recycler_view);
        mRecyclerViewAdapter = new InteractionTypeRecyclerViewAdapter(this, mSelectedPositions);
        recyclerView.setAdapter(mRecyclerViewAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        mViewModel.getAllInteractionTypes().observe(this, interactionTypes -> {

            findViewById(R.id.ait_no_data)
                .setVisibility(
                    interactionTypes == null || interactionTypes.size() < 1
                    ? View.VISIBLE
                    : View.GONE
                );

            mRecyclerViewAdapter.setItems(interactionTypes);
            mRecyclerViewAdapter.notifyDataSetChanged();
        });

        mRecyclerViewAdapter.setOnClickListener(new OnClickListener<InteractionType>() {
            @Override
            public void onItemClick(View view, InteractionType obj, int pos) {
                if(mRecyclerViewAdapter.getSelectedItemCount() > 0){
                    enableActionMode(pos);
                }
            }

            @Override
            public void onItemLongClick(View view, InteractionType obj, int pos) {
                enableActionMode(pos);
            }
        });

        mSelectionCallback = new SelectionCallback(this, mRecyclerViewAdapter);

        if(mSelectedPositions.size() > 0)
            enableActionMode(-1);
    }

    @Override
    protected void onSaveInstanceState(@NotNull Bundle outState) {
        super.onSaveInstanceState(outState);

        mViewModel.setSelectedPositions(InteractionTypesActivity.class.getName(), mSelectedPositions);
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
            mRecyclerViewAdapter.toggleSelection(pos);
        }

        int count = mRecyclerViewAdapter.getSelectedItemCount();

        if(count == 0){
            finishActionMode();
        } else {
            mActionMode.setTitle(String.valueOf(count));
            mActionMode.invalidate();

            if(count == 1){
                mActionMode.getMenu().findItem(R.id.mam_edit).setVisible(true);
            } else {
                mActionMode.getMenu().findItem(R.id.mam_edit).setVisible(false);
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
    public void onActionModeFinished() {
        mViewModel.clearSelectedPositions();
        mActionMode = null;
    }

    @Override
    public void editSelection() {
        InteractionType interactionType = mRecyclerViewAdapter.getSelectedItems().get(0);

        new EditInteractionTypeDialog(interactionType).show(getSupportFragmentManager(), "editInteractionType");
    }

    @Override
    public void deleteSelection() {

        List<InteractionType> selection = new ArrayList<>(mRecyclerViewAdapter.getSelectedItems());
        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder
            .append(getResources().getString(R.string.alert_dialog_delete_all_types_notice))
            .append(getResources().getString(R.string.alert_dialog_types_to_be_deleted));

        for(InteractionType type : selection){
            stringBuilder
                .append("\n• ")
                .append(type.getInteractionTypeName());
        }

        new AlertDialog.Builder(this)
            .setTitle(getResources().getString(R.string.are_you_sure))
            .setMessage(stringBuilder.toString())
            .setPositiveButton(android.R.string.ok, (dialog, which) -> actuallyDeleteSelection(selection))
            .setNegativeButton(android.R.string.cancel, null)
            .create()
            .show();
    }

    @Override
    public void actuallyDeleteSelection(List<InteractionType> selection) {
        finishActionMode();

        for(InteractionType interactionType : selection){
            mViewModel.delete(interactionType);
        }
    }

    @Override
    public boolean typeExists(String typeName) {

        for(InteractionType interactionType : mRecyclerViewAdapter.getItems()){
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
