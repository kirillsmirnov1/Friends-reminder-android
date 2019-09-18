package com.trulden.friends.database.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "interaction_type_table")
public class InteractionType extends AbstractEntity {

    @PrimaryKey(autoGenerate = true)
    private long id;

    @NonNull
    private String interactionTypeName;

    private int frequency;

    public InteractionType(@NonNull String interactionTypeName, int frequency){
        this.interactionTypeName = interactionTypeName;
        this.frequency = frequency;
    }

    @Ignore
    public InteractionType(long id, @NonNull String interactionTypeName, int frequency){
        this.id = id;
        this.interactionTypeName = interactionTypeName;
        this.frequency = frequency;
    }

    // -----------------------------------------
    // Getters and setters
    // -----------------------------------------

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @NonNull
    public String getInteractionTypeName() {
        return interactionTypeName;
    }

    public void setInteractionTypeName(@NonNull String interactionTypeName) {
        this.interactionTypeName = interactionTypeName;
    }

    public int getFrequency() {
        return frequency;
    }

    public void setFrequency(int frequency) {
        this.frequency = frequency;
    }
}
