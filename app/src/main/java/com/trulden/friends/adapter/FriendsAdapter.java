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
    private static List<Friend> mFriendsData = new ArrayList<>();
    private OnClickListener onClickListener = null;

    private HashSet<Integer> selectedFriendsPositions;

    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public FriendsAdapter(Context context, @NonNull HashSet<Integer> selectedFriendsPositions){
        mContext = context;
        this.selectedFriendsPositions = selectedFriendsPositions;
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
                mFriendsData.get(position),
                position);
    }

    @Override
    public int getItemCount() {
        return mFriendsData.size();
    }

    public void setFriends(List<Friend> friends){
        mFriendsData = friends;
    }

    public static boolean friendExists(String name){
        for(Friend friend : mFriendsData){
            if(friend.getName().equals(name))
                return true;
        }
        return false;
    }

    public void clearSelections() {
        selectedFriendsPositions.clear();
        notifyDataSetChanged();
    }

    public int getSelectedItemCount() {
        return selectedFriendsPositions.size();
    }

    public void toggleSelection(int pos) {
        if(selectedFriendsPositions.contains(pos)){
            selectedFriendsPositions.remove(pos);
        } else {
            selectedFriendsPositions.add(pos);
        }
        notifyItemChanged(pos);
    }

    public List<Friend> getSelectedFriends() {
        List <Friend> selectedFriends = new ArrayList<>(selectedFriendsPositions.size());
        for(Integer position : selectedFriendsPositions){
            selectedFriends.add(mFriendsData.get(position));
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
            mFriendEntryLayout.setActivated(selectedFriendsPositions.contains(position));

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

    public interface OnClickListener {
        void onItemClick(View view, Friend obj, int pos);
        void onItemLongClick(View view, Friend obj, int pos);
    }
}
