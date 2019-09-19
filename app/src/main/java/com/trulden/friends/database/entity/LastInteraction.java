package com.trulden.friends.database.entity;

import androidx.room.Relation;

import java.util.List;

public class LastInteraction extends AbstractEntity {

    private int typeId;

    private long date;

    private String friend;

    @Relation(parentColumn = "typeId", entityColumn = "id", entity = InteractionType.class)
    private List<InteractionType> interactionTypes;

    public LastInteraction(int typeId, long date, String friend){
        this.typeId = typeId;
        this.date = date;
        this.friend = friend;
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

    public List<InteractionType> getInteractionTypes() {
        return interactionTypes;
    }

    public int getTypeId() {
        return typeId;
    }

    public void setTypeId(int typeId) {
        this.typeId = typeId;
    }
}
