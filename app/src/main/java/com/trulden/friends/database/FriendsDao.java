package com.trulden.friends.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.trulden.friends.database.entity.Friend;
import com.trulden.friends.database.entity.InteractionType;

import java.util.List;

@Dao
public interface FriendsDao {

    // -----------------------------------------
    // Friend
    // -----------------------------------------

    @Insert
    void add(Friend friend);

    @Query("SELECT * FROM friend_table ORDER BY name COLLATE NOCASE ASC")
    LiveData<List<Friend>> getAllFriends();

    @Delete
    void delete(Friend friend);

    @Update
    void update(Friend friend);

    @Query("SELECT * from friend_table LIMIT 1")
    Friend[] getAnyFriend();

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

    @Query("SELECT * from interaction_type_table LIMIT 1")
    Friend[] getAnyInteractionType();
}
