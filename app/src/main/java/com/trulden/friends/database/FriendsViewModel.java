package com.trulden.friends.database;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.trulden.friends.database.entity.Friend;
import com.trulden.friends.database.entity.InteractionType;

import java.util.List;

public class FriendsViewModel extends AndroidViewModel {

    private FriendsRepository mRepository;
    private LiveData<List<Friend>> mAllFriends;
    private LiveData<List<InteractionType>> mAllInteractionTypes;

    public FriendsViewModel(@NonNull Application application) {
        super(application);

        mRepository = new FriendsRepository(application);
        mAllFriends = mRepository.getAllFriends();
        mAllInteractionTypes = mRepository.getAllInteractionTypes();
    }


    public LiveData<List<Friend>> getAllFriends() {
        return mAllFriends;
    }
    public LiveData<List<InteractionType>> getAllInteractionTypes() {
        return mAllInteractionTypes;
    }

    public void addFriend(Friend friend){mRepository.add(friend);}
    public void updateFriend(Friend friend){mRepository.update(friend); }
    public void deleteFriend(Friend friend){mRepository.delete(friend);}
}
