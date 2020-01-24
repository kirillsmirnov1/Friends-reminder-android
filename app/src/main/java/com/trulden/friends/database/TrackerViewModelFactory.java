package com.trulden.friends.database;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

/**
 * Factory for {@link TrackerViewModel}
 * Used in {@link com.trulden.friends.activity.TrackerFragment TrackerFragment}
 */
public class TrackerViewModelFactory implements ViewModelProvider.Factory {

    private Application application;

    private long friendId;
    private long typeId;

    public TrackerViewModelFactory(@NonNull Application application, long friendId, long typeId) {
        this.application = application;

        this.friendId = friendId;
        this.typeId = typeId;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new TrackerViewModel(application, friendId, typeId);
    }
}
