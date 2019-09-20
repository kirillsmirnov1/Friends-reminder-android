package com.trulden.friends.database.entity;

import androidx.room.Relation;

import java.util.List;

import static com.trulden.friends.util.Util.daysPassed;

/**
 * Shows how much time passed since interaction of some type with some friend.
 * Not stored in database.
 */
public class LastInteraction implements Entity {

    // TODO store in database

    private int typeId;

    private long date;

    private String friend;

    @Relation(parentColumn = "typeId", entityColumn = "id", entity = InteractionType.class)
    private List<InteractionType> interactionTypes;

    /**
     * Constructor for LI entry. Must be used only by DAO
     * @param typeId type of Interaction
     * @param date Unix epoch time of interaction
     * @param friend name of friend
     */
    public LastInteraction(int typeId, long date, String friend){
        this.typeId = typeId;
        this.date = date;
        this.friend = friend;
    }

    /**
     * Check if it's time to interact again
     * @return true if enough days have passed
     */
    public boolean itsTime(){
        return (daysPassed(this) > getInteractionType().getFrequency());
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

    public String getFriend() {
        return friend;
    }

    public void setFriend(String friend) {
        this.friend = friend;
    }

    public InteractionType getInteractionType() {
        return interactionTypes.get(0);
    }

    public void setInteractionTypes(List<InteractionType> interactionTypes) {
        this.interactionTypes = interactionTypes;
    }

    // FIXME those methods are required by room, but I couldn't find a way to suppress warnings for them

    public List<InteractionType> getInteractionTypes() {
        return interactionTypes;
    }

    public int getTypeId() {
        return typeId;
    }
}
