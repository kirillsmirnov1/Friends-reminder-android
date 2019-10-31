package com.trulden.friends.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.trulden.friends.R;
import com.trulden.friends.activity.interfaces.LastInteractionsSelection;
import com.trulden.friends.adapter.LastInteractionsRecyclerViewAdapter;
import com.trulden.friends.adapter.base.OnClickListener;
import com.trulden.friends.adapter.base.SelectionCallback;
import com.trulden.friends.database.FriendsViewModel;
import com.trulden.friends.database.entity.Friend;

import java.util.HashSet;
import java.util.Objects;

import static com.trulden.friends.adapter.LastInteractionsRecyclerViewAdapter.TrackerMode.SHOW_TYPE_NAME;
import static com.trulden.friends.util.Util.EXTRA_FRIEND_ID;
import static com.trulden.friends.util.Util.EXTRA_FRIEND_NAME;
import static com.trulden.friends.util.Util.EXTRA_FRIEND_NOTES;
import static com.trulden.friends.util.Util.UPDATE_FRIEND_REQUEST;
import static com.trulden.friends.util.Util.makeToast;

/**
 * Shows Friend data
 */
public class FriendPageActivity
    extends AppCompatActivity
    implements LastInteractionsSelection {

    private TextView mPersonNotes;
    private View mNotesTrackersDivider;
    private TextView mLISubhead;

    private Friend mFriend;

    private FriendsViewModel mViewModel;
    private LastInteractionsRecyclerViewAdapter mRecyclerViewAdapter;
    private HashSet<Integer> mSelectedPositions;
    private SelectionCallback mSelectionCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_page);

        mViewModel = ViewModelProviders.of(this).get(FriendsViewModel.class);

        mPersonNotes = findViewById(R.id.afp_notes);
        mNotesTrackersDivider = findViewById(R.id.afp_notes_tracker_divider);
        mLISubhead = findViewById(R.id.afp_LI_subhead);

        Intent intent = getIntent();

        mFriend = new Friend(
                intent.getLongExtra(EXTRA_FRIEND_ID, -1),
                Objects.requireNonNull(intent.getStringExtra(EXTRA_FRIEND_NAME)),
                intent.getStringExtra(EXTRA_FRIEND_NOTES));

        setFriendInfo(mFriend);

        mSelectedPositions = mViewModel.getLITFSelections(FriendPageActivity.class.getName());

        RecyclerView recyclerView = findViewById(R.id.afp_LI_recycler_view);
        mRecyclerViewAdapter = new LastInteractionsRecyclerViewAdapter(this, mSelectedPositions, SHOW_TYPE_NAME);
        recyclerView.setAdapter(mRecyclerViewAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        mViewModel.getLastInteractionsOfAFriend(mFriend.getId()).observe(this, lastInteractionWrappers -> {

            if(lastInteractionWrappers.size() == 0){
                mLISubhead.setVisibility(View.GONE);
                recyclerView.setVisibility(View.GONE);
            } else {
                mLISubhead.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.VISIBLE);

                if(!mFriend.getInfo().isEmpty()){
                    mNotesTrackersDivider.setVisibility(View.VISIBLE);
                }
            }

            mRecyclerViewAdapter.setItems(lastInteractionWrappers);
            mRecyclerViewAdapter.notifyDataSetChanged();
        });

        mRecyclerViewAdapter.setOnClickListener(new OnClickListener() {
            @Override
            public void onItemClick(View view, Object obj, int pos) {
                if(mRecyclerViewAdapter.getSelectedItemCount() > 0){
                    toggleSelection(pos);
                } else {
                    //TODO
                    //((MainActivity) getActivity()).showTracker(lastInteractionWrapper);
                }
            }

            @Override
            public void onItemLongClick(View view, Object obj, int pos) {
                if(mRecyclerViewAdapter.getSelectedItemCount() > 0){
                    toggleSelection(pos);
                } else {
                    enableActionMode(pos);
                }
            }
        });

        mSelectionCallback = new SelectionCallback(this, mRecyclerViewAdapter);

        if(mSelectedPositions.size() > 0){
            enableActionMode(-1);
        }
    }

    private void setFriendInfo(Friend friend){
        mFriend = friend;
        Objects.requireNonNull(getSupportActionBar()).setTitle(friend.getName());
        mPersonNotes.setText(friend.getInfo());

        if(friend.getInfo().isEmpty()){
            mPersonNotes.setVisibility(View.GONE);
            mNotesTrackersDivider.setVisibility(View.GONE);
        } else {
            mPersonNotes.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_selection_edit_delete, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()){
            case R.id.msed_edit: {
                Intent intent = new Intent(this, EditFriendActivity.class);

                intent.putExtra(EXTRA_FRIEND_ID, mFriend.getId());
                intent.putExtra(EXTRA_FRIEND_NAME, mFriend.getName());
                intent.putExtra(EXTRA_FRIEND_NOTES, mFriend.getInfo());

                startActivityForResult(intent, UPDATE_FRIEND_REQUEST);
                break;
            }

            case R.id.msed_delete: {
                mViewModel.delete(mFriend);
                makeToast(this, "«" + mFriend.getName() + "»" + getString(R.string.toast_notice_friend_deleted));
                finish();
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent resultingIntent) {
        super.onActivityResult(requestCode, resultCode, resultingIntent);

        if (requestCode == UPDATE_FRIEND_REQUEST) {
            if (resultCode == RESULT_OK) {
                assert resultingIntent != null;
                long id = resultingIntent.getLongExtra(EXTRA_FRIEND_ID, -1);
                if (id != -1) {
                    String name = resultingIntent.getStringExtra(EXTRA_FRIEND_NAME);
                    String info = resultingIntent.getStringExtra(EXTRA_FRIEND_NOTES);

                    assert name != null;
                    Friend friend = new Friend(id, name, info);
                    setFriendInfo(friend);
                    mViewModel.update(friend);
                }
            }
        }
    }

    // TODO

    @Override
    public void hideSelection() {

    }

    @Override
    public void unhideSelection() {

    }

    @Override
    public void enableActionMode(int pos) {

    }

    @Override
    public void toggleSelection(int pos) {

    }

    @Override
    public void finishActionMode() {

    }

    @Override
    public void nullifyActionMode() {

    }
}
