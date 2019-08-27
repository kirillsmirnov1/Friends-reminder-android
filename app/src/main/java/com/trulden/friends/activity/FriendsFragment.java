package com.trulden.friends.activity;


import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ActionMode;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.trulden.friends.R;
import com.trulden.friends.adapter.FriendsAdapter;
import com.trulden.friends.database.Friend;
import com.trulden.friends.database.FriendsViewModel;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class FriendsFragment extends Fragment implements FragmentWithSelection{

    private final static String LOG_TAG = FriendsFragment.class.getCanonicalName();

    private FriendsViewModel mFriendsViewModel;
    private FriendsAdapter mFriendsAdapter;


    private ActionModeCallback mActionModeCallback;
    private ActionMode mActionMode;

    public FriendsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_friends, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView recyclerView = view.findViewById(R.id.friends_recyclerView);
        mFriendsAdapter = new FriendsAdapter(getActivity());
        recyclerView.setAdapter(mFriendsAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        mFriendsViewModel = ViewModelProviders.of(getActivity()).get(FriendsViewModel.class);

        mFriendsViewModel.getAllFriends().observe(this, new Observer<List<Friend>>() {
            @Override
            public void onChanged(List<Friend> friends) {
                mFriendsAdapter.setFriends(friends);
                // We need to tell adapter to refresh view, otherwise it might not happen
                mFriendsAdapter.notifyDataSetChanged();
            }
        });

        mFriendsAdapter.setOnClickListener(new FriendsAdapter.OnClickListener() {
            @Override
            public void onItemClick(View view, Friend obj, int pos) {
                if(mFriendsAdapter.getSelectedItemCount() > 0){
                    enableActionMode(pos);
                } else {
                    // TODO open friend page
                    Toast.makeText(getContext(), "click", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onItemLongClick(View view, Friend obj, int pos) {
                enableActionMode(pos);
            }
        });

        mActionModeCallback = new ActionModeCallback();

    }

    private void enableActionMode(int pos) {
        if(mActionMode == null){
            mActionMode = ((AppCompatActivity)getActivity()).startSupportActionMode(mActionModeCallback);
        }
        toggleSelection(pos);
    }

    private void toggleSelection(int pos) {
        mFriendsAdapter.toggleSelection(pos);
        int count = mFriendsAdapter.getSelectedItemCount();

        if(count == 0){
            mActionMode.finish();
        } else {
            mActionMode.setTitle(String.valueOf(count));
            mActionMode.invalidate();
        }
    }

    @Override
    public void clearSelection() {
        // TODO
    }

    @Override
    public void editSelection() {
        // TODO
    }

    @Override
    public void deleteSelection() {
        for (Friend friend : mFriendsAdapter.getSelectedFriends()){
            mFriendsViewModel.deleteFriend(friend);
        }
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
                case R.id.clear_selection: {
                    clearSelection();
                    mode.finish();
                    return true;
                }
            }

            return false;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            mFriendsAdapter.clearSelections();
            mActionMode = null;
        }
    }
}
