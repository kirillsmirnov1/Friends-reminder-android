package com.trulden.friends.database.entity;

import androidx.room.ForeignKey;
import androidx.room.Index;

import static androidx.room.ForeignKey.CASCADE;
/**
 * Establishes a connection between Friend and Interaction.
 * Friend X was in Interaction Y.
 */
@androidx.room.Entity(
    tableName = "bind_friend_interaction_table",

    primaryKeys = {"friendId", "interactionId"},

    foreignKeys = {
        @ForeignKey(entity = Friend.class,
                    parentColumns = "id",
                    childColumns = "friendId",
                    onDelete = CASCADE),

        @ForeignKey(entity = Interaction.class,
                    parentColumns = "id",
                    childColumns = "interactionId",
                    onDelete = CASCADE)
        },

    indices =
    @Index(value = "interactionId")
)
public class BindFriendInteraction implements Entity {

    private long friendId;
    private long interactionId;

    /**
     * Must be used only by database classes
     * @param friendId id of friend in question
     * @param interactionId if of interaction in question
     */
    public BindFriendInteraction(long friendId, long interactionId) {
        this.friendId = friendId;
        this.interactionId = interactionId;
    }

    // -----------------------------------------
    // Getters and setters
    // -----------------------------------------

    public long getFriendId() {
        return friendId;
    }

    public long getInteractionId() {
        return interactionId;
    }
}
