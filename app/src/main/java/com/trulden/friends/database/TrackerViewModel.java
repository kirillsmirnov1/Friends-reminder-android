package com.trulden.friends.database;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;

import com.trulden.friends.database.entity.Interaction;
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
    private LiveData<Interaction> mInteraction;
    private LiveData<List<String>> mCoParticipantNames;

    private final Observer<LastInteractionWrapper> lastInteractionWrapperObserver;

    private FriendsRepository mRepository;

    TrackerViewModel(@NonNull Application application, long friendId, long typeId){
        super(application);

        mRepository = new FriendsRepository(application);

        mLastInteractionWrapper = mRepository.getLiveLastInteractionWrapper(typeId, friendId);

        lastInteractionWrapperObserver = lastInteractionWrapper -> {
            long interactionId = lastInteractionWrapper.getLastInteraction().getInteractionId();
            String friendName = lastInteractionWrapper.getFriendName();

            mInteraction = mRepository.getInteraction(interactionId);
            mCoParticipantNames = mRepository.getCoParticipantNames(interactionId, friendName);
        };

        // Must be removed in onCleared()
        mLastInteractionWrapper.observeForever(lastInteractionWrapperObserver);
    }

    @Override
    protected void onCleared() {
        super.onCleared();

        mLastInteractionWrapper.removeObserver(lastInteractionWrapperObserver);
    }

    public LiveData<LastInteractionWrapper> getLastInteractionWrapper() {
        return mLastInteractionWrapper;
    }

    public LiveData<Interaction> getInteraction() {
        return mInteraction;
    }

    public LiveData<List<String>> getCoParticipantNames() {
        return mCoParticipantNames;
    }

    public void update(LastInteraction lastInteraction) {
        mRepository.update(lastInteraction);
    }
}
