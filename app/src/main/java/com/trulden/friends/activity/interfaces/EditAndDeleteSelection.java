package com.trulden.friends.activity.interfaces;

/**
 * Interface with edit/delete selection and finishActionMode methods
 */
public interface EditAndDeleteSelection extends BasicSelection {
    /**
     * Open editing activity for selected item.
     * Must work only when one item selected
     */
    void editSelection();

    /**
     * Delete selected items
     */
    void deleteSelection();
}
