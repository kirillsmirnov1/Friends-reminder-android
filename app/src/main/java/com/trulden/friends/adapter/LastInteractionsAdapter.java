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
import com.trulden.friends.database.wrappers.LastInteractionWrapper;

import static com.trulden.friends.util.Util.daysPassed;

/**
 * RecyclerView adapter for LastInteraction objects.
 * Used in {@link com.trulden.friends.activity.LastInteractionsTabFragment LastInteractionsTabFragment}
 */
public class LastInteractionsAdapter extends CustomRVAdapter<LastInteractionsAdapter.ViewHolder, LastInteractionWrapper> {

    public LastInteractionsAdapter(Context context){
        //noinspection ConstantConditions
        super(context, null);
        mContext = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.entry_last_interaction, parent, false));
    }

    class ViewHolder
            extends RecyclerView.ViewHolder
            implements BindableViewHolder<LastInteractionWrapper> {

        private TextView mName;
        private TextView mTime;

        private RelativeLayout layout;

        ViewHolder(@NonNull View itemView) {
            super(itemView);

            mName = itemView.findViewById(R.id.eli_friend_name);
            mTime = itemView.findViewById(R.id.eli_time_passed);

            layout = itemView.findViewById(R.id.eli_layout);
        }

        public void bindTo(final LastInteractionWrapper interaction, int pos) {

            mName.setText(interaction.getFriendName());

            int daysPassed = daysPassed(interaction.getLastInteraction());

            String dateString = daysPassed + mContext.getString(R.string.days_ago);

            mTime.setText(dateString);

            // Grey out LI for which time have not yet come
            if(!interaction.itsTime()) {
                layout.setBackground(ContextCompat.getDrawable(mContext, R.drawable.item_background_grey));
            }
        }
    }
}
