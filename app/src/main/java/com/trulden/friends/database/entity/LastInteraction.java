package com.trulden.friends.database.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;

import java.util.Calendar;

// TODO get LI objects by direct query from database
public class LastInteraction {

    @ColumnInfo(name = "interaction_type_name")
    private String interactionType;

    @NonNull
    @ColumnInfo(name = "date")
    private Calendar date;

    @ColumnInfo(name = "friend_name")
    private String friendName;

    public LastInteraction(@NonNull String interactionType, @NonNull Calendar date, String friendName){
        this.interactionType = interactionType;
        this.date = date;
        this.friendName = friendName;
    }

    // -----------------------------------------
    // Getters and setters
    // -----------------------------------------

    public String getInteractionTypeId() {
        return interactionType;
    }

    public void setInteractionTypeId(String interactionType) {
        this.interactionType = interactionType;
    }

    @NonNull
    public Calendar getDate() {
        return date;
    }

    public void setDate(@NonNull Calendar date) {
        this.date = date;
    }

    public String getFriendName() {
        return friendName;
    }

    public void setFriendName(String friendName) {
        this.friendName = friendName;
    }
}
