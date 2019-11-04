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

    /**
     * Used in onCreate() to check if fragment in fact exists and needs to be shown
     */
    void checkIfTrackerFragmentNeedsToBeShown();

    /**
     * Sets visibility of am_fade_background and am_tracker_over_layout
     * @param visibility View.VISIBLE or View.GONE
     */
    void setTrackerOverActivityVisibility(int visibility);
}
