package com.trulden.friends.activity.interfaces;

/**
 * Interface with edit/delete selection and nullifyActionMode methods
 */
public interface ActivityWithSelection {
    /**
     * Open editing activity for selected item.
     * Must work only when one item selected
     */
    void editSelection();

    /**
     * Delete selected items
     */
    void deleteSelection();

    /**
     * Set actionMode = null
     */
    void nullifyActionMode();
}
