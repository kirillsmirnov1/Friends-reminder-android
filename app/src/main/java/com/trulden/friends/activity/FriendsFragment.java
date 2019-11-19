package com.trulden.friends.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ActionMode;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.trulden.friends.R;
import com.trulden.friends.activity.interfaces.EditAndDeleteSelection;
import com.trulden.friends.activity.interfaces.SelectionWithOnDeleteAlert;
import com.trulden.friends.adapter.FriendsRecyclerViewAdapter;
import com.trulden.friends.adapter.base.OnClickListener;
import com.trulden.friends.adapter.base.SelectionCallback;
import com.trulden.friends.database.FriendsViewModel;
import com.trulden.friends.database.entity.Friend;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;

import static com.trulden.friends.util.Util.EXTRA_FRIEND_ID;
import static com.trulden.friends.util.Util.EXTRA_FRIEND_NAME;
import static com.trulden.friends.util.Util.EXTRA_FRIEND_NOTES;
import static com.trulden.friends.util.Util.UPDATE_FRIEND_REQUEST;
import static com.trulden.friends.util.Util.makeToast;
import static com.trulden.friends.util.Util.openFriendsPage;

/**
 * Shows list of selectable friends.
 */
public class FriendsFragment
        extends Fragment
        implements
            EditAndDeleteSelection,
            SelectionWithOnDeleteAlert<Friend> {

    private final static String LOG_TAG = FriendsFragment.class.getCanonicalName();

    private FriendsViewModel mViewModel;
    private FriendsRecyclerViewAdapter mRecyclerViewAdapter;

    private SelectionCallback mSelectionCallback;
    private ActionMode mActionMode;

    private HashSet<Integer> mSelectedPositions = new HashSet<>();

    public FriendsFragment() {
        // Fragments require public constructor with no args
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_friends, container, false);
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mViewModel = ViewModelProviders.of(getActivity()).get(FriendsViewModel.class);

        mSelectedPositions = mViewModel.getSelectedPositions(FriendsFragment.class.getName());

        RecyclerView recyclerView = view.findViewById(R.id.ff_recycler_view);
        mRecyclerViewAdapter = new FriendsRecyclerViewAdapter(getActivity(), mSelectedPositions);
        recyclerView.setAdapter(mRecyclerViewAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        mViewModel.getAllFriends().observe(getViewLifecycleOwner(), friends -> {
            view.findViewById(R.id.ff_no_data)
                .setVisibility(
                    friends == null || friends.size() < 1
                    ? View.VISIBLE
                    : View.GONE
                );

            mRecyclerViewAdapter.setItems(friends);
            // We need to tell adapter to refresh view, otherwise it might not happen
            mRecyclerViewAdapter.notifyDataSetChanged();
        });

        mRecyclerViewAdapter.setOnClickListener(new OnClickListener<Friend>() {
            @Override
            public void onItemClick(View view, Friend friend, int pos) {
                if(mRecyclerViewAdapter.getSelectedItemCount() > 0){
                    enableActionMode(pos);
                } else {
                    openFriendsPage(getActivity(), friend);
                }
            }

            @Override
            public void onItemLongClick(View view, Friend obj, int pos) {
                enableActionMode(pos);
            }
        });

        mSelectionCallback = new SelectionCallback(this, mRecyclerViewAdapter);

        if(mSelectedPositions.size() > 0)
            enableActionMode(-1);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        mViewModel.setSelectedPositions(FriendsFragment.class.getName(), mSelectedPositions);
    }

    @Override
    public void enableActionMode(int pos) {
        if(mActionMode == null){
            mActionMode = ((AppCompatActivity) Objects.requireNonNull(getActivity()))
                    .startSupportActionMode(mSelectionCallback);
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
    public void onDetach() {
        if(MainActivity.getFragmentToLoad() != MainActivity.FragmentToLoad.FRIENDS_FRAGMENT) {
            finishActionMode();
        }
        super.onDetach();
    }

    @Override
    public void editSelection() {
        Intent intent = new Intent(getActivity(), EditFriendActivity.class);
        Friend friend = mRecyclerViewAdapter.getSelectedItems().get(0);

        intent.putExtra(EXTRA_FRIEND_ID, friend.getId());
        intent.putExtra(EXTRA_FRIEND_NAME, friend.getName());
        intent.putExtra(EXTRA_FRIEND_NOTES, friend.getInfo());

        Objects.requireNonNull(getActivity())
                .startActivityForResult(intent, UPDATE_FRIEND_REQUEST);
    }

    @Override
    public void deleteSelection() {

        List<Friend> selection = new ArrayList<>(mRecyclerViewAdapter.getSelectedItems());
        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder
            .append(getResources().getString(R.string.alert_dialog_delete_all_friends_notice))
            .append(getResources().getString(R.string.alert_dialog_friends_to_be_deleted));

        for(Friend friend : selection){
            stringBuilder
                .append("\n• ")
                .append(friend.getName());
        }

        new AlertDialog.Builder(getActivity())
            .setTitle(getResources().getString(R.string.are_you_sure))
            .setMessage(stringBuilder.toString())
            .setPositiveButton(android.R.string.ok, (dialog, which) -> actuallyDeleteSelection(selection))
            .setNegativeButton(android.R.string.cancel, null)
            .create()
            .show();
    }

    @Override
    public void actuallyDeleteSelection(List<Friend> selection){

        finishActionMode();

        int countOfSelectedFriends = selection.size();
        if(countOfSelectedFriends == 1){
            makeToast(getActivity(), "«" + selection.get(0).getName() + "»" + getString(R.string.toast_notice_friend_deleted));
        } else {
            makeToast(getActivity(), getString(R.string.friends_deleted));
        }
        for (Friend friend : selection){
            mViewModel.delete(friend);
        }
    }

    @Override
    public void finishActionMode() {
        mViewModel.clearSelectedPositions();
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
}
