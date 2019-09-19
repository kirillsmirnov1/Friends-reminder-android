package com.trulden.friends.database.entity;

import androidx.annotation.NonNull;
import androidx.room.Relation;

import java.util.List;

public class LastInteraction extends AbstractEntity {

    private String type; // TODO store id

    private long date;

    private String friend;

    @Relation(parentColumn = "type", entityColumn = "interactionTypeName", entity = InteractionType.class)
    private List<InteractionType> interactionTypes;

    public LastInteraction(@NonNull String type, long date, String friend){
        this.type = type;
        this.date = date;
        this.friend = friend;
    }

    // -----------------------------------------
    // Getters and setters
    // -----------------------------------------

    public String getType() {
        return type;
    }

    public void setType(String interactionType) {
        this.type = interactionType;
    }

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
}
