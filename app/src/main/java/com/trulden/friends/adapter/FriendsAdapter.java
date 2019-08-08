package com.trulden.friends.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.trulden.friends.R;

import java.util.ArrayList;
import java.util.Arrays;

public class FriendsAdapter extends RecyclerView.Adapter<FriendsAdapter.ViewHolder> {

    private Context mContext;
    private ArrayList<String> mFriendsData = new ArrayList<>();

    public FriendsAdapter(Context context){
        mContext = context;

        mFriendsData.addAll(Arrays.asList(context.getResources().getStringArray(R.array.persons_list)));
    }

    @NonNull
    @Override
    public FriendsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.friend_entry, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull FriendsAdapter.ViewHolder holder, int position) {
        holder.bindTo(mFriendsData.get(position));
    }

    @Override
    public int getItemCount() {
        return mFriendsData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView mTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            mTextView = itemView.findViewById(R.id.friend_entry_text);
        }

        public void bindTo(String s) {
            mTextView.setText(s);
        }
    }
}
