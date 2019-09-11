package com.trulden.friends.activity.interfaces;

import com.trulden.friends.database.entity.InteractionType;

public interface EditInteractionType {
    boolean typeExists(String typeName);
    void saveType(InteractionType interactionType);
}
