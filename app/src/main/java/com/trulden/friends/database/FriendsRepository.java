package com.trulden.friends.database;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import com.trulden.friends.database.entity.BindFriendInteraction;
import com.trulden.friends.database.entity.Friend;
import com.trulden.friends.database.entity.Interaction;
import com.trulden.friends.database.entity.InteractionType;

import java.util.HashSet;
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

    enum TaskSelector{
        ADD_FRIEND,
        UPDATE_FRIEND,
        REMOVE_FRIEND,

        ADD_INTERACTION_TYPE,
        UPDATE_INTERACTION_TYPE,
        REMOVE_INTERACTION_TYPE,

        ADD_INTERACTION,
        UPDATE_INTERACTION,
        REMOVE_INTERACTION
    }

    // -----------------------------------------
    // Friend
    // -----------------------------------------

    void add(Friend friend){
        new FriendAsyncTask(TaskSelector.ADD_FRIEND, mFriendsDao)
                .execute(friend);
    }

    void update(Friend friend){
        new FriendAsyncTask(TaskSelector.UPDATE_FRIEND, mFriendsDao)
                .execute(friend);
    }

    void delete(Friend friend){
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

    // -----------------------------------------
    // InteractionType
    // -----------------------------------------

    void add(InteractionType interactionType){
        new InteractionTypeAsyncTask(TaskSelector.ADD_INTERACTION_TYPE, mFriendsDao)
                .execute(interactionType);
    }

    void update(InteractionType interactionType){
        new InteractionTypeAsyncTask(TaskSelector.UPDATE_INTERACTION_TYPE, mFriendsDao)
                .execute(interactionType);
    }

    void delete(InteractionType interactionType){
        new InteractionTypeAsyncTask(TaskSelector.REMOVE_INTERACTION_TYPE, mFriendsDao)
                .execute(interactionType);
    }

    private static class InteractionTypeAsyncTask extends AsyncTask<InteractionType, Void, Void>{

        private FriendsDao mFriendsDao;
        private TaskSelector mTaskSelector;

        InteractionTypeAsyncTask(TaskSelector taskSelector, FriendsDao friendsDao){
            mFriendsDao = friendsDao;
            mTaskSelector = taskSelector;
        }

        @Override
        protected Void doInBackground(InteractionType... interactionTypes) {

            switch (mTaskSelector){
                case ADD_INTERACTION_TYPE:
                    mFriendsDao.add(interactionTypes[0]);
                    break;

                case UPDATE_INTERACTION_TYPE:
                    mFriendsDao.update(interactionTypes[0]);
                    break;

                case REMOVE_INTERACTION_TYPE:
                    mFriendsDao.delete(interactionTypes[0]);
                    break;

                default:
                    // Do nothing
            }

            return null;
        }
    }

    // -----------------------------------------
    // Interaction
    // -----------------------------------------

    public void add(Interaction interaction, HashSet<Integer> friendsIds) {

    }

    private static class InteractionAsyncTask extends AsyncTask<Void, Void, Void>{

        private FriendsDao mFriendsDao;
        private TaskSelector mTaskSelector;

        private Interaction interaction;
        private HashSet<Integer> friendIds;

        public InteractionAsyncTask(FriendsDao mFriendsDao, TaskSelector mTaskSelector, Interaction interaction, HashSet<Integer> friendIds) {
            this.mFriendsDao = mFriendsDao;
            this.mTaskSelector = mTaskSelector;
            this.interaction = interaction;
            this.friendIds = friendIds;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            switch (mTaskSelector){
                case ADD_INTERACTION:

                    long interactionId = mFriendsDao.add(interaction);

                    for(Integer friendId : friendIds){
                        mFriendsDao.add(new BindFriendInteraction(friendId, interactionId));
                    }

                    break;

                case UPDATE_INTERACTION: // TODO
                    break;

                case REMOVE_INTERACTION: // TODO
                    break;

                default:
                    // Do nothing
            }

            return null;
        }
    }

}
