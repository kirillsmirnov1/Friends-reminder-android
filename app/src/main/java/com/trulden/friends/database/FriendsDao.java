package com.trulden.friends.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import com.trulden.friends.database.entity.BindFriendInteraction;
import com.trulden.friends.database.entity.Friend;
import com.trulden.friends.database.entity.Interaction;
import com.trulden.friends.database.entity.InteractionType;
import com.trulden.friends.database.wrappers.FriendName;
import com.trulden.friends.database.wrappers.InteractionWithFriendIDs;
import com.trulden.friends.database.wrappers.LastInteraction;

import java.util.List;

/**
 * Defines interactions with database
 */
@Dao
public interface FriendsDao {

    // -----------------------------------------
    // Friend
    // -----------------------------------------

    @Insert
    void add(Friend friend);

    @Query("SELECT * FROM friend_table ORDER BY name COLLATE NOCASE ASC")
    LiveData<List<Friend>> getAllFriends();

    @Query("SELECT id, name FROM friend_table")
    LiveData<List<FriendName>> getFriendNames();

    @Delete
    void delete(Friend friend);

    @Update
    void update(Friend friend);

    // -----------------------------------------
    // InteractionType
    // -----------------------------------------

    @Insert
    void add(InteractionType type);

    @Query("SELECT * FROM interaction_type_table")
    LiveData<List<InteractionType>> getAllInteractionTypes();

    @Delete
    void delete(InteractionType interactionType);

    @Update
    void update(InteractionType interactionType);

    /**
     * Used to check if there is any interaction types in database
     * @return array with one or none of types
     */
    @Query("SELECT * from interaction_type_table LIMIT 1")
    InteractionType[] getAnyInteractionType();

    // -----------------------------------------
    // Interaction
    // -----------------------------------------

    @Insert
    long add(Interaction interaction);

    @Query("SELECT * FROM interaction_table ORDER BY date DESC")
    LiveData<List<Interaction>> getAllInteractions();

    @Query("SELECT * FROM interaction_table ORDER BY date DESC")
    @Transaction
    LiveData<List<InteractionWithFriendIDs>> getInteractionsWithFriendIDs();

    @Delete
    void delete(Interaction interaction);

    @Update
    void update(Interaction interaction);

    // -----------------------------------------
    // BindFriendInteraction
    // -----------------------------------------

    @Insert
    void add(BindFriendInteraction bindFriendInteraction);

    @Delete
    void delete(BindFriendInteraction bindFriendInteraction);

    /**
     * Deletes all BindFriendInteraction for this Interaction. Used when updating Interaction.
     * @param interactionId interaction for which binds will be deleted
     */
    @Query("DELETE FROM bind_friend_interaction_table WHERE interactionId = :interactionId;")
    void deleteBindingsByInteractionId(long interactionId);

    @Query("SELECT * FROM bind_friend_interaction_table WHERE friendId = :id")
    List<BindFriendInteraction> getBindsOfFriend(long id);

    @Query("SELECT COUNT(interactionId) FROM bind_friend_interaction_table WHERE interactionId = :interactionId")
    int getNumberOfInteractionBinds(long interactionId);

    // -----------------------------------------
    // LastInteraction
    // -----------------------------------------

    /**
     * @return {@link LastInteraction} list.
     */
    @Transaction
    @Query("SELECT typeId, friend, date FROM\n" +
            "(SELECT interaction_type_table.id AS typeId, frequency, friend_table.name AS friend, MAX(interaction_table.date) AS date\n" +
            " FROM \n" +
            " (((interaction_table INNER JOIN bind_friend_interaction_table \n" +
            "  ON interaction_table.id = bind_friend_interaction_table.interactionId) \n" +
            "  INNER JOIN interaction_type_table\n" +
            "  ON interaction_table.interactionTypeId = interaction_type_table.id)\n" +
            "  INNER JOIN friend_table\n" +
            "  ON bind_friend_interaction_table.friendId = friend_table.id)\n" +
            " GROUP BY friendId, interactionTypeId\n" +
            " ORDER BY interactionTypeId, date ASC)"
            //+ " WHERE date < (:currDate - frequency * :k)"
            )
    LiveData<List<LastInteraction>> getLastInteractions(/*long currDate, int k*/);
}
