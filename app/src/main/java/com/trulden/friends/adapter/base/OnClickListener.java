package com.trulden.friends.adapter.base;

import android.view.View;

/**
 * Used for creating anonymous listeners in RecyclerView adapters
 * @param <T> class of object used in RV
 */
public interface OnClickListener<T> {
    void onItemClick(View view, T obj, int pos);
    void onItemLongClick(View view, T obj, int pos);
}
