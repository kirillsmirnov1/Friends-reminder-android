package com.trulden.friends.database;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import com.trulden.friends.database.entity.Friend;
import com.trulden.friends.database.entity.InteractionType;

import java.util.List;

class FriendsRepository {

    private FriendsDao mFriendsDao;
    private LiveData<List<Friend>> mAllFriends;
    private LiveData<List<InteractionType>> mAllInteractionTypes;

    FriendsRepository(Application application){
        mFriendsDao = FriendsDatabase.getDatabase(application).friendsDao();

        mAllFriends = mFriendsDao.getAllFriends();
        mAllInteractionTypes = mFriendsDao.getAllInteractionTypes();
    }

    LiveData<List<Friend>> getAllFriends() { return mAllFriends; }
    LiveData<List<InteractionType>> getAllInteractionTypes() { return mAllInteractionTypes; }

    // TODO InteractionType tasks
    enum TaskSelector{
        ADD_FRIEND,
        UPDATE_FRIEND,
        REMOVE_FRIEND
    }

    void addFriend(Friend friend){
        new FriendAsyncTask(TaskSelector.ADD_FRIEND, mFriendsDao)
                .execute(friend);
    }

    void updateFriend(Friend friend){
        new FriendAsyncTask(TaskSelector.UPDATE_FRIEND, mFriendsDao)
                .execute(friend);
    }

    void deleteFriend(Friend friend){
        new FriendAsyncTask(TaskSelector.REMOVE_FRIEND, mFriendsDao)
                .execute(friend);
    }

    private static class FriendAsyncTask extends AsyncTask<Friend, Void, Void>{

        private FriendsDao mFriendsDao;
        private TaskSelector mTaskSelector;

        FriendAsyncTask(TaskSelector taskSelector, FriendsDao friendsDao){
            mFriendsDao = friendsDao;
            mTaskSelector = taskSelector;
        }

        @Override
        protected Void doInBackground(Friend... friends) {

            switch (mTaskSelector){
                case ADD_FRIEND:
                    mFriendsDao.add(friends[0]);
                    break;

                case UPDATE_FRIEND:
                    mFriendsDao.update(friends[0]);
                    break;

                case REMOVE_FRIEND:
                    mFriendsDao.delete(friends[0]);
                    break;

                default:
                    // Do nothing
            }

            return null;
        }
    }

}
