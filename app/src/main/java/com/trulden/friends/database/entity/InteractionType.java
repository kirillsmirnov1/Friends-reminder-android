package com.trulden.friends.database.entity;

import androidx.annotation.NonNull;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.io.Serializable;

/**
 * Type of Interaction.
 * Consists of unique name and proposed frequency of interactions
 */
@androidx.room.Entity(tableName = "interaction_type_table")
public class InteractionType implements Entity, Serializable {

    @PrimaryKey(autoGenerate = true)
    private long id;

    @NonNull
    private String interactionTypeName;

    private int frequency;

    /**
     * Constructor for creating new InteractionType
     * @param interactionTypeName unique name of this type
     * @param frequency proposed frequency for interactions of this type
     */
    public InteractionType(@NonNull String interactionTypeName, int frequency){
        this.interactionTypeName = interactionTypeName;
        this.frequency = frequency;
    }

    /**
     * Constructor for updating existing InteractionType
     * @param id id of this type in database
     * @param interactionTypeName unique name of this type
     * @param frequency proposed frequency for interactions of this type
     */
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
