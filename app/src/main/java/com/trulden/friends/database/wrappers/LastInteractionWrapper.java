package com.trulden.friends.database.wrappers;

import androidx.room.Embedded;
import androidx.room.Relation;

import com.trulden.friends.database.entity.Entity;
import com.trulden.friends.database.entity.Friend;
import com.trulden.friends.database.entity.InteractionType;
import com.trulden.friends.database.entity.LastInteraction;

import java.util.List;

import static com.trulden.friends.util.Util.daysPassed;

/**
 * Wrapper for {@link LastInteraction}.
 * Contains LI object, and corresponding {@link Friend} and {@link InteractionType} objects in lists.
 * Provides {@link #itsTime()} method for checking if it's time to interact again.
 */
public class LastInteractionWrapper implements Entity {
    @Embedded
    private LastInteraction lastInteraction;

    @Relation(
            parentColumn = "friendId", entityColumn = "id",
            entity = Friend.class
    )
    private List<Friend> friends;

    @Relation(
            parentColumn = "typeId", entityColumn = "id",
            entity = InteractionType.class
    )
    private List<InteractionType> types;

    /**
     * Check if it's time to interact again
     * @return true if enough days have passed
     */
    public boolean itsTime(){
        return lastInteraction.isReady();
    }

    public InteractionType getType(){
        return types.get(0);
    }

    public String getFriendName(){
        return friends.get(0).getName();
    }

    public LastInteraction getLastInteraction() {
        return lastInteraction;
    }

    public void setLastInteraction(LastInteraction lastInteraction) {
        this.lastInteraction = lastInteraction;
    }

    public List<Friend> getFriends() {
        return friends;
    }

    public void setFriends(List<Friend> friends) {
        this.friends = friends;
    }

    public List<InteractionType> getTypes() {
        return types;
    }

    public void setTypes(List<InteractionType> types) {
        this.types = types;
    }

    public Friend getFriend() {
        return friends.get(0);
    }

    public String getTypeName(){
        return types.get(0).getInteractionTypeName();
    }

    public long getFrequency(){
        return lastInteraction.getFrequency();
    }

    public enum LastInteractionStatus{
        DEFAULT, // Always should be first because 0 is default value for status column
        HIDDEN
    }
}
