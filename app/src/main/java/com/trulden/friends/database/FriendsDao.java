package com.trulden.friends.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface FriendsDao {

    @Insert
    void addFriend(Friend friend);

    @Query("SELECT * FROM friend_table")
    LiveData<List<Friend>> getAllFriends();

    @Delete
    void deleteFriend(Friend friend);

    @Update
    void update(Friend friend);
}
