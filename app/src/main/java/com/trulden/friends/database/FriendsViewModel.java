package com.trulden.friends.database;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

public class FriendsViewModel extends AndroidViewModel {

    private FriendsRepository mRepository;
    private LiveData<List<Friend>> mAllFriends;

    public FriendsViewModel(@NonNull Application application) {
        super(application);

        mRepository = new FriendsRepository(application);
        mAllFriends = mRepository.getAllFriends();
    }


    public LiveData<List<Friend>> getAllFriends() {
        return mAllFriends;
    }

    public void addFriend(Friend friend){mRepository.addFriend(friend);}
    public void updateFriend(Friend friend){mRepository.updateFriend(friend); }
    public void deleteFriend(Friend friend){mRepository.deleteFriend(friend);}
}
