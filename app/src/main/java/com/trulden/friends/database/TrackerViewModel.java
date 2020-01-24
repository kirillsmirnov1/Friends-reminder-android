package com.trulden.friends.database;

import android.app.Application;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.trulden.friends.database.entity.LastInteraction;
import com.trulden.friends.database.wrappers.LastInteractionWrapper;

import java.util.List;

/**
 * ViewModel with Tracker/{@link LastInteraction} data
 * Must be instantiated with {@link TrackerViewModelFactory}
 * Used in {@link com.trulden.friends.activity.TrackerFragment TrackerFragment}
 */
public class TrackerViewModel extends AndroidViewModel {

    private final LiveData<LastInteractionWrapper> mLastInteractionWrapper;
    private LiveData<List<String>> mOtherFriendsList;
    private LiveData<String> mInteractionComment;
    private MutableLiveData<String> mOtherFriends;

    private final Observer<List<String>> mOtherFriendsObserver;
    private final Observer<LastInteractionWrapper> mLastInteractionWrapperObserver;

    private FriendsRepository mRepository;

    TrackerViewModel(@NonNull Application application, long friendId, long typeId){
        super(application);

        mRepository = new FriendsRepository(application);

        mLastInteractionWrapper = mRepository.getLiveLastInteractionWrapper(typeId, friendId);
        mOtherFriends = new MutableLiveData<>();

        mOtherFriendsObserver = otherFriends -> {
            String names =
                otherFriends.size() > 0
                ? "+ " + TextUtils.join(", ", otherFriends)
                : "";
            mOtherFriends.setValue(names);
        };

        mLastInteractionWrapperObserver = lastInteractionWrapper -> {
            long interactionId = lastInteractionWrapper.getLastInteraction().getInteractionId();
            String friendName = lastInteractionWrapper.getFriendName();

            mInteractionComment = mRepository.getInteractionComment(interactionId);
            mOtherFriendsList = mRepository.getCoParticipantNames(interactionId, friendName); // TODO rename

            // Must be removed in onCleared()
            mOtherFriendsList.observeForever(mOtherFriendsObserver);
        };

        // Must be removed in onCleared()
        mLastInteractionWrapper.observeForever(mLastInteractionWrapperObserver);
    }

    @Override
    protected void onCleared() {
        super.onCleared();

        mLastInteractionWrapper.removeObserver(mLastInteractionWrapperObserver);
        mOtherFriendsList.removeObserver(mOtherFriendsObserver);
    }

    public LiveData<LastInteractionWrapper> getLastInteractionWrapper() {
        return mLastInteractionWrapper;
    }

    public void update(LastInteraction lastInteraction) {
        mRepository.update(lastInteraction);
    }

    public LiveData<String> getInteractionComment() {
        return mInteractionComment;
    }

    public LiveData<String> getOtherFriends() {
        return mOtherFriends;
    }
}
