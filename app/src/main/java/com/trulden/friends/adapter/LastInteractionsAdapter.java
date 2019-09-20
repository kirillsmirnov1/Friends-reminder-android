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
import com.trulden.friends.adapter.base.BindableViewHolder;
import com.trulden.friends.adapter.base.CustomRVAdapter;
import com.trulden.friends.database.entity.LastInteraction;

import static com.trulden.friends.util.Util.daysPassed;

public class LastInteractionsAdapter extends CustomRVAdapter<LastInteractionsAdapter.ViewHolder, LastInteraction> {

    public LastInteractionsAdapter(Context context){
        //noinspection ConstantConditions
        super(context, null);
        mContext = context;
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

        ViewHolder(@NonNull View itemView) {
            super(itemView);

            mName = itemView.findViewById(R.id.last_interaction_name);
            mTime = itemView.findViewById(R.id.last_interaction_time);

            layout = itemView.findViewById(R.id.last_interaction_entry_layout);
        }

        public void bindTo(final LastInteraction lastInteraction, int pos) {

            mName.setText(lastInteraction.getFriend());

            String dateString = daysPassed(lastInteraction) + mContext.getString(R.string.days_ago);

            mTime.setText(dateString);

            if(!lastInteraction.itsTime()) {
                layout.setBackground(ContextCompat.getDrawable(mContext, R.drawable.item_backgroung_grey));
            }
        }
    }
}
