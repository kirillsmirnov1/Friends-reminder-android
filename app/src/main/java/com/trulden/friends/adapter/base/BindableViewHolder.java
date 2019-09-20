package com.trulden.friends.adapter.base;

import com.trulden.friends.database.entity.Entity;
/**
 * Interface with bindTo method
 * @param <T_E> Entity, passed to binded view
 * */
public interface BindableViewHolder<T_E extends Entity> {
    /**
     * Binds an entity to a position
     * @param entity object to be binded
     * @param position position to which object will be binded
     */
    void bindTo(final T_E entity, final int position);
}
