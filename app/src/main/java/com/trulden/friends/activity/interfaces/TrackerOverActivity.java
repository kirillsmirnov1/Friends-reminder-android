package com.trulden.friends.activity.interfaces;

import com.trulden.friends.database.wrappers.LastInteractionWrapper;

/**
 * Shows {@link com.trulden.friends.activity.TrackerFragment TrackerFragment} over activity implementing this interface.
 */
public interface TrackerOverActivity {
    /**
     * Show {@link com.trulden.friends.activity.TrackerFragment TrackerFragment} over activity.
     * @param lastInteractionWrapper data to be shown in tracker.
     */
    void showTrackerOverActivity(LastInteractionWrapper lastInteractionWrapper);

    /**
     * Destroy fragment and hide views.
     */
    void closeTrackerOverActivity();
}
