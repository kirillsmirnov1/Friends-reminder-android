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

import static com.trulden.friends.util.Util.daysPassed;
import static com.trulden.friends.util.Util.itsTime;

// TODO implement selection

public class LastInteractionsAdapter extends CustomRVAdapter<LastInteractionsAdapter.ViewHolder, LastInteraction> {

    private InteractionType mType;

    public LastInteractionsAdapter(Context context, InteractionType type){
        super(context, null);
        mContext = context;
        mType = type;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.last_interaction_entry, parent, false));
    }

    class ViewHolder
            extends RecyclerView.ViewHolder
            implements BindableViewHolder<LastInteraction> {

        private TextView mName;
        private TextView mTime;

        private RelativeLayout layout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            mName = itemView.findViewById(R.id.last_interaction_name);
            mTime = itemView.findViewById(R.id.last_interaction_time);

            layout = itemView.findViewById(R.id.last_interaction_entry_layout);
        }

        public void bindTo(final LastInteraction lastInteraction, int pos) {

            mName.setText(lastInteraction.getFriend());

            String dateString = daysPassed(lastInteraction) + mContext.getString(R.string.days_ago);

            mTime.setText(dateString);

            if(!itsTime(lastInteraction, mType)) {
                layout.setBackground(ContextCompat.getDrawable(mContext, R.drawable.item_backgroung_grey));
            }
        }
    }
}
