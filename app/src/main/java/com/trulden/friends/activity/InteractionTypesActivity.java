package com.trulden.friends.activity;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ActionMode;
import androidx.lifecycle.ViewModelProviders;

import com.trulden.friends.R;
import com.trulden.friends.adapter.InteractionTypeAdapter;
import com.trulden.friends.database.FriendsViewModel;

import java.util.HashSet;

public class InteractionTypesActivity extends AppCompatActivity {

    private FriendsViewModel mFriendsViewModel;

    private InteractionTypeAdapter mInteractionTypeAdapter;

    private ActionModeCallback mActionModeCallback;
    private ActionMode mActionMode;

    private HashSet<Integer> selectedPositions = new HashSet<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_interaction_types);

        mFriendsViewModel = ViewModelProviders.of(this).get(FriendsViewModel.class);
    }

    // FIXME can this be turned into outer class?
    private class ActionModeCallback implements ActionMode.Callback{

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {

            // TODO add menu

            return false;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {

            // TODO add menu options

            return false;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            mInteractionTypeAdapter.clearSelections();
            mActionMode = null;
        }
    }
}
