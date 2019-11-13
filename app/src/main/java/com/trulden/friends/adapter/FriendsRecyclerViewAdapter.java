package com.trulden.friends.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.trulden.friends.R;
import com.trulden.friends.adapter.base.BindableViewHolder;
import com.trulden.friends.adapter.base.CustomRVAdapter;
import com.trulden.friends.database.entity.Friend;

import java.util.HashSet;

/**
 * RecyclerView Adapter for Friend objects.
 * Used in {@link com.trulden.friends.activity.FriendsFragment FriendsFragment}.
 *
 */
public class FriendsRecyclerViewAdapter extends CustomRVAdapter<FriendsRecyclerViewAdapter.ViewHolder, Friend> {

    public FriendsRecyclerViewAdapter(Context context, @NonNull HashSet<Integer> selectedPositions){
        super(context, selectedPositions);
    }

    @NonNull
    @Override
    public FriendsRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(mContext)
                .inflate(R.layout.entry_friend, parent, false));
    }

    public class ViewHolder
            extends RecyclerView.ViewHolder
            implements BindableViewHolder<Friend> {

        private TextView mTextView;
        private View mFriendEntryLayout;

        ViewHolder(@NonNull View itemView) {
            super(itemView);

            mTextView = itemView.findViewById(R.id.ef_name);
            mFriendEntryLayout = itemView.findViewById(R.id.ef_layout);
        }

        public void bindTo(final Friend friend, final int position) {

            mTextView.setText(friend.getName());
            mFriendEntryLayout.setActivated(mSelectedPositions.contains(position));

            mFriendEntryLayout.setOnClickListener(v -> {
                if(mOnClickListener == null) {
                    return;
                }
                mOnClickListener.onItemClick(v, friend, position);
            });

            mFriendEntryLayout.setOnLongClickListener(v -> {
                if (mOnClickListener == null) {
                    return false;
                }

                mOnClickListener.onItemLongClick(v, friend, position);
                return true;
            });
        }
    }
}
