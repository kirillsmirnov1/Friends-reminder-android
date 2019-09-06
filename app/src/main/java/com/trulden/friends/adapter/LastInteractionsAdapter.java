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

public class LastInteractionsAdapter extends RecyclerView.Adapter<LastInteractionsAdapter.ViewHolder> {

    private Context mContext;
    private ArrayList<String> mLastInteractions; // TODO объекты Remind, а не строки

    public LastInteractionsAdapter(Context context, ArrayList<String> lastInteractions){
        mContext = context;
        mLastInteractions = lastInteractions;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.reminder_entry, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bindTo(mLastInteractions.get(position));
    }

    @Override
    public int getItemCount() {
        return mLastInteractions.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private TextView mTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            mTextView = itemView.findViewById(R.id.reminder_entry_text);
        }

        public void bindTo(String remind) {
            mTextView.setText(remind);
        }
    }
}
