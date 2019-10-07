package com.trulden.friends.database;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.trulden.friends.database.entity.Friend;
import com.trulden.friends.database.entity.Interaction;
import com.trulden.friends.database.entity.InteractionType;
import com.trulden.friends.database.wrappers.FriendName;
import com.trulden.friends.database.wrappers.InteractionWithFriendIDs;
import com.trulden.friends.database.entity.LastInteraction;

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

    public FriendsViewModel(@NonNull Application application) {
        super(application);

        mRepository = new FriendsRepository(application);

        mAllFriends = mRepository.getAllFriends();
        mAllInteractionTypes = mRepository.getAllInteractionTypes();
        mAllInteractions = mRepository.getAllInteractions();
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

    public LiveData<List<LastInteraction>> getLastInteractions(/*long currDate*/) { return mRepository.getLastInteractions(/*currDate*/); }
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
    public void delete(Interaction interaction)                           { mRepository.delete(interaction);             }

}
