package com.trulden.friends.database;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

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

/**
 * View model of app database
 */
public class FriendsViewModel extends AndroidViewModel {

    private FriendsRepository mRepository;

    private LiveData<List<Friend>> mAllFriends;
    private LiveData<List<InteractionType>> mAllInteractionTypes;
    private LiveData<List<Interaction>> mAllInteractions;

    private MutableLiveData<Boolean> mShowHiddenLI;

    private HashMap<String, HashSet<Integer>> mLITFSelections;

    private int mSelectedLITabPos = 0;

    private LastInteractionWrapper mTrackerInFragment;

    public FriendsViewModel(@NonNull Application application) {
        super(application);

        mRepository = new FriendsRepository(application);

        mAllFriends = mRepository.getAllFriends();
        mAllInteractionTypes = mRepository.getAllInteractionTypes();
        mAllInteractions = mRepository.getAllInteractions();

        mShowHiddenLI = new MutableLiveData<>();

        mLITFSelections = new HashMap<>();
    }

    public HashSet<Integer> getLITFSelections(String type){
        HashSet<Integer> selection = mLITFSelections.get(type);

        if(selection == null){
            selection = new HashSet<>();
        }

        return selection;
    }

    public void clearLITFSelections() {
        mLITFSelections.clear();
    }

    // FIXME maybe turn to «setSelectedPositions()» and use it everywhere?
    public void setLITF_selections(String type, HashSet<Integer> selection){
        mLITFSelections.put(type, selection);
    }

    public FriendsDao getDao(){return mRepository.getDao();}

    public LiveData<List<Friend>> getAllFriends() {
        return mAllFriends;
    }
    public LiveData<List<InteractionType>> getAllInteractionTypes() {
        return mAllInteractionTypes;
    }
    public LiveData<List<Interaction>> getAllInteractions() {
        return mAllInteractions;
    }

    public LiveData<List<LastInteractionWrapper>> getLiveAllLastInteractionWrappers() { return mRepository.getLiveAllLastInteractionWrappers(); }
    public LiveData<List<LastInteractionWrapper>> getLiveVisibleLastInteractionWrappers() { return mRepository.getLiveVisibleLastInteractionWrappers(); }
    public LiveData<List<InteractionWithFriendIDs>> getInteractionsWithFriendIDs() {return mRepository.getInteractionsWithFriendsIDs();}
    public LiveData<List<FriendName>> getFriendNames() { return mRepository.getFriendNames(); }

    public void add(Friend friend)    { mRepository.add(friend);    }
    public void update(Friend friend) { mRepository.update(friend); }
    public void delete(Friend friend) { mRepository.delete(friend); }

    public void add(InteractionType interactionType)    { mRepository.add(interactionType);    }
    public void update(InteractionType interactionType) { mRepository.update(interactionType); }
    public void delete(InteractionType interactionType) { mRepository.delete(interactionType); }

    public void add(Interaction interaction, HashSet<Long> friendsIds)    { mRepository.add(interaction, friendsIds);    }
    public void update(Interaction interaction, HashSet<Long> friendsIds) { mRepository.update(interaction, friendsIds); }
    public void delete(Interaction interaction, HashSet<Long> friendIDs)  { mRepository.delete(interaction, friendIDs);  }

    public void update(LastInteraction lastInteraction) { mRepository.update(lastInteraction); }

    public LiveData<Boolean> getShowHiddenLI() {
        return mShowHiddenLI;
    }

    public boolean getShowHiddenLIValue(){
        return mShowHiddenLI.getValue();
    }

    public void setShowHiddenLI(boolean showHiddenLI) {
        mShowHiddenLI.setValue(showHiddenLI);
    }

    public int getSelectedLITabPos() {
        return mSelectedLITabPos;
    }

    public void setSelectedLITabPos(int mSelectedLITabPos) {
        this.mSelectedLITabPos = mSelectedLITabPos;
    }

    public LiveData<List<LastInteractionWrapper>> getLiveLastInteractionWrappersOfAFriend(long friendId) {
        return mRepository.getLiveLastInteractionWrappersOfAFriend(friendId);
    }

    public LiveData<List<Interaction>> getInteraction(long interactionId) {
        return mRepository.getInteraction(interactionId);
    }

    public LiveData<List<String>> getCoParticipantNames(long interactionId, String friendsName) {
        return mRepository.getCoParticipantNames(interactionId, friendsName);
    }

    public LastInteractionWrapper getTrackerInFragment() {
        return mTrackerInFragment;
    }

    public void setTrackerInFragment(LastInteractionWrapper trackerInFragment) {
        this.mTrackerInFragment = trackerInFragment;
    }

    public LiveData<LastInteractionWrapper> getLiveLastInteractionWrapper(long typeId, long friendId) {
        return mRepository.getLiveLastInteractionWrapper(typeId, friendId);
    }

    public void checkLastInteractionsReadiness() {
        mRepository.checkLastInteractionsReadiness();
    }
}
