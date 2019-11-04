package com.trulden.friends.activity;

import android.content.pm.ActivityInfo;

import org.junit.Test;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.pressBack;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

public class RotateTrackerFragmentTest extends AbstractMATest {

    @Test
    public void rotateInLIFTest(){

        openLastInteractions();

        onView(withText("Caleb")).perform(click());

        mActivityTestRule.getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        onView(withText("B + C")).check(matches(isDisplayed()));

        mActivityTestRule.getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        onView(withText("Aaron")).perform(click());
    }

    @Test
    public void rotateInFriendsTest(){
        openFriends();
        onView(withText("Aaron")).perform(click());

        onView(withText("Meeting")).perform(click());

        mActivityTestRule.getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        onView(withText("A + B")).check(matches(isDisplayed()));

        mActivityTestRule.getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        onView(withText("Last Interactions")).perform(click());

        pressBack();
    }
}
