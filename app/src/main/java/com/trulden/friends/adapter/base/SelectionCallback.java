package com.trulden.friends.adapter.base;

import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.view.ActionMode;

import com.trulden.friends.R;
import com.trulden.friends.activity.interfaces.BasicSelection;
import com.trulden.friends.activity.interfaces.EditAndDeleteSelection;
import com.trulden.friends.activity.interfaces.LastInteractionsSelection;
import com.trulden.friends.activity.interfaces.SelectionWithOnDeleteAlert;
import com.trulden.friends.activity.interfaces.ShareSelection;

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

        mode.getMenuInflater().inflate(R.menu.menu_action_mode, menu);

        // By default, all options are invisible.
        // Need to set required ones visible

        if(mActivity instanceof ShareSelection){
            menu.findItem(R.id.mam_share).setVisible(true);
        }

        if(mActivity instanceof EditAndDeleteSelection) {
            menu.findItem(R.id.mam_delete).setVisible(true);
            menu.findItem(R.id.mam_edit).setVisible(true);
        }

        if(mActivity instanceof LastInteractionsSelection){
            menu.findItem(R.id.mam_hide).setVisible(true);
            menu.findItem(R.id.mam_unhide).setVisible(true);
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
            case R.id.mam_share: {
                ((ShareSelection) mActivity).shareSelection();
                mode.finish();
                return true;
            }
            case R.id.mam_delete: {
                ((EditAndDeleteSelection) mActivity).deleteSelection();

                // Those should handle finish by themselves
                if(!(mActivity instanceof SelectionWithOnDeleteAlert)) {
                    mode.finish();
                }
                return true;
            }
            case R.id.mam_edit: {
                ((EditAndDeleteSelection) mActivity).editSelection();
                mode.finish();
                return true;
            }
            case R.id.mam_hide: {
                ((LastInteractionsSelection) mActivity).hideSelection();
                mode.finish();
                return true;
            }
            case R.id.mam_unhide: {
                ((LastInteractionsSelection) mActivity).unhideSelection();
                mode.finish();
                return true;
            }
        }

        return false;
    }

    @Override
    public void onDestroyActionMode(ActionMode mode) {
        mActivity.onActionModeFinished();

        mAdapter.clearSelections();
    }
}
