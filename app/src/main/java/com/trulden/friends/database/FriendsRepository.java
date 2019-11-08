package com.trulden.friends.database;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import com.trulden.friends.database.entity.BindFriendInteraction;
import com.trulden.friends.database.entity.Friend;
import com.trulden.friends.database.entity.Interaction;
import com.trulden.friends.database.entity.InteractionType;
import com.trulden.friends.database.entity.LastInteraction;
import com.trulden.friends.database.wrappers.FriendName;
import com.trulden.friends.database.wrappers.InteractionWithFriendIDs;
import com.trulden.friends.database.wrappers.LastInteractionWrapper;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import static com.trulden.friends.database.FriendsRepository.TaskSelector.*;

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

    LiveData<List<LastInteractionWrapper>> getLiveAllLastInteractionWrappers() {
        return mFriendsDao.getLiveAllLastInteractionWrappers();
    }

    LiveData<List<LastInteractionWrapper>> getLiveVisibleLastInteractionWrappers(){
        return mFriendsDao.getLiveVisibleLastInteractionWrappers();
    }

    LiveData<List<InteractionWithFriendIDs>> getInteractionsWithFriendsIDs(){
        return mFriendsDao.getInteractionsWithFriendIDs();
    }

    LiveData<List<FriendName>> getFriendNames(){
        return mFriendsDao.getFriendNames();
    }

    public LiveData<List<LastInteractionWrapper>> getLiveLastInteractionWrappersOfAFriend(long friendId) {
        return mFriendsDao.getLiveLastInteractionWrappersOfAFriend(friendId);
    }

    FriendsDao getDao() {
        return mFriendsDao;
    }

    public LiveData<List<Interaction>> getInteraction(long interactionId) {
        return mFriendsDao.getInteractionById(interactionId);
    }

    public LiveData<List<String>> getCoParticipantNames(long interactionId, String friendsName) {
        return mFriendsDao.getCoParticipantNames(interactionId, friendsName);
    }

    public LiveData<LastInteractionWrapper> getLiveLastInteractionWrapper(long typeId, long friendId) {
        return mFriendsDao.getLiveLastInteractionWrapper(typeId, friendId);
    }

    public void checkLastInteractionsReadiness() {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                for(LastInteraction li : mFriendsDao.getAllLastInteractions()){
                    li.calculateReadiness();
                    mFriendsDao.update(li);
                }

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
        REMOVE_INTERACTION,

        UPDATE_LAST_INTERACTION
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
                    InteractionType newType = interactionTypes[0];
                    InteractionType oldType = mFriendsDao.getTypeById(newType.getId());

                    mFriendsDao.update(newType);

                    if(newType.getFrequency() != oldType.getFrequency()) {

                        List<LastInteraction> lastInteractions = mFriendsDao
                            .getLastInteractionsByTypeAndFrequency(oldType.getId(), oldType.getFrequency());

                        for(LastInteraction lastInteraction : lastInteractions){
                            lastInteraction.setFrequency(newType.getFrequency());
                            lastInteraction.calculateReadiness();
                            mFriendsDao.update(lastInteraction);
                        }
                    }
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

    public void delete(Interaction interaction, HashSet<Long> friendIDs) {
        new InteractionAsyncTask(mFriendsDao, TaskSelector.REMOVE_INTERACTION, interaction, friendIDs)
                .execute();
    }

    public void update(Interaction interaction, HashSet<Long> friendsIds) {
        new InteractionAsyncTask(mFriendsDao, TaskSelector.UPDATE_INTERACTION, interaction, friendsIds)
                .execute();
    }

    /**
     * Handles Interaction-based queries to database
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
                    long typeId = interaction.getInteractionTypeId();

                    for (Long friendId : friendIds) {
                        mFriendsDao.add(new BindFriendInteraction(friendId, interactionId));

                        LastInteraction lastInteraction = mFriendsDao
                                .getLastInteraction(typeId, friendId);

                        if(lastInteraction == null){
                            long frequency = mFriendsDao.getTypeById(typeId).getFrequency();
                            LastInteraction li = new LastInteraction(friendId, typeId, interactionId,
                                            interaction.getDate(), 0, frequency,false);
                            li.calculateReadiness();
                            mFriendsDao.add(li);
                        } else {

                            if(interaction.getDate() > lastInteraction.getDate()){
                                lastInteraction.setDate(interaction.getDate());
                                lastInteraction.setInteractionId(interactionId);
                                lastInteraction.calculateReadiness();

                                mFriendsDao.update(lastInteraction);
                            }
                        }
                    }

                    break;
                }

                case UPDATE_INTERACTION: {

                    long interactionId = interaction.getId();

                    Interaction oldInteraction = mFriendsDao.getInteraction(interactionId).get(0);
                    List<BindFriendInteraction> oldBinds = mFriendsDao.getBindsOfInteraction(interactionId);

                    // It's easier to delete old binds and recalculate them, than to check every change
                    mFriendsDao.deleteBindingsByInteractionId(interactionId);

                    mFriendsDao.update(interaction);

                    for (Long friendId : friendIds) {
                        mFriendsDao.add(new BindFriendInteraction(friendId, interactionId));

                        calculateLastInteraction(interaction.getInteractionTypeId(), friendId);

                        // If type of interaction changed, need to recalc LI of that type too
                        if(interaction.getInteractionTypeId() != oldInteraction.getInteractionTypeId()){
                            calculateLastInteraction(oldInteraction.getInteractionTypeId(), friendId);
                        }
                    }

                    // Handle deleted friends
                    for(BindFriendInteraction bind : oldBinds){
                        if(!friendIds.contains(bind.getFriendId())){
                            calculateLastInteraction(
                                    oldInteraction.getInteractionTypeId(), bind.getFriendId());
                        }
                    }

                    break;
                }

                case REMOVE_INTERACTION: {

                    long typeId = interaction.getInteractionTypeId();
                    HashMap<Long, Long> statuses = new HashMap<>();
                    HashMap<Long, Long> frequencies = new HashMap<>();

                    for(Long friendId : friendIds){
                        LastInteraction lastInteraction = mFriendsDao.getLastInteraction(typeId, friendId);

                        statuses.put(friendId, lastInteraction.getStatus());
                        frequencies.put(friendId, lastInteraction.getFrequency());
                    }

                    mFriendsDao.delete(interaction);

                    for(Long friendId : friendIds){
                        LastInteraction lastInteraction = mFriendsDao
                                .getLastInteraction(typeId, friendId);

                        // If LI connected to Interaction is deleted, need to calculate new one
                        if(lastInteraction == null){
                            calculateLastInteraction(typeId, friendId, statuses.get(friendId), frequencies.get(friendId));
                        }
                    }

                    break;
                }

                default:
                    // Do nothing
            }

            return null;
        }

        /**
         * Calculates and integrates fresh {@link LastInteraction} entry.
         * Call if old entry isn't deleted.
         */
        private void calculateLastInteraction(long typeId, long friendId){
            LastInteraction oldLastInteraction;
            long status;
            long frequency;

            try {
                oldLastInteraction = mFriendsDao.getLastInteraction(typeId, friendId);
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
                oldLastInteraction = null;
            }


            if(oldLastInteraction == null){
                status = 0;
                frequency = mFriendsDao.getTypeById(typeId).getFrequency();
            } else {
                status =  oldLastInteraction.getStatus();
                frequency = oldLastInteraction.getFrequency();
                mFriendsDao.delete(oldLastInteraction);
            }

            calculateLastInteraction(typeId, friendId, status, frequency);
        }

        /**
         * Calculates and integrates fresh {@link LastInteraction} entry.
         * Call if old entry is deleted
         */
        private void calculateLastInteraction(long typeId, long friendId, long status, long frequency){
            mFriendsDao.calculateLastInteraction(typeId, friendId, status, frequency);

            try {
                LastInteraction lastInteraction = mFriendsDao.getLastInteraction(typeId, friendId);
                lastInteraction.calculateReadiness();
                mFriendsDao.update(lastInteraction);
            } catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    // -----------------------------------------
    // Last Interaction
    // -----------------------------------------

    public void update(LastInteraction lastInteraction) {
        new LastInteractionAsyncTask(mFriendsDao, UPDATE_LAST_INTERACTION, lastInteraction)
                .execute();
    }

    private static class LastInteractionAsyncTask extends AsyncTask<Void, Void, Void>{

        FriendsDao mFriendsDao;
        TaskSelector mTaskSelector;
        LastInteraction mLastInteraction;

        public LastInteractionAsyncTask(FriendsDao friendsDao, TaskSelector selector, LastInteraction interaction) {
            mFriendsDao = friendsDao;
            mTaskSelector = selector;
            mLastInteraction = interaction;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            if(mTaskSelector == UPDATE_LAST_INTERACTION) {
                mLastInteraction.calculateReadiness();
                mFriendsDao.update(mLastInteraction);
            }
            return null;
        }
    }
}
