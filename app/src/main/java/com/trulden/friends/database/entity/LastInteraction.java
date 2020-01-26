package com.trulden.friends.database.entity;

import androidx.room.ForeignKey;
import androidx.room.Index;

import java.io.Serializable;
import java.util.Calendar;

import static androidx.room.ForeignKey.CASCADE;
import static com.trulden.friends.util.Util.calendarDaysBetween;

/**
 * Shows how much time passed since interaction of some type with some friend.
 * Objects of this class are called «Last Interaction trackers» or just «trackers» for brevity.
 */
@androidx.room.Entity(
        tableName = "last_interaction_table",

        primaryKeys = {"friendId", "typeId"},

        foreignKeys = {
                @ForeignKey(entity = Friend.class,
                            parentColumns = "id",
                            childColumns = "friendId",
                            onDelete = CASCADE),

                @ForeignKey(entity = InteractionType.class,
                            parentColumns = "id",
                            childColumns = "typeId",
                            onDelete = CASCADE),

                @ForeignKey(entity = Interaction.class,
                            parentColumns = "id",
                            childColumns = "interactionId",
                            onDelete = CASCADE)
        },

        indices = {
                @Index(value = "typeId"),
                @Index(value = "interactionId")
        }
)
public class LastInteraction implements Entity, Serializable {

    private long friendId;

    private long typeId;

    private long interactionId;

    private long date;

    /**
     * Used to set visibility
     */
    private long status;

    private long frequency;

    private boolean ready;

    /**
     * Constructor for LI entry. Must be used only by database classes
     * @param typeId type of Interaction
     * @param date Unix epoch time of interaction
     * @param ready shows if it's time to interact again
     */
    public LastInteraction(long friendId, long typeId, long interactionId, long date, long status,
                           long frequency, boolean ready){
        this.friendId = friendId;
        this.typeId = typeId;
        this.interactionId = interactionId;
        this.date = date;
        this.status = status;
        this.frequency = frequency;
        this.ready = ready;
    }

    public void calculateReadiness(){
        Calendar dateCalendar = Calendar.getInstance();
        dateCalendar.setTimeInMillis(date);

        ready = calendarDaysBetween(Calendar.getInstance(), dateCalendar) >= frequency;
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

    public long getTypeId() {
        return typeId;
    }

    public long getFriendId() {
        return friendId;
    }

    public void setFriendId(long friendId) {
        this.friendId = friendId;
    }

    public long getInteractionId() {
        return interactionId;
    }

    public void setInteractionId(long interactionId) {
        this.interactionId = interactionId;
    }

    public void setTypeId(long typeId) {
        this.typeId = typeId;
    }

    public long getStatus() {
        return status;
    }

    public void setStatus(long status) {
        this.status = status;
    }

    public long getFrequency() {
        return frequency;
    }

    public void setFrequency(long frequency) {
        this.frequency = frequency;
    }

    public boolean isReady() {
        return ready;
    }

    public void setReady(boolean ready) {
        this.ready = ready;
    }
}
