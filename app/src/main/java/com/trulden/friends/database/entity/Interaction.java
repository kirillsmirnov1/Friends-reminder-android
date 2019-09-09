package com.trulden.friends.database.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.util.Calendar;

import static androidx.room.ForeignKey.CASCADE;

@Entity(tableName = "interaction_table",

        foreignKeys = {
        @ForeignKey(entity = InteractionType.class,
                    parentColumns = "id",
                    childColumns = "interactionTypeId",
                    onDelete = CASCADE)
        })

public class Interaction {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private int interactionTypeId;

    @NonNull
    private Calendar date;

    private String comment;

    private String friendNames;

    public Interaction(int interactionTypeId, @NonNull Calendar date, String comment, String friendNames) {
        this.interactionTypeId = interactionTypeId;
        this.date = date;
        this.comment = comment;
        this.friendNames = friendNames;
    }

    @Ignore
    public Interaction(int id, int interactionTypeId, @NonNull Calendar date, String comment, String friendNames) {
        this.id = id;
        this.interactionTypeId = interactionTypeId;
        this.date = date;
        this.comment = comment;
        this.friendNames = friendNames;
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

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getFriendNames() {
        return friendNames;
    }

    public void setFriendNames(String friendNames) {
        this.friendNames = friendNames;
    }
}
