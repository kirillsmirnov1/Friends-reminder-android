package com.trulden.friends.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.trulden.friends.R;
import com.trulden.friends.database.entity.Interaction;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class LogAdapter extends RecyclerView.Adapter<LogAdapter.ViewHolder> {

    private Context mContext;
    private List<Interaction> mLogData = new ArrayList<>();

    public LogAdapter(Context context){
        mContext = context;
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

    public void setInteractions(List<Interaction> interactions) {
        mLogData = interactions;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView mTypeAndNames;
        private TextView mDate;
        private TextView mComment;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            mTypeAndNames = itemView.findViewById(R.id.log_entry_type_and_names);
            mDate = itemView.findViewById(R.id.log_entry_date);
            mComment = itemView.findViewById(R.id.log_entry_comment);
        }

        public void bindTo(Interaction interaction) {
            // FIXME get NAME of activity, not id
            mTypeAndNames.setText(interaction.getInteractionTypeId() + " with " + interaction.getFriendNames());
            mDate.setText(new SimpleDateFormat("dd MMM yyyy").format(interaction.getDate().getTimeInMillis()));
            mComment.setText(interaction.getComment());
            // TODO hide comment if empty
        }
    }
}
