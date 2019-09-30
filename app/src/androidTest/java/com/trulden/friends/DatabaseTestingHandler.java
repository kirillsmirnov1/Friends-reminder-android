package com.trulden.friends;

import android.content.Context;

import androidx.room.Room;

import com.trulden.friends.database.FriendsDao;
import com.trulden.friends.database.FriendsDatabase;

public class DatabaseTestingHandler {
    public static void initAndFillDatabase(Context context){
        FriendsDatabase db = Room.inMemoryDatabaseBuilder(context, FriendsDatabase.class).build();
        FriendsDao dao = db.friendsDao();
    }
}
