package com.trulden.friends.activity.interfaces;

import java.util.List;

/**
 * Some classes show {@link androidx.appcompat.app.AlertDialog AlertDialog} in {@link EditAndDeleteSelection#deleteSelection() deleteSelection()} to confirm action.
 * Those classes need another «delete» method, one with an actual deletion.
 * @param <T> elements in selection
 */
public interface SelectionWithOnDeleteAlert<T> extends EditAndDeleteSelection {
    /**
     * Classes implementing this interface must delete selection in that call. Action mode should be finished in that call.
     */
    void actuallyDeleteSelection(List<T> selection);
}
