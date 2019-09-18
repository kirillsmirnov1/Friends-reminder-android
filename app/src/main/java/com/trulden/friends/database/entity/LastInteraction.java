package com.trulden.friends.database.entity;

import androidx.annotation.NonNull;

public class LastInteraction extends AbstractEntity {

    private String type;

    private long date;

    private String friend;

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
}
