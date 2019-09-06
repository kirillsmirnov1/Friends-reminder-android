package com.trulden.friends.database.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.util.Calendar;

import static androidx.room.ForeignKey.CASCADE;

@Entity(tableName = "last_interaction_table",

        foreignKeys = {
        @ForeignKey(entity = InteractionType.class,
                    parentColumns = "id",
                    childColumns = "interactionTypeId",
                    onDelete = CASCADE),

        @ForeignKey(entity = Friend.class,
                    parentColumns = "id",
                    childColumns = "friendId",
                    onDelete = CASCADE)
        })

public class LastInteraction {

    @PrimaryKey(autoGenerate = true)
    private int id;
    private int interactionTypeId;
    @NonNull
    private Calendar date;
    private int friendId;

    public LastInteraction(int interactionTypeId, @NonNull Calendar date, int friendId){
        this.interactionTypeId = interactionTypeId;
        this.date = date;
        this.friendId = friendId;
    }

    @Ignore
    public LastInteraction(int id, int interactionTypeId, @NonNull Calendar date, int friendId){
        this.id = id;
        this.interactionTypeId = interactionTypeId;
        this.date = date;
        this.friendId = friendId;
    }

    // -----------------------------------------
    // Getters and setters
    // -----------------------------------------

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getInteractionTypeId() {
        return interactionTypeId;
    }

    public void setInteractionTypeId(int interactionTypeId) {
        this.interactionTypeId = interactionTypeId;
    }

    @NonNull
    public Calendar getDate() {
        return date;
    }

    public void setDate(@NonNull Calendar date) {
        this.date = date;
    }

    public int getFriendId() {
        return friendId;
    }

    public void setFriendId(int friendId) {
        this.friendId = friendId;
    }
}
