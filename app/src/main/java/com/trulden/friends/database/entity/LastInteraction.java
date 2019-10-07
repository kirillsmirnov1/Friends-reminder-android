package com.trulden.friends.database.entity;

import androidx.room.ForeignKey;
import androidx.room.Relation;

import java.util.List;

import static androidx.room.ForeignKey.CASCADE;
import static com.trulden.friends.util.Util.daysPassed;

/**
 * Shows how much time passed since interaction of some type with some friend.
 */
@androidx.room.Entity(
        tableName = "last_interaction_table",

        primaryKeys = {"friendId", "interactionId"},

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
        })
public class LastInteraction implements Entity {

    private int friendId;

    private int typeId;

    private int interactionId;

    private long date;

    /**
     * Will be used to set visibility and other params of that LI
     */
    private int status;

    @Relation(parentColumn = "typeId", entityColumn = "id", entity = InteractionType.class)
    private List<InteractionType> interactionTypes;

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

    /**
     * Check if it's time to interact again
     * @return true if enough days have passed
     */
    public boolean itsTime(){
        return (daysPassed(this) >= getInteractionType().getFrequency());
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

    public InteractionType getInteractionType() {
        return interactionTypes.get(0);
    }

    public void setInteractionTypes(List<InteractionType> interactionTypes) {
        this.interactionTypes = interactionTypes;
    }

    public List<InteractionType> getInteractionTypes() {
        return interactionTypes;
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
