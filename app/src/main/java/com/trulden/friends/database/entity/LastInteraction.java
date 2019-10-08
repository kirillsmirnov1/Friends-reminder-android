package com.trulden.friends.database.entity;

import androidx.room.ForeignKey;
import androidx.room.Index;

import static androidx.room.ForeignKey.CASCADE;

/**
 * Shows how much time passed since interaction of some type with some friend.
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
                            onDelete = CASCADE) // TODO What else can we do on delete?
        }
)
public class LastInteraction implements Entity {

    private int friendId;

    private int typeId;

    private int interactionId;

    private long date;

    /**
     * Will be used to set visibility and other params of that LI
     */
    private int status;

    /**
     * Constructor for LI entry. Must be used only by database classes
     * @param typeId type of Interaction
     * @param date Unix epoch time of interaction
     */
    public LastInteraction(int friendId, int typeId, int interactionId, long date, int status){
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

    public int getTypeId() {
        return typeId;
    }

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

    public void setTypeId(int typeId) {
        this.typeId = typeId;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
