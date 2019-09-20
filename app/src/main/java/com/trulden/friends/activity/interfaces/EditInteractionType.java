package com.trulden.friends.activity.interfaces;

import com.trulden.friends.database.entity.InteractionType;

/**
 * Interface for classes which can create or edit InteractionType objects
 */
public interface EditInteractionType {
    /**
     * Checks if type already exists
     * @param typeName name of type to be checked
     * @return true, if type exists, false otherwise
     */
    boolean typeExists(String typeName);

    /**
     * Save type to database
     * @param interactionType type to be saved
     */
    void saveType(InteractionType interactionType);
}
