package com.trulden.friends.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.trulden.friends.R;
import com.trulden.friends.database.entity.InteractionType;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class InteractionTypeAdapter extends RecyclerView.Adapter<InteractionTypeAdapter.ViewHolder> {

    private Context mContext;
    private OnClickListener onClickListener = null;
    private HashSet<Integer> selectedPositions;

    private static List<InteractionType> mInteractionTypes = new ArrayList<>();

    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public InteractionTypeAdapter(Context context, @NonNull HashSet<Integer> selectedPositions){
        mContext = context;
        this.selectedPositions = selectedPositions;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(mContext)
                .inflate(R.layout.interaction_type_entry, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder
            .bindTo(
                mInteractionTypes.get(position),
                position);
    }

    @Override
    public int getItemCount() {
        return mInteractionTypes.size();
    }

    public void setInteractionTypes(List<InteractionType> interactionTypes){
        mInteractionTypes = interactionTypes;
    }

    public static boolean InteractionTypeExists(String name){
        for(InteractionType interactionType : mInteractionTypes){
            if(interactionType.getInteractionTypeName().equals(name))
                return true;
        }
        return false;
    }

    public void clearSelections() {
        selectedPositions.clear();
        notifyDataSetChanged();
    }

    public int getSelectedItemCount() {
        return selectedPositions.size();
    }

    public void toggleSelection(int pos) {
        if(selectedPositions.contains(pos)){
            selectedPositions.remove(pos);
        } else {
            selectedPositions.add(pos);
        }
        notifyItemChanged(pos);
    }

    public List<InteractionType> getSelectedFriends() {
        List <InteractionType> selectedFriends = new ArrayList<>(selectedPositions.size());
        for(Integer position : selectedPositions){
            selectedFriends.add(mInteractionTypes.get(position));
        }
        return selectedFriends;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        private TextView mInteractionName;
        private TextView mInteractionFrequency;
        private View mInteractionTypeEntryLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            mInteractionName = itemView.findViewById(R.id.interaction_type_entry_name);
            mInteractionFrequency = itemView.findViewById(R.id.interaction_type_entry_frequency);

            mInteractionTypeEntryLayout = itemView.findViewById(R.id.interaction_type_entry_layout);
        }

        public void bindTo(final InteractionType interactionType, final int position) {
            mInteractionName.setText(interactionType.getInteractionTypeName());
            mInteractionFrequency.setText("per " + interactionType.getFrequency() + " days");

            mInteractionTypeEntryLayout.setActivated(selectedPositions.contains(position));

            mInteractionTypeEntryLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(onClickListener == null)
                        return;
                    onClickListener.onItemClick(view, interactionType, position);
                }
            });

            mInteractionTypeEntryLayout.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    if(onClickListener == null)
                        return false;

                    onClickListener.onItemLongClick(view, interactionType, position);
                    return true;
                }
            });
        }
    }

    // FIXME  I have a feeling, that it should not be inner class
    public interface OnClickListener {
        void onItemClick(View view, InteractionType obj, int pos);
        void onItemLongClick(View view, InteractionType obj, int pos);
    }
}
