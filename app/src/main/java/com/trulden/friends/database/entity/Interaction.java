package com.trulden.friends.database.entity;

import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import static androidx.room.ForeignKey.CASCADE;

/**
 * Interaction of user with their friends.
 *
 * Every interaction must be of specific {@link InteractionType}.
 *
 * One interaction can be with several Friends at once, which is handled by {@link BindFriendInteraction}.
 */
@androidx.room.Entity(tableName = "interaction_table",

        foreignKeys = {
        @ForeignKey(entity = InteractionType.class,
                    parentColumns = "id",
                    childColumns = "interactionTypeId",
                    onDelete = CASCADE)
        },

        indices =
        @Index(value = "interactionTypeId")
)
public class Interaction implements Entity {

    @PrimaryKey(autoGenerate = true)
    private long id;

    private long interactionTypeId;

    private long date;

    private String comment;

    private String friendNames;

    /**
     * Constructor for creating new Interaction
     * @param interactionTypeId id of type of interaction
     * @param date date of interaction in Unix epoch time
     * @param comment plain text notes about interaction
     * @param friendNames plain text friend names. Should be divided by comma
     */
    public Interaction(long interactionTypeId, long date, String comment, String friendNames) {
        this.interactionTypeId = interactionTypeId;
        this.date = date;
        this.comment = comment;
        this.friendNames = friendNames;
    }

    /**
     * Constructor for updating existing Interaction
     * @param id id of Interaction in database
     * @param interactionTypeId id of type of interaction
     * @param date date of interaction in Unix epoch time
     * @param comment plain text notes about interaction
     * @param friendNames plain text friend names. Should be divided by comma.
     *                    Should be used only for ease of demonstration.
     *                    Access to real friends object should be handled through BindFriendInteraction.
     *
     * @see InteractionType InteractionType
     * @see BindFriendInteraction BindFriendInteraction
     */
    @Ignore
    public Interaction(long id, long interactionTypeId, long date, String comment, String friendNames) {
        this.id = id;
        this.interactionTypeId = interactionTypeId;
        this.date = date;
        this.comment = comment;
        this.friendNames = friendNames;
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

    public long getInteractionTypeId() {
        return interactionTypeId;
    }

    /**
     * @return date of interaction in Unix epoch time
     */
    public long getDate() {
        return date;
    }

    /**
     * @param date date of interaction in Unix epoch time
     */
    public void setDate(long date) {
        this.date = date;
    }

    public String getComment() {
        return comment;
    }

    /**
     * Those names should be used only for ease of demonstration.
     * Access to real friends object should be handled through BindFriendInteraction.
     * @return names of friends divided by comma
     */
    public String getFriendNames() {
        return friendNames;
    }
}
