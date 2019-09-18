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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class FriendsAdapter extends RecyclerView.Adapter<FriendsAdapter.ViewHolder> {

    private Context mContext;
    private OnClickListener onClickListener = null;
    private HashSet<Integer> mSelectedPositions;

    private static List<Friend> mEntriesData = new ArrayList<>();

    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public FriendsAdapter(Context context, @NonNull HashSet<Integer> selectedPositions){
        mContext = context;
        mSelectedPositions = selectedPositions;
    }

    @NonNull
    @Override
    public FriendsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(mContext)
                .inflate(R.layout.friend_entry, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull FriendsAdapter.ViewHolder holder, final int position) {
        holder
            .bindTo(
                mEntriesData.get(position),
                position);
    }

    @Override
    public int getItemCount() {
        return mEntriesData.size();
    }

    public void setFriends(List<Friend> friends){
        mEntriesData = friends;
    }

    public static boolean friendExists(String name){
        for(Friend friend : mEntriesData){
            if(friend.getName().equals(name))
                return true;
        }
        return false;
    }

    public void clearSelections() {
        mSelectedPositions.clear();
        notifyDataSetChanged();
    }

    public int getSelectedItemCount() {
        return mSelectedPositions.size();
    }

    public void toggleSelection(int pos) {
        if(mSelectedPositions.contains(pos)){
            mSelectedPositions.remove(pos);
        } else {
            mSelectedPositions.add(pos);
        }
        notifyItemChanged(pos);
    }

    public List<Friend> getSelectedFriends() {
        List <Friend> selectedFriends = new ArrayList<>(mSelectedPositions.size());
        for(Integer position : mSelectedPositions){
            selectedFriends.add(mEntriesData.get(position));
        }
        return selectedFriends;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

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
                    if(onClickListener == null)
                        return;
                    onClickListener.onItemClick(v, friend, position);
                }
            });

            mFriendEntryLayout.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (onClickListener == null)
                        return false;

                    onClickListener.onItemLongClick(v, friend, position);
                    return true;
                }
            });
        }
    }
}
