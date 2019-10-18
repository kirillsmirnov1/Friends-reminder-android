package com.trulden.friends.activity.interfaces;

/**
 * Handles finishing and nullifying of action mode
 */
public interface SelectionHandler {
    /**
     * Should call mActionMode.finish()
     */
    void finishActionMode();

    /**
     * Set actionMode = null
     */
    void nullifyActionMode();
}
