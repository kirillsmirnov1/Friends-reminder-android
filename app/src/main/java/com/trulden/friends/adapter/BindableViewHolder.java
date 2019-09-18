package com.trulden.friends.adapter;

import com.trulden.friends.database.entity.AbstractEntity;
/**
 * interface with bindTo method
 * @param <T_E> Entity, passed to binded view
 * */
interface BindableViewHolder<T_E extends AbstractEntity> {
    void bindTo(final T_E entity, final int position);
}
