package com.trulden.friends.activity;

import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ActionMode;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.trulden.friends.R;
import com.trulden.friends.activity.interfaces.LastInteractionsSelection;
import com.trulden.friends.activity.interfaces.TrackerOverActivity;
import com.trulden.friends.adapter.LastInteractionsRecyclerViewAdapter;
import com.trulden.friends.adapter.base.OnClickListener;
import com.trulden.friends.adapter.base.SelectionCallback;
import com.trulden.friends.database.FriendsViewModel;
import com.trulden.friends.database.entity.Friend;
import com.trulden.friends.database.entity.LastInteraction;
import com.trulden.friends.database.wrappers.LastInteractionWrapper;

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
    implements
        LastInteractionsSelection,
        TrackerOverActivity {

    private TextView mPersonNotes;
    private View mNotesTrackersDivider;
    private TextView mLISubhead;
    private FrameLayout mTrackerOverLayout;

    private Friend mFriend;

    private FriendsViewModel mViewModel;
    private LastInteractionsRecyclerViewAdapter mRecyclerViewAdapter;
    private HashSet<Integer> mSelectedPositions;
    private SelectionCallback mSelectionCallback;
    private ActionMode mActionMode;

    private boolean mTrackerOverShown;
    private TrackerFragment mTrackerOverFragment;
    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_page);

        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        mViewModel = ViewModelProviders.of(this).get(FriendsViewModel.class);

        mPersonNotes = findViewById(R.id.afp_notes);
        mNotesTrackersDivider = findViewById(R.id.afp_notes_tracker_divider);
        mLISubhead = findViewById(R.id.afp_LI_subhead);
        mTrackerOverLayout = findViewById(R.id.afp_tracker_over_layout);

        Intent intent = getIntent();

        mFriend = new Friend(
                intent.getLongExtra(EXTRA_FRIEND_ID, -1),
                Objects.requireNonNull(intent.getStringExtra(EXTRA_FRIEND_NAME)),
                intent.getStringExtra(EXTRA_FRIEND_NOTES));

        setFriendInfo(mFriend);

        mSelectedPositions = mViewModel.getSelectedPositions(FriendPageActivity.class.getName());

        RecyclerView recyclerView = findViewById(R.id.afp_LI_recycler_view);
        mRecyclerViewAdapter = new LastInteractionsRecyclerViewAdapter(this, mSelectedPositions, SHOW_TYPE_NAME);
        recyclerView.setAdapter(mRecyclerViewAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        mViewModel.getLiveLastInteractionWrappersOfAFriend(mFriend.getId()).observe(this, lastInteractionWrappers -> {

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

        mRecyclerViewAdapter.setOnClickListener(new OnClickListener<LastInteractionWrapper>() {
            @Override
            public void onItemClick(View view, LastInteractionWrapper obj, int pos) {
                if(mRecyclerViewAdapter.getSelectedItemCount() > 0){
                    toggleSelection(pos);
                } else {
                   showTrackerOverActivity(obj);
                }
            }

            @Override
            public void onItemLongClick(View view, LastInteractionWrapper obj, int pos) {
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

        checkIfTrackerFragmentNeedsToBeShown();
    }

    private void setFriendInfo(Friend friend){
        mFriend = friend;
        mToolbar.setTitle(friend.getName());
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

        getMenuInflater().inflate(R.menu.menu_selection, menu);

        menu.findItem(R.id.ms_edit).setVisible(true);
        menu.findItem(R.id.ms_delete).setVisible(true);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()){
            case R.id.ms_edit: {
                Intent intent = new Intent(this, EditFriendActivity.class);

                intent.putExtra(EXTRA_FRIEND_ID, mFriend.getId());
                intent.putExtra(EXTRA_FRIEND_NAME, mFriend.getName());
                intent.putExtra(EXTRA_FRIEND_NOTES, mFriend.getInfo());

                startActivityForResult(intent, UPDATE_FRIEND_REQUEST);
                break;
            }

            case R.id.ms_delete: {
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

    @Override
    protected void onDestroy() {
        mViewModel.setSelectedPositions(FriendPageActivity.class.getName(), mSelectedPositions);
        super.onDestroy();
    }

    @Override
    public void hideSelection() {
        for(LastInteractionWrapper interactionWrapper : mRecyclerViewAdapter.getSelectedItems()){

            LastInteraction interaction = interactionWrapper.getLastInteraction();

            interaction.setStatus(LastInteractionWrapper.LastInteractionStatus.HIDDEN.ordinal());

            mViewModel.update(interaction);
        }

    }

    @Override
    public void unhideSelection() {
        for(LastInteractionWrapper interactionWrapper : mRecyclerViewAdapter.getSelectedItems()){
            LastInteraction interaction = interactionWrapper.getLastInteraction();

            interaction.setStatus(LastInteractionWrapper.LastInteractionStatus.DEFAULT.ordinal());

            mViewModel.update(interaction);
        }
    }

    @Override
    public void enableActionMode(int pos) {
        if(mActionMode == null){
            mActionMode = startSupportActionMode(mSelectionCallback);
        }

        toggleSelection(pos);
    }

    @Override
    public void toggleSelection(int pos) {
        if(pos != -1){
            mRecyclerViewAdapter.toggleSelection(pos);
        }

        int count = mRecyclerViewAdapter.getSelectedItemCount();

        if(count == 0){
            finishActionMode();
        } else {
            mActionMode.setTitle(String.valueOf(count));
        }

    }

    @Override
    public void finishActionMode() {
        mViewModel.clearSelectedPositions();
        if(mActionMode != null){
            mActionMode.finish();
        }

    }

    @Override
    public void nullifyActionMode() {
        mViewModel.clearSelectedPositions();
        if(mActionMode != null){
            mActionMode = null;
        }

    }

    @Override
    public void showTrackerOverActivity(LastInteractionWrapper lastInteractionWrapper) {
        mTrackerOverShown = true;

        mTrackerOverFragment = TrackerFragment
            .newInstance(
                lastInteractionWrapper.getType().getId(),
                lastInteractionWrapper.getFriend().getId());

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.afp_tracker_over_layout, mTrackerOverFragment)
                .commit();

        setTrackerOverActivityVisibility(View.VISIBLE);
    }

    @Override
    public void closeTrackerOverActivity() {
        setTrackerOverActivityVisibility(View.GONE);
        mTrackerOverShown = false;
        getSupportFragmentManager()
                .beginTransaction()
                .remove(mTrackerOverFragment)
                .commit();

        mViewModel.setTrackerInFragment(null);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {

        if(mTrackerOverShown){
            Rect outRect = new Rect();
            mTrackerOverLayout.getGlobalVisibleRect(outRect);
            if(!outRect.contains((int)ev.getRawX(), (int)ev.getRawY())){
                closeTrackerOverActivity();
                return true;
            } else {
                return super.dispatchTouchEvent(ev);
            }
        }

        return super.dispatchTouchEvent(ev);
    }

    @Override
    public void onBackPressed() {
        if(mTrackerOverShown){

            closeTrackerOverActivity();

            return;
        }
        super.onBackPressed();
    }

    @Override
    public void checkIfTrackerFragmentNeedsToBeShown(){
        int fragmentsId = R.id.afp_tracker_over_layout;

        if(getSupportFragmentManager().findFragmentById(fragmentsId) != null){
            mTrackerOverShown = true;
            setTrackerOverActivityVisibility(View.VISIBLE);
            mTrackerOverFragment = (TrackerFragment) getSupportFragmentManager().findFragmentById(fragmentsId);
        }
    }

    @Override
    public void setTrackerOverActivityVisibility(int visibility){
        findViewById(R.id.afp_fade_background).setVisibility(visibility);
        mTrackerOverLayout.setVisibility(visibility);
    }

    @Override
    public void updateLastInteraction(LastInteraction lastInteraction) {
        mViewModel.update(lastInteraction);
    }
}
