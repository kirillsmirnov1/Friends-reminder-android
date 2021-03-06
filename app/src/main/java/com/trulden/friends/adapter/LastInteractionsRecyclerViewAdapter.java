package com.trulden.friends.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.trulden.friends.R;
import com.trulden.friends.adapter.base.BindableViewHolder;
import com.trulden.friends.adapter.base.CustomRVAdapter;
import com.trulden.friends.database.wrappers.LastInteractionWrapper;
import com.trulden.friends.view.FadebleRelativeLayout;

import java.util.HashSet;

import static com.trulden.friends.util.Util.daysPassed;

/**
 * RecyclerView adapter for LastInteraction objects.
 * Used in {@link com.trulden.friends.activity.LastInteractionsTabFragment LastInteractionsTabFragment}
 */
public class LastInteractionsRecyclerViewAdapter extends CustomRVAdapter<LastInteractionsRecyclerViewAdapter.ViewHolder, LastInteractionWrapper> {

    TrackerMode mTrackerMode;

    public LastInteractionsRecyclerViewAdapter(Context context,
                                               @NonNull HashSet<Integer> selectedPositions,
                                               TrackerMode trackerMode){
        super(context, selectedPositions);
        mContext = context;
        mTrackerMode = trackerMode;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.entry_last_interaction, parent, false));
    }

    class ViewHolder
            extends RecyclerView.ViewHolder
            implements BindableViewHolder<LastInteractionWrapper> {

        private TextView  mName;
        private TextView  mTime;
        private ImageView mHiddenIcon;
        private TextView  mFrequency;
        private FadebleRelativeLayout mLayout;

        ViewHolder(@NonNull View itemView) {
            super(itemView);

            mName = itemView.findViewById(R.id.eli_friend_name);
            mTime = itemView.findViewById(R.id.eli_time_passed);
            mHiddenIcon = itemView.findViewById(R.id.eli_hidden_icon);
            mFrequency = itemView.findViewById(R.id.eli_frequency);

            mLayout = itemView.findViewById(R.id.eli_layout);
        }

        public void bindTo(final LastInteractionWrapper interaction, final int pos) {

            if(interaction.getLastInteraction().getStatus() == 1){
                mHiddenIcon.setVisibility(View.VISIBLE);
            } else {
                mHiddenIcon.setVisibility(View.INVISIBLE);
            }

            switch (mTrackerMode) {
                case SHOW_FRIEND_NAME:
                    mName.setText(interaction.getFriendName());
                    break;
                case SHOW_TYPE_NAME:
                    mName.setText(interaction.getTypeName());
                    break;
            }

            int daysPassed = daysPassed(interaction.getLastInteraction());

            String dateString = String.format(mContext.getString(R.string.days_ago), daysPassed);

            mTime.setText(dateString);

            mFrequency.setText(String.format(mContext.getString(R.string.LI_every_x_days), interaction.getFrequency()));

            // Grey out LI for which time have not yet come
            mLayout.setFaded(!interaction.itsTime());

            mLayout.setActivated(mSelectedPositions.contains(pos));

            // Need to do that for state selector to work correctly
            // Otherwise, it behaves strangely
            mLayout.invalidate();

            mLayout.setOnClickListener(view -> {
                if(mOnClickListener == null) {
                    return;
                }

                mOnClickListener.onItemClick(view, interaction, pos);
            });

            mLayout.setOnLongClickListener(view -> {
                if(mOnClickListener == null) {
                    return false;
                }

                mOnClickListener.onItemLongClick(view, interaction, pos);
                return true;
            });
        }
    }

    public enum TrackerMode{
        SHOW_FRIEND_NAME,
        SHOW_TYPE_NAME
    }
}
