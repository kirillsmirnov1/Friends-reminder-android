package com.trulden.friends.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
                position,
                selectedPositions.contains(position)
            );
    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        }

        public void bindTo(InteractionType interactionType, int position, boolean contains) {

        }
    }

    public interface OnClickListener {
        void onItemClick(View view, InteractionType obj, int pos);
        void onItemLongClick(View view, InteractionType obj, int pos);
    }
}
