package com.trulden.friends.adapter.base;

import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.view.ActionMode;

import com.trulden.friends.R;
import com.trulden.friends.activity.interfaces.BasicSelection;
import com.trulden.friends.activity.interfaces.EditAndDeleteSelection;
import com.trulden.friends.activity.interfaces.LastInteractionsSelection;

/**
 * Handles choose of selection options
 * */
public class SelectionCallback implements ActionMode.Callback {

    private BasicSelection mActivity;
    private CustomRVAdapter mAdapter;

    public SelectionCallback(BasicSelection activity, CustomRVAdapter adapter){
        super();

        mActivity = activity;
        mAdapter = adapter;
    }

    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        if(mActivity instanceof EditAndDeleteSelection) {
            mode.getMenuInflater().inflate(R.menu.menu_selection, menu);
            // TODO rename this menu
        }

        if(mActivity instanceof LastInteractionsSelection){
            mode.getMenuInflater().inflate(R.menu.menu_selection_last_interactions, menu);
        }

        return true;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
        return false;
    }

    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {

        switch (item.getItemId()) {
            case R.id.menu_selection_delete: {
                ((EditAndDeleteSelection) mActivity).deleteSelection();
                mode.finish();
                return true;
            }
            case R.id.menu_selection_edit: {
                ((EditAndDeleteSelection) mActivity).editSelection();
                mode.finish();
                return true;
            }
            case R.id.msli_hide: {
                ((LastInteractionsSelection) mActivity).hideSelection();
                mode.finish();
                return true;
            }
            case R.id.msli_unhide: {
                ((LastInteractionsSelection) mActivity).unhideSelection();
                mode.finish();
                return true;
            }
        }

        return false;
    }

    @Override
    public void onDestroyActionMode(ActionMode mode) {
        mAdapter.clearSelections();
        mActivity.nullifyActionMode();
    }
}
