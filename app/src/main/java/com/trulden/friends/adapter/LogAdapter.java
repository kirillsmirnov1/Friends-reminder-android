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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.trulden.friends.util.Util.*;

public class LogAdapter extends RecyclerView.Adapter<LogAdapter.ViewHolder> {

    private Context mContext;
    private List<Interaction> mInteractions = new ArrayList<>();
    private HashMap<Long, String> mInteractionTypes = new HashMap<>();

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
        holder.bindTo(mInteractions.get(position));
    }

    @Override
    public int getItemCount() {
        return mInteractions.size();
    }

    public void setInteractions(List<Interaction> interactions) {
        mInteractions = interactions;
    }

    public void setInteractionTypes(HashMap<Long, String> interactionTypes){
        mInteractionTypes = interactionTypes;
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
            String interactionTypeName = mInteractionTypes.get(interaction.getInteractionTypeId());

            mTypeAndNames.setText(interactionTypeName + " with " + interaction.getFriendNames());
            mDate.setText(dateFormat.format(interaction.getDate().getTimeInMillis()));

            if(interaction.getComment() == null || interaction.getComment().isEmpty()){
                mComment.setPadding(0, 0, 0, 0);
                mComment.setHeight(0);

                int p = mDate.getPaddingStart();
                mDate.setPadding(p, 0, p, p);
            }

            mComment.setText(interaction.getComment());
        }
    }
}
