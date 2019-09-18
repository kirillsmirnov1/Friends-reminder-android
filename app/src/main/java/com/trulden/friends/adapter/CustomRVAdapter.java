package com.trulden.friends.adapter;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * CustomRVAdapter holds fields and methods of all RecyclerViews used in app
 * @param <T_VH> class for ViewHolder
 * @param <T_E>  POJO entries of data
 * */
abstract class CustomRVAdapter<T_VH extends RecyclerView.ViewHolder, T_E> extends RecyclerView.Adapter<T_VH>{

    Context mContext;
    OnClickListener mOnClickListener = null;
    HashSet<Integer> mSelectedPositions;

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
        List <T_E> selectedItems = new ArrayList<>(mSelectedPositions.size());
        for(Integer position : mSelectedPositions){
            selectedItems.add(mEntries.get(position));
        }
        return selectedItems;
    }

    public List<T_E> getEntries() {
        return mEntries;
    }

}
