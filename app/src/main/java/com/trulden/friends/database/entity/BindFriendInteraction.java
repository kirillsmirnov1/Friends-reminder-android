package com.trulden.friends.database.entity;

import androidx.room.Entity;
import androidx.room.ForeignKey;

import static androidx.room.ForeignKey.CASCADE;

@Entity(
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
        }
)
public class BindFriendInteraction {

    private long friendId;
    private long interactionId;

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

    public void setFriendId(long friendId) {
        this.friendId = friendId;
    }

    public long getInteractionId() {
        return interactionId;
    }

    public void setInteractionId(long interactionId) {
        this.interactionId = interactionId;
    }
}
