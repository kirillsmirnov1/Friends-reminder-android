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
import com.trulden.friends.adapter.FriendsAdapter;
import com.trulden.friends.adapter.OnClickListener;
import com.trulden.friends.database.FriendsViewModel;
import com.trulden.friends.database.entity.Friend;

import java.util.HashSet;
import java.util.List;

import static com.trulden.friends.util.Util.EXTRA_FRIEND_ID;
import static com.trulden.friends.util.Util.EXTRA_FRIEND_NOTES;
import static com.trulden.friends.util.Util.EXTRA_FRIEND_NAME;
import static com.trulden.friends.util.Util.UPDATE_FRIEND_REQUEST;
import static com.trulden.friends.util.Util.makeToast;

public class FriendsFragment extends Fragment implements ActivityWithSelection{

    private final static String LOG_TAG = FriendsFragment.class.getCanonicalName();
    private static final String SELECTED_FRIENDS_POSITIONS = "SELECTED_FRIENDS_POSITIONS";

    private FriendsViewModel mFriendsViewModel;
    private FriendsAdapter mFriendsAdapter;

    private ActionModeCallback mActionModeCallback;
    private ActionMode mActionMode;

    private HashSet<Integer> selectedFriendsPositions = new HashSet<>();

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

        if(savedInstanceState!= null && savedInstanceState.containsKey(SELECTED_FRIENDS_POSITIONS)){
            selectedFriendsPositions = (HashSet<Integer>) savedInstanceState.getSerializable(SELECTED_FRIENDS_POSITIONS);
        }

        RecyclerView recyclerView = view.findViewById(R.id.friends_recyclerView);
        mFriendsAdapter = new FriendsAdapter(getActivity(), selectedFriendsPositions);
        recyclerView.setAdapter(mFriendsAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        mFriendsViewModel = ViewModelProviders.of(getActivity()).get(FriendsViewModel.class);

        mFriendsViewModel.getAllFriends().observe(this, new Observer<List<Friend>>() {
            @Override
            public void onChanged(List<Friend> friends) {
                mFriendsAdapter.setEntries(friends);
                // We need to tell adapter to refresh view, otherwise it might not happen
                mFriendsAdapter.notifyDataSetChanged();
            }
        });

        mFriendsAdapter.setOnClickListener(new OnClickListener<Friend>() {
            @Override
            public void onItemClick(View view, Friend friend, int pos) {
                if(mFriendsAdapter.getSelectedItemCount() > 0){
                    enableActionMode(pos);
                } else {
                    Intent intent = new Intent(getActivity(), FriendPageActivity.class);
                    intent.putExtra(EXTRA_FRIEND_ID, friend.getId());
                    intent.putExtra(EXTRA_FRIEND_NAME, friend.getName());
                    intent.putExtra(EXTRA_FRIEND_NOTES, friend.getInfo());
                    getActivity().startActivity(intent);
                }
            }

            @Override
            public void onItemLongClick(View view, Friend obj, int pos) {
                enableActionMode(pos);
            }
        });

        mActionModeCallback = new ActionModeCallback();

        if(selectedFriendsPositions.size() > 0)
            enableActionMode(-1);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(SELECTED_FRIENDS_POSITIONS, selectedFriendsPositions);
    }

    private void enableActionMode(int pos) {
        if(mActionMode == null){
            mActionMode = ((AppCompatActivity)getActivity()).startSupportActionMode(mActionModeCallback);
        }
        toggleSelection(pos);
    }

    private void toggleSelection(int pos) {
        if(pos != -1) {
            mFriendsAdapter.toggleSelection(pos);
        }

        int count = mFriendsAdapter.getSelectedItemCount();

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
    public void onDetach() {
        if(MainActivity.getFragmentToLoad() != MainActivity.FragmentToLoad.FRIENDS_FRAGMENT) {
            if(mActionMode != null) {
                mActionMode.finish();
            }
        }
        super.onDetach();
    }

    @Override
    public void editSelection() {
        Intent intent = new Intent(getActivity(), EditFriendActivity.class);
        Friend friend = mFriendsAdapter.getSelectedItems().get(0);

        intent.putExtra(EXTRA_FRIEND_ID, friend.getId());
        intent.putExtra(EXTRA_FRIEND_NAME, friend.getName());
        intent.putExtra(EXTRA_FRIEND_NOTES, friend.getInfo());

        getActivity().startActivityForResult(intent, UPDATE_FRIEND_REQUEST);
    }

    @Override
    public void deleteSelection() {
        int countOfSelectedFriends = mFriendsAdapter.getSelectedItemCount();
        if(countOfSelectedFriends == 1){
            makeToast(getActivity(), "«" + mFriendsAdapter.getSelectedItems().get(0).getName() + "»" + getString(R.string.toast_notice_friend_deleted));
        } else {
            makeToast(getActivity(), getString(R.string.friends_deleted));
        }
        for (Friend friend : mFriendsAdapter.getSelectedItems()){
            mFriendsViewModel.delete(friend);
        }
    }

    @Override
    public void nullActionMode() {
        mActionMode = null;
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
            mFriendsAdapter.clearSelections();
            mActionMode = null;
        }
    }
}
