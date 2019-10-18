package com.trulden.friends.activity.interfaces;

/**
 * Handles selection of element
 */
public interface BasicSelection extends SelectionHandler {
    /**
     * Enable action mode and toggle position
     */
    void enableActionMode(int pos);

    /**
     * Toggle selection for position
     */
    void toggleSelection(int pos);
}
