package com.trulden.friends.activity.interfaces;

/**
 * Handles selection of {@link com.trulden.friends.database.wrappers.LastInteractionWrapper LastInteractionWrapper} elements in {@link com.trulden.friends.activity.LastInteractionsFragment LastInteractionsFragment}
 */
public interface LastInteractionsSelection extends BasicSelection {
    /**
     * Hide selected elements by setting their status to
     * {@link com.trulden.friends.database.wrappers.LastInteractionWrapper.LastInteractionStatus#HIDDEN HIDDEN}
     */
    void hideSelection();

    /**
     * Unhide selected elements by setting their status to
     * {@link com.trulden.friends.database.wrappers.LastInteractionWrapper.LastInteractionStatus#DEFAULT DEFAULT}
     */
    void unhideSelection();
}
