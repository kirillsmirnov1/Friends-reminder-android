package com.trulden.friends.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.collection.LongSparseArray;
import androidx.recyclerview.widget.RecyclerView;

import com.trulden.friends.R;
import com.trulden.friends.activity.InteractionsFragment;
import com.trulden.friends.adapter.base.BindableViewHolder;
import com.trulden.friends.adapter.base.CustomRVAdapter;
import com.trulden.friends.database.entity.Interaction;
import com.trulden.friends.database.wrappers.InteractionWithFriendIDs;

import java.util.HashSet;
import java.util.List;

import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
import static com.trulden.friends.util.Util.formatDate;

/**
 * RecyclerView adapter for Interaction objects.
 * Used in {@link InteractionsFragment InteractionsFragment}
 */
public class InteractionsRecyclerViewAdapter extends CustomRVAdapter<InteractionsRecyclerViewAdapter.ViewHolder, InteractionWithFriendIDs> {

    private LongSparseArray<String> mInteractionTypes = new LongSparseArray<>();
    private LongSparseArray<String> mFriendNames = new LongSparseArray<>();

    public InteractionsRecyclerViewAdapter(Context context, @NonNull HashSet<Integer> selectedInteractionsPositions){
        super(context, selectedInteractionsPositions);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.entry_interaction, parent, false));
    }

    public void setInteractionTypes(LongSparseArray<String> interactionTypes){
        mInteractionTypes = interactionTypes;
    }

    public void setFriends(LongSparseArray<String> friendNames) {
        mFriendNames = friendNames;
    }

    public class ViewHolder
            extends RecyclerView.ViewHolder
            implements BindableViewHolder<InteractionWithFriendIDs> {

        private TextView mType;
        private TextView mDate;
        private TextView mNames;
        private TextView mComment;

        private LinearLayout mTypeAndDate;

        private View mInteractionEntryLayout;

        ViewHolder(@NonNull View itemView) {
            super(itemView);

            mType = itemView.findViewById(R.id.ei_type);
            mDate = itemView.findViewById(R.id.ei_date);
            mNames = itemView.findViewById(R.id.ei_names);
            mComment = itemView.findViewById(R.id.ei_comment);

            mTypeAndDate = itemView.findViewById(R.id.ei_type_and_date_linear_layout);

            mInteractionEntryLayout = itemView.findViewById(R.id.ei_layout);
        }

        public void bindTo(final InteractionWithFriendIDs interactionWithFriendIDs, final int position) {

            // Set data

            final Interaction interaction = interactionWithFriendIDs.interaction;

            String interactionTypeName = mInteractionTypes.get(interaction.getInteractionTypeId());

            mType.setText(interactionTypeName);
            mDate.setText(formatDate(interaction.getDate()));
            mNames.setText(generateNameString(interactionWithFriendIDs.friendIDs));
            mComment.setText(interaction.getComment());

            // If there is no comment, hide it's TextView
            int p = mTypeAndDate.getPaddingStart();
            if(interaction.getComment() == null || interaction.getComment().isEmpty()){
                mComment.getLayoutParams().height = 0;

                mTypeAndDate.setPadding(p, 0, p, p);
            } else {
                mComment.getLayoutParams().height = WRAP_CONTENT;
                mComment.requestLayout();

                mTypeAndDate.setPadding(p, 0, p, 0);
            }

            // Set click listeners

            mInteractionEntryLayout.setActivated(mSelectedPositions.contains(position));

            mInteractionEntryLayout.setOnClickListener(view -> {
                if(mOnClickListener == null) {
                    return;
                }
                mOnClickListener.onItemClick(view, interactionWithFriendIDs, position);
            });

            mInteractionEntryLayout.setOnLongClickListener(view -> {
                if (mOnClickListener == null) {
                    return false;
                }

                mOnClickListener.onItemLongClick(view, interactionWithFriendIDs, position);
                return true;
            });

        }
    }

    /**
     * Generate string of friends names divided by comma
     * @param friendIDs ids of friends in question
     * @return names divided by comma
     */
    public String generateNameString(List<Long> friendIDs) {
        String[] names = new String[friendIDs.size()];

        for(int i = 0; i < names.length; ++i){
            names[i] = mFriendNames.get(friendIDs.get(i));
        }

        return TextUtils.join(", ", names);
    }
}
