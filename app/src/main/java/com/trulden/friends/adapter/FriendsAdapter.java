package com.trulden.friends.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.trulden.friends.R;
import com.trulden.friends.database.entity.Friend;

import java.util.HashSet;

public class FriendsAdapter extends CustomRVAdapter<FriendsAdapter.ViewHolder, Friend> {

    public FriendsAdapter(Context context, @NonNull HashSet<Integer> selectedPositions){
        super(context, selectedPositions);
    }

    @NonNull
    @Override
    public FriendsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(mContext)
                .inflate(R.layout.friend_entry, parent, false));
    }

    public class ViewHolder
            extends RecyclerView.ViewHolder
            implements BindableViewHolder<Friend> {

        private TextView mTextView;
        private View mFriendEntryLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            mTextView = itemView.findViewById(R.id.friend_entry_text);
            mFriendEntryLayout = itemView.findViewById(R.id.friend_entry_layout);
        }

        public void bindTo(final Friend friend, final int position) {

            mTextView.setText(friend.getName());
            mFriendEntryLayout.setActivated(mSelectedPositions.contains(position));

            mFriendEntryLayout.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){
                    if(mOnClickListener == null)
                        return;
                    mOnClickListener.onItemClick(v, friend, position);
                }
            });

            mFriendEntryLayout.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (mOnClickListener == null)
                        return false;

                    mOnClickListener.onItemLongClick(v, friend, position);
                    return true;
                }
            });
        }
    }
}
