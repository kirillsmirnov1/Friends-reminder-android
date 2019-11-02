package com.trulden.friends.database.entity;

import androidx.room.ForeignKey;
import androidx.room.Index;

import static androidx.room.ForeignKey.CASCADE;

/**
 * Shows how much time passed since interaction of some type with some friend.
 * Objects of this class are called «Last Interaction trackers» or just «trackers» for brevity.
 */
@androidx.room.Entity(
        tableName = "last_interaction_table",

        primaryKeys = {"friendId", "typeId"},

        foreignKeys = {
                @ForeignKey(entity = Friend.class,
                            parentColumns = "id",
                            childColumns = "friendId",
                            onDelete = CASCADE),

                @ForeignKey(entity = InteractionType.class,
                            parentColumns = "id",
                            childColumns = "typeId",
                            onDelete = CASCADE),

                @ForeignKey(entity = Interaction.class,
                            parentColumns = "id",
                            childColumns = "interactionId",
                            onDelete = CASCADE)
        },

        indices = {
                @Index(value = "typeId"),
                @Index(value = "interactionId")
        }
)
public class LastInteraction implements Entity {

    private long friendId;

    private long typeId;

    private long interactionId;

    private long date;

    /**
     * Will be used to set visibility and other params of that LI
     */
    private long status;

    /**
     * Constructor for LI entry. Must be used only by database classes
     * @param typeId type of Interaction
     * @param date Unix epoch time of interaction
     */
    public LastInteraction(long friendId, long typeId, long interactionId, long date, long status){
        this.friendId = friendId;
        this.typeId = typeId;
        this.interactionId = interactionId;
        this.date = date;
        this.status = status;
    }

    // -----------------------------------------
    // Getters and setters
    // -----------------------------------------

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public long getTypeId() {
        return typeId;
    }

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

    public void setTypeId(long typeId) {
        this.typeId = typeId;
    }

    public long getStatus() {
        return status;
    }

    public void setStatus(long status) {
        this.status = status;
    }
}
