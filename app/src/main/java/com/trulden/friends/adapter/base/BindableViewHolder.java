package com.trulden.friends.adapter.base;

import com.trulden.friends.database.entity.Entity;
/**
 * interface with bindTo method
 * @param <T_E> Entity, passed to binded view
 * */
public interface BindableViewHolder<T_E extends Entity> {
    void bindTo(final T_E entity, final int position);
}
