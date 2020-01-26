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

    @Query("DELETE FROM friend_table;")
    void wipeFriends();

    @Transaction
    @Query( "SELECT name FROM" +
            "((SELECT friendId FROM bind_friend_interaction_table WHERE interactionId = :interactionId)" +
            "INNER JOIN (SELECT * FROM friend_table WHERE name != :friendsName) " +
            "ON friendId=id)")
    LiveData<List<String>> getOtherFriends(long interactionId, String friendsName);

    @Transaction
    @Query("SELECT name FROM" +
            "((SELECT friendId FROM bind_friend_interaction_table WHERE interactionId = :interactionId)" +
            "INNER JOIN (SELECT * FROM friend_table) " +
            "ON friendId=id)")
    LiveData<List<String>> getFriendNamesOfInteraction(long interactionId);

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
    InteractionType getAnyInteractionType();

    @Query("SELECT * from interaction_type_table where id = :typeId LIMIT 1")
    InteractionType getTypeById(long typeId);

    @Query("DELETE FROM interaction_type_table;")
    void wipeTypes();

    @Query("SELECT * from interaction_type_table " +
            "WHERE id = :interactionTypeId " +
            "LIMIT 1;")
    LiveData<InteractionType> getType(long interactionTypeId);

    // -----------------------------------------
    // Interaction
    // -----------------------------------------

    @Insert
    long add(Interaction interaction);

    @Query("SELECT * FROM interaction_table ORDER BY date DESC")
    LiveData<List<Interaction>> getAllInteractions();

    @Query("SELECT * FROM interaction_table WHERE id = :interactionId LIMIT 1")
    Interaction getInteraction(long interactionId);

    @Query("SELECT * FROM interaction_table WHERE id = :interactionId LIMIT 1")
    LiveData<Interaction> getInteractionById(long interactionId);

    @Query("SELECT comment FROM interaction_table WHERE id = :interactionId LIMIT 1")
    LiveData<String> getInteractionComment(long interactionId);

    @Query("SELECT * FROM interaction_table ORDER BY date DESC")
    @Transaction
    LiveData<List<InteractionWithFriendIDs>> getInteractionsWithFriendIDs();

    @Delete
    void delete(Interaction interaction);

    @Update
    void update(Interaction interaction);

    @Query("DELETE FROM interaction_table WHERE id = :interactionId")
    void deleteInteractionById(long interactionId);

    @Query("DELETE FROM interaction_table;")
    void wipeInteractions();

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

    @Query("SELECT * FROM bind_friend_interaction_table WHERE interactionId = :interactionId")
    List<BindFriendInteraction> getBindsOfInteraction(long interactionId);

    @Query("SELECT COUNT(interactionId) FROM bind_friend_interaction_table WHERE interactionId = :interactionId")
    int getNumberOfInteractionBinds(long interactionId);

    @Query("DELETE FROM bind_friend_interaction_table")
    void wipeBinds();

    // -----------------------------------------
    // LastInteraction
    // -----------------------------------------

    @Insert
    void add(LastInteraction interaction);

    @Update
    void update(LastInteraction lastInteraction);

    @Delete
    void delete(LastInteraction lastInteraction);

    /**
     * @return {@link LastInteractionWrapper} list.
     */
    @Transaction
    @Query("SELECT * FROM last_interaction_table ORDER BY ready DESC, date;")
    LiveData<List<LastInteractionWrapper>> getLiveAllLastInteractionWrappers();

    @Transaction
    @Query("SELECT * FROM last_interaction_table ORDER BY date ASC")
    List<LastInteraction> getAllLastInteractions();

    @Transaction
    @Query("SELECT * FROM last_interaction_table WHERE status = 0 ORDER BY ready DESC, date")
    LiveData<List<LastInteractionWrapper>> getLiveVisibleLastInteractionWrappers();

    @Transaction
    @Query("SELECT * FROM last_interaction_table " +
            "WHERE typeId = :typeId " +
            "AND   friendId = :friendId;")
    LastInteraction getLastInteraction(long typeId, long friendId);

    @Transaction
    @Query("SELECT * FROM last_interaction_table " +
            "WHERE typeId = :typeId " +
            "AND   friendId = :friendId " +
            "LIMIT 1;")
    LiveData<LastInteractionWrapper> getLiveLastInteractionWrapper(long typeId, long friendId);

    @Transaction
    @Query("SELECT * FROM last_interaction_table " +
            "WHERE friendId = :friendId;")
    LiveData<List<LastInteractionWrapper>> getLiveLastInteractionWrappersOfAFriend(long friendId);

    @Transaction
    @Query(
        "INSERT OR IGNORE INTO \n" +
        "  last_interaction_table(friendId, typeId, interactionId, date, status, frequency, ready)\n" +
        "SELECT \n" +
        "  friendId, typeId, interactionId, MAX(date), :status, :frequency, 0\n" +
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
    void calculateLastInteraction(long typeId, long friendId, long status, long frequency);

    @Query("SELECT * FROM last_interaction_table " +
            "WHERE typeId = :typeId " +
                "AND frequency = :frequency")
    List<LastInteraction> getLastInteractionsByTypeAndFrequency(long typeId, long frequency);
}
