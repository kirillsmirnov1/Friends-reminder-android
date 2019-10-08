package com.trulden.friends.database;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import com.trulden.friends.database.entity.BindFriendInteraction;
import com.trulden.friends.database.entity.Friend;
import com.trulden.friends.database.entity.Interaction;
import com.trulden.friends.database.entity.InteractionType;
import com.trulden.friends.database.wrappers.FriendName;
import com.trulden.friends.database.wrappers.InteractionWithFriendIDs;
import com.trulden.friends.database.wrappers.LastInteractionWrapper;

import java.util.HashSet;
import java.util.List;

/**
 * Handles database queries
 */
class FriendsRepository {

    private FriendsDao mFriendsDao;

    private LiveData<List<Friend>> mAllFriends;
    private LiveData<List<InteractionType>> mAllInteractionTypes;
    private LiveData<List<Interaction>> mAllInteractions;

    FriendsRepository(Application application){
        mFriendsDao = FriendsDatabase.getDatabase(application).friendsDao();

        mAllFriends = mFriendsDao.getAllFriends();
        mAllInteractionTypes = mFriendsDao.getAllInteractionTypes();
        mAllInteractions = mFriendsDao.getAllInteractions();
    }

    LiveData<List<Friend>> getAllFriends() { return mAllFriends; }
    LiveData<List<InteractionType>> getAllInteractionTypes() { return mAllInteractionTypes; }
    LiveData<List<Interaction>> getAllInteractions() { return mAllInteractions; }

    LiveData<List<LastInteractionWrapper>> getLastInteractions() {
        return mFriendsDao.getLastInteractions();
    }

    LiveData<List<InteractionWithFriendIDs>> getInteractionsWithFriendsIDs(){
        return mFriendsDao.getInteractionsWithFriendIDs();
    }

    LiveData<List<FriendName>> getFriendNames(){
        return mFriendsDao.getFriendNames();
    }

    FriendsDao getDao() {
        return mFriendsDao;
    }

    public void refreshLastInteractions() {

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                mFriendsDao.refreshLastInteractions();
                return null;
            }
        }.execute();
    }

    /**
     * Available tasks
     */
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

    /**
     * Handles Friend-based queries to database
     */
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

                    Friend friend = friends[0];

                    // Check if there is some interactions with this friend only and delete them

                    List<BindFriendInteraction> binds = mFriendsDao.getBindsOfFriend(friend.getId());

                    for(BindFriendInteraction bind : binds){
                        if(mFriendsDao.getNumberOfInteractionBinds(bind.getInteractionId()) < 2){
                            mFriendsDao.deleteInteractionById(bind.getInteractionId());
                        }
                    }

                    mFriendsDao.delete(friend);
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

    /**
     * Handles InteractionType-based queries to database
     */
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

    public void add(Interaction interaction, HashSet<Long> friendsIds) {
        new InteractionAsyncTask(mFriendsDao, TaskSelector.ADD_INTERACTION, interaction, friendsIds)
                .execute();
    }

    public void delete(Interaction interaction) {
        new InteractionAsyncTask(mFriendsDao, TaskSelector.REMOVE_INTERACTION, interaction, null)
                .execute();
    }

    public void update(Interaction interaction, HashSet<Long> friendsIds) {
        new InteractionAsyncTask(mFriendsDao, TaskSelector.UPDATE_INTERACTION, interaction, friendsIds)
                .execute();
    }

    /**
     * Handlse Interaction-based queries to database
     */
    private static class InteractionAsyncTask extends AsyncTask<Void, Void, Void>{

        private FriendsDao mFriendsDao;
        private TaskSelector mTaskSelector;

        private Interaction interaction;
        private HashSet<Long> friendIds;

        InteractionAsyncTask(FriendsDao mFriendsDao, TaskSelector mTaskSelector, Interaction interaction, HashSet<Long> friendIds) {
            this.mFriendsDao = mFriendsDao;
            this.mTaskSelector = mTaskSelector;
            this.interaction = interaction;
            this.friendIds = friendIds;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            switch (mTaskSelector){
                case ADD_INTERACTION: {

                    long interactionId = mFriendsDao.add(interaction);

                    for (Long friendId : friendIds) {
                        mFriendsDao.add(new BindFriendInteraction(friendId, interactionId));
                    }

                    break;
                }

                case UPDATE_INTERACTION: {

                    long interactionId = interaction.getId();

                    mFriendsDao.deleteBindingsByInteractionId(interactionId);

                    mFriendsDao.update(interaction);

                    for (Long friendId : friendIds) {
                        mFriendsDao.add(new BindFriendInteraction(friendId, interactionId));
                    }

                    break;
                }

                case REMOVE_INTERACTION: {

                    mFriendsDao.delete(interaction);

                    break;
                }

                default:
                    // Do nothing
            }

            return null;
        }
    }

}
