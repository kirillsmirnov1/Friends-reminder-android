package com.trulden.friends.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.trulden.friends.R;
import com.trulden.friends.adapter.base.BindableViewHolder;
import com.trulden.friends.adapter.base.CustomRVAdapter;
import com.trulden.friends.database.entity.InteractionType;

import java.util.HashSet;

/**
 * RecyclerView for InteractionType objects.
 * Used in {@link com.trulden.friends.activity.InteractionTypesActivity InteractionTypesActivity}
 */
public class InteractionTypeRecyclerViewAdapter extends CustomRVAdapter<InteractionTypeRecyclerViewAdapter.ViewHolder, InteractionType> {

    public InteractionTypeRecyclerViewAdapter(Context context, @NonNull HashSet<Integer> selectedPositions){
        super(context, selectedPositions);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(mContext)
                .inflate(R.layout.entry_interaction_type, parent, false));
    }

    public class ViewHolder
            extends RecyclerView.ViewHolder
            implements BindableViewHolder<InteractionType> {

        private TextView mInteractionName;
        private TextView mInteractionFrequency;
        private View mInteractionTypeEntryLayout;

        ViewHolder(@NonNull View itemView) {
            super(itemView);

            mInteractionName = itemView.findViewById(R.id.eit_name);
            mInteractionFrequency = itemView.findViewById(R.id.eit_frequency);

            mInteractionTypeEntryLayout = itemView.findViewById(R.id.eit_layout);
        }

        public void bindTo(final InteractionType interactionType, final int position) {

            mInteractionName.setText(interactionType.getInteractionTypeName());
            mInteractionFrequency.setText(
                    String.format(mContext.getString(R.string.per_days), interactionType.getFrequency()));

            mInteractionTypeEntryLayout.setActivated(mSelectedPositions.contains(position));

            mInteractionTypeEntryLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(mOnClickListener == null)
                        return;
                    mOnClickListener.onItemClick(view, interactionType, position);
                }
            });

            mInteractionTypeEntryLayout.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    if(mOnClickListener == null)
                        return false;

                    mOnClickListener.onItemLongClick(view, interactionType, position);
                    return true;
                }
            });
        }
    }
}
