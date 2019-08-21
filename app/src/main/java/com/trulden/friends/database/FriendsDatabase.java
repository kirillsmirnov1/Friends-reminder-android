package com.trulden.friends.database;

import android.content.Context;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

@Database(entities = {Friend.class}, version = 1, exportSchema = false)
public abstract class FriendsDatabase extends RoomDatabase {

    private static FriendsDatabase INSTANCE;

    private static RoomDatabase.Callback sRoomDataBaseCallback = new RoomDatabase.Callback(){
        @Override
        public void onOpen(@NonNull SupportSQLiteDatabase db) {
            super.onOpen(db);

            new PopulateDBAsync(INSTANCE).execute();
        }
    };

    public static FriendsDatabase getDatabase(final Context context){

        if(INSTANCE == null){
            synchronized (FriendsDatabase.class){
                if(INSTANCE == null){
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            FriendsDatabase.class, "friends_database")
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

        String[] defaultFriends = {"Aaron", "Benjamin", "Carol"};

        private final FriendsDao mDao;

        PopulateDBAsync(FriendsDatabase db){
            mDao = db.friendsDao();
        }

        @Override
        protected Void doInBackground(Void... voids) {

            if(mDao.getAnyFriend().length<1){
                for(String friend : defaultFriends){
                    mDao.addFriend(new Friend(friend, ""));
                }
            }

            return null;
        }
    }

}
