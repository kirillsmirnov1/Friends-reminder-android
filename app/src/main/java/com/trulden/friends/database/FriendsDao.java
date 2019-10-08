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
import com.trulden.friends.database.entity.LastInteraction;
import com.trulden.friends.database.wrappers.FriendName;
import com.trulden.friends.database.wrappers.InteractionWithFriendIDs;
import com.trulden.friends.database.wrappers.LastInteractionWrapper;

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

    @Query("DELETE FROM interaction_table WHERE id = :interactionId")
    void deleteInteractionById(long interactionId);

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

    @Insert
    void add(LastInteraction interaction);

    @Update
    void update(LastInteraction lastInteraction);

    /**
     * @return {@link LastInteractionWrapper} list.
     */
    @Transaction
    @Query("SELECT * FROM last_interaction_table ORDER BY date ASC")
    LiveData<List<LastInteractionWrapper>> getLastInteractions();

    @Transaction
    @Query("SELECT * FROM last_interaction_table " +
            "WHERE typeId = :typeId " +
            "AND   friendId = :friendId;")
    List<LastInteraction> getLastInteraction(long typeId, long friendId);

    @Transaction
    @Query(
        "INSERT OR IGNORE INTO \n" +
        "  last_interaction_table(friendId, typeId, interactionId, date)\n" +
        "SELECT \n" +
        "  friendId, typeId, interactionId, MAX(date)\n" +
        "FROM \n" +
        "    (SELECT id AS interId, interactionTypeId as typeId, date \n" +
        "    FROM interaction_table WHERE typeId = :typeId) \n" +
        "  t1 \n" +
        "  INNER JOIN\n" +
        "    (SELECT friendId, interactionId  \n" +
        "    FROM bind_friend_interaction_table WHERE friendId = :friendId)\n" +
        "  t2\n" +
        "  ON (t1.interId = t2.interactionId); "
    )
    void recalcLastInteraction(long typeId, Long friendId);

    // TODO clean up

    @Query("DELETE FROM friend_table;")
    void wipeFriends();

    @Query("DELETE FROM interaction_type_table;")
    void wipeTypes();

    @Query("DELETE FROM interaction_table;")
    void wipeInteractions();

    @Query("DELETE FROM bind_friend_interaction_table")
    void wipeBinds();

    @Transaction
    @Query(
            "INSERT OR REPLACE INTO last_interaction_table(friendId, typeId, interactionId, date)" +
                    "SELECT friendId, typeId, interactionId, date FROM\n" +
                    "(SELECT friend_table.id AS friendId, interaction_type_table.id AS typeId, interaction_table.id AS interactionId, MAX(interaction_table.date) AS date\n" +
                    " FROM \n" +
                    " (((interaction_table INNER JOIN bind_friend_interaction_table \n" +
                    "  ON interaction_table.id = bind_friend_interaction_table.interactionId) \n" +
                    "  INNER JOIN interaction_type_table\n" +
                    "  ON interaction_table.interactionTypeId = interaction_type_table.id)\n" +
                    "  INNER JOIN friend_table\n" +
                    "  ON bind_friend_interaction_table.friendId = friend_table.id)\n" +
                    " GROUP BY friendId, interactionTypeId\n" +
                    " ORDER BY interactionTypeId, date ASC)"
    )
    void refreshLastInteractions();
}
