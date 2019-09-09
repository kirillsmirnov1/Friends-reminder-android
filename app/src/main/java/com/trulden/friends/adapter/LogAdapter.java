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

public class LogAdapter extends RecyclerView.Adapter<LogAdapter.ViewHolder> {

    private Context mContext;
    private ArrayList<String> mLogData = new ArrayList<>();

    public LogAdapter(Context context){
        mContext = context;

        mLogData.addAll(Arrays.asList(context.getResources().getStringArray(R.array.log_list)));
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.log_entry, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bindTo(mLogData.get(position));
    }

    @Override
    public int getItemCount() {
        return mLogData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView mTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            mTextView = itemView.findViewById(R.id.log_entry_comment);
        }

        public void bindTo(String s) {
            mTextView.setText(s);
        }
    }
}
