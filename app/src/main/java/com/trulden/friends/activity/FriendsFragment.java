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
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.trulden.friends.R;
import com.trulden.friends.activity.interfaces.EditAndDeleteSelection;
import com.trulden.friends.adapter.FriendsAdapter;
import com.trulden.friends.adapter.base.OnClickListener;
import com.trulden.friends.adapter.base.SelectionCallback;
import com.trulden.friends.database.FriendsViewModel;
import com.trulden.friends.database.entity.Friend;
import com.trulden.friends.database.entity.InteractionType;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;

import static com.trulden.friends.util.Util.EXTRA_FRIEND_ID;
import static com.trulden.friends.util.Util.EXTRA_FRIEND_NAME;
import static com.trulden.friends.util.Util.EXTRA_FRIEND_NOTES;
import static com.trulden.friends.util.Util.UPDATE_FRIEND_REQUEST;
import static com.trulden.friends.util.Util.makeToast;

/**
 * Shows list of selectable friends.
 */
public class FriendsFragment extends Fragment implements EditAndDeleteSelection {

    private final static String LOG_TAG = FriendsFragment.class.getCanonicalName();
    private static final String SELECTED_FRIENDS_POSITIONS = "SELECTED_FRIENDS_POSITIONS";

    private FriendsViewModel mFriendsViewModel;
    private FriendsAdapter mFriendsAdapter;

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

        mFriendsViewModel = ViewModelProviders.of(getActivity()).get(FriendsViewModel.class);

        if(savedInstanceState!= null && savedInstanceState.containsKey(SELECTED_FRIENDS_POSITIONS)){
            mSelectedPositions = (HashSet<Integer>) savedInstanceState.getSerializable(SELECTED_FRIENDS_POSITIONS);
        }

        RecyclerView recyclerView = view.findViewById(R.id.ff_recycler_view);
        mFriendsAdapter = new FriendsAdapter(getActivity(), mSelectedPositions);
        recyclerView.setAdapter(mFriendsAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        //mFriendsViewModel = ViewModelProviders.of(Objects.requireNonNull(getActivity())).get(FriendsViewModel.class);

        mFriendsViewModel.getAllFriends().observe(getViewLifecycleOwner(), new Observer<List<Friend>>() {
            @Override
            public void onChanged(List<Friend> friends) {

                view.findViewById(R.id.ff_no_data)
                    .setVisibility(
                        friends == null || friends.size() < 1
                        ? View.VISIBLE
                        : View.GONE
                    );

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
                    Objects.requireNonNull(getActivity()).startActivity(intent);
                }
            }

            @Override
            public void onItemLongClick(View view, Friend obj, int pos) {
                enableActionMode(pos);
            }
        });

        mSelectionCallback = new SelectionCallback(this, mFriendsAdapter);

        if(mSelectedPositions.size() > 0)
            enableActionMode(-1);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(SELECTED_FRIENDS_POSITIONS, mSelectedPositions);
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
            mFriendsAdapter.toggleSelection(pos);
        }

        int count = mFriendsAdapter.getSelectedItemCount();

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

        Objects.requireNonNull(getActivity())
                .startActivityForResult(intent, UPDATE_FRIEND_REQUEST);
    }

    @Override
    public void deleteSelection() {

        List<Friend> selection = new ArrayList<>(mFriendsAdapter.getSelectedItems());
        StringBuilder stringBuilder = new StringBuilder("Friends to be deleted:");

        for(Friend friend : selection){
            stringBuilder.append("\n• " + friend.getName());
        }

        new AlertDialog.Builder(getActivity())
            .setTitle("This will change multi-person interactions and delete solo interactions of selected friends")
            .setMessage(stringBuilder.toString())
            .setPositiveButton("Ok", (dialog, which) -> actuallyDeleteSelection(selection))
            .setNegativeButton("Cancel", null)
            .create()
            .show();
    }

    private void actuallyDeleteSelection(List<Friend> selection){

        if(mActionMode != null) {
            mActionMode.finish();
        }

        int countOfSelectedFriends = selection.size();
        if(countOfSelectedFriends == 1){
            makeToast(getActivity(), "«" + selection.get(0).getName() + "»" + getString(R.string.toast_notice_friend_deleted));
        } else {
            makeToast(getActivity(), getString(R.string.friends_deleted));
        }
        for (Friend friend : selection){
            mFriendsViewModel.delete(friend);
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
}
