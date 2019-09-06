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

    private int friendId;
    private int interactionId;

    public BindFriendInteraction(int friendId, int interactionId) {
        this.friendId = friendId;
        this.interactionId = interactionId;
    }

    // -----------------------------------------
    // Getters and setters
    // -----------------------------------------

    public int getFriendId() {
        return friendId;
    }

    public void setFriendId(int friendId) {
        this.friendId = friendId;
    }

    public int getInteractionId() {
        return interactionId;
    }

    public void setInteractionId(int interactionId) {
        this.interactionId = interactionId;
    }
}
