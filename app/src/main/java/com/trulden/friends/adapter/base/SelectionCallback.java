package com.trulden.friends.adapter.base;

import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.view.ActionMode;

import com.trulden.friends.R;
import com.trulden.friends.activity.interfaces.ActivityWithSelection;

/**
 * Handles choose of selection options
 * */
public class SelectionCallback implements ActionMode.Callback {

    private ActivityWithSelection mActivity;
    private CustomRVAdapter mAdapter;

    public SelectionCallback(ActivityWithSelection activity, CustomRVAdapter adapter){
        super();

        mActivity = activity;
        mAdapter = adapter;
    }

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
                mActivity.deleteSelection();
                mode.finish();
                return true;
            }
            case R.id.edit_selection: {
                mActivity.editSelection();
                mode.finish();
                return true;
            }
        }

        return false;
    }

    @Override
    public void onDestroyActionMode(ActionMode mode) {
        mAdapter.clearSelections();
        mActivity.finishActionMode();
    }
}
