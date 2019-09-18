package com.trulden.friends.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.trulden.friends.R;
import com.trulden.friends.database.entity.InteractionType;
import com.trulden.friends.database.entity.LastInteraction;

import java.util.ArrayList;
import java.util.Calendar;

import static com.trulden.friends.util.Util.MILLISECONDS_IN_DAYS;
import static com.trulden.friends.util.Util.daysPassed;
import static com.trulden.friends.util.Util.itsTime;

public class LastInteractionsAdapter extends RecyclerView.Adapter<LastInteractionsAdapter.ViewHolder> {

    private Context mContext;
    private ArrayList<LastInteraction> mLastInteractions;
    private InteractionType type;

    public LastInteractionsAdapter(Context context, InteractionType type, ArrayList<LastInteraction> lastInteractions){
        mContext = context;
        mLastInteractions = lastInteractions;
        this.type = type;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.last_interaction_entry, parent, false));
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

        private TextView mName;
        private TextView mTime;

        private RelativeLayout layout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            mName = itemView.findViewById(R.id.last_interaction_name);
            mTime = itemView.findViewById(R.id.last_interaction_time);

            layout = itemView.findViewById(R.id.last_interaction_entry_layout);
        }

        public void bindTo(LastInteraction lastInteraction) {
            mName.setText(lastInteraction.getFriend());

            String dateString = daysPassed(lastInteraction) + mContext.getString(R.string.days_ago);

            mTime.setText(dateString);

            if(!itsTime(lastInteraction, type)) {
                layout.setBackground(ContextCompat.getDrawable(mContext, R.drawable.item_backgroung_grey));
            }
        }
    }
}
