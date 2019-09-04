package com.trulden.friends.database;

import android.content.Context;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.trulden.friends.database.entity.Friend;
import com.trulden.friends.util.Util;

@Database(entities = {Friend.class}, version = Util.DATABASE_VERSION, exportSchema = false)
public abstract class FriendsDatabase extends RoomDatabase {

    public static final String DATABASE_NAME = "friends_database";

    private static FriendsDatabase INSTANCE;

    private static RoomDatabase.Callback sRoomDataBaseCallback = new RoomDatabase.Callback(){
        @Override
        public void onOpen(@NonNull SupportSQLiteDatabase db) {
            super.onOpen(db);

            new PopulateDBAsync(INSTANCE).execute();
        }
    };

    public static void wipeDatabaseInstance(){
        INSTANCE = null;
    }

    public static FriendsDatabase getDatabase(final Context context){

        if(INSTANCE == null){
            synchronized (FriendsDatabase.class){
                if(INSTANCE == null){
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            FriendsDatabase.class, DATABASE_NAME)
                            .fallbackToDestructiveMigration()
                            .addCallback(sRoomDataBaseCallback)
                            .build();
                }
            }
        }

        return INSTANCE;
    }

    public abstract FriendsDao friendsDao();

    private static class PopulateDBAsync extends AsyncTask<Void, Void, Void>{

        String[] defaultFriends =
                {"Aaron", "Benjamin", "Carol", "Dominick", "Eve", "Frank", "George", "Hamlet", "Ian",
                 "Jacob", "Kate", "Leonard", "Michael", "Nikolas", "Oprah", "Peter", "Quynh",
                 "Richard", "Stephen", "Thomas", "Utah", "Victor", "Wilfred", "Xan", "Yan", "Zorro"};

        private final FriendsDao mDao;

        PopulateDBAsync(FriendsDatabase db){
            mDao = db.friendsDao();
        }

        @Override
        protected Void doInBackground(Void... voids) {

            if(mDao.getAnyFriend().length<1){
                for(String friend : defaultFriends){
                    mDao.add(new Friend(friend, ""));
                }
            }

            return null;
        }
    }

}
