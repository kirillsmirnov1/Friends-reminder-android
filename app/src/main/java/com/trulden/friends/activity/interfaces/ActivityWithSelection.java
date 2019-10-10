package com.trulden.friends.activity.interfaces;

/**
 * Interface with edit/delete selection and finishActionMode methods
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
     * Should call mActionMode.finish()
     */
    void finishActionMode();

    /**
     * Set actionMode = null
     */
    void nullifyActionMode();

    /**
     * Enable action mode and toggle position
     */
    void enableActionMode(int pos);

    /**
     * Toggle selection for position
     */
    void toggleSelection(int pos);
}
