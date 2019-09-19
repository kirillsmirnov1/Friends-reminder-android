package com.trulden.friends.base;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.trulden.friends.adapter.OnClickListener;
import com.trulden.friends.base.BindableViewHolder;
import com.trulden.friends.database.entity.AbstractEntity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * CustomRVAdapter holds fields and methods of all RecyclerViews used in app
 * @param <T_VH> class for ViewHolder
 * @param <T_E>  POJO entries of data
 * */
public abstract class CustomRVAdapter
        <T_VH extends RecyclerView.ViewHolder & BindableViewHolder,
         T_E  extends AbstractEntity>
        extends RecyclerView.Adapter<T_VH>{

    protected Context mContext;
    protected OnClickListener mOnClickListener = null;
    protected HashSet<Integer> mSelectedPositions;

    List<T_E> mEntries = new ArrayList<>();

    public CustomRVAdapter(Context context, @NonNull HashSet<Integer> selectedPositions){
        mContext = context;
        mSelectedPositions = selectedPositions;
    }

    public void setOnClickListener(OnClickListener onClickListener) {
        mOnClickListener = onClickListener;
    }

    @Override
    public int getItemCount() {
        return mEntries.size();
    }

    @Override
    public void onBindViewHolder(@NonNull T_VH holder, int position) {
        holder.bindTo(mEntries.get(position), position);
    }

    public void setEntries(List<T_E> entries){
        mEntries = entries;
    }

    public void clearSelections() {
        mSelectedPositions.clear();
        notifyDataSetChanged();
    }

    public int getSelectedItemCount() {
        return mSelectedPositions.size();
    }

    public void toggleSelection(int pos) {
        if(mSelectedPositions.contains(pos)){
            mSelectedPositions.remove(pos);
        } else {
            mSelectedPositions.add(pos);
        }
        notifyItemChanged(pos);
    }

    public List<T_E> getSelectedItems() {
        List <T_E> selectedTypes = new ArrayList<>(mSelectedPositions.size());
        for(Integer position : mSelectedPositions){
            selectedTypes.add(mEntries.get(position));
        }
        return selectedTypes;
    }

    public List<T_E> getEntries() {
        return mEntries;
    }

}
