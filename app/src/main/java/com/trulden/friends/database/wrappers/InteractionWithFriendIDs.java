package com.trulden.friends.database.wrappers;

import androidx.room.Embedded;
import androidx.room.Relation;

import com.trulden.friends.database.entity.Entity;
import com.trulden.friends.database.entity.Friend;
import com.trulden.friends.database.entity.Interaction;

import java.util.List;

/**
 * Stores an Interaction and IDs of friends, related to that interaction
 */
public class InteractionWithFriendIDs implements Entity {
    @Embedded
    public Interaction interaction;

    @Relation(
        parentColumn = "id", entityColumn = "id",
        entity = Friend.class, projection = {"id"})
    public List<Integer> friendIDs;
}
