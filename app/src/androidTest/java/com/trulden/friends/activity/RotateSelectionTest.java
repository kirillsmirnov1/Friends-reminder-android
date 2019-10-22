package com.trulden.friends.activity;

import android.content.pm.ActivityInfo;
import android.view.View;

import org.hamcrest.Matcher;
import org.junit.Test;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.*;
import static androidx.test.espresso.assertion.ViewAssertions.*;
import static androidx.test.espresso.matcher.ViewMatchers.*;
import static org.hamcrest.Matchers.*;

public class RotateSelectionTest extends AbstractMATest {

    @Test
    public void rotateInteractionsTest(){
        rotateTest("Log", withText("1"), "A + B");
    }

    @Test
    public void rotateLITest(){

        Matcher<View> m = allOf(withText("1"), not(hasSibling(withText("Meeting"))));

        rotateTest("LI", m, "Caleb");
    }

    @Test
    public void rotateFriendsTest(){
        rotateTest("Friends", withText("1"), "Aaron");
    }

    private void rotateTest(String activity, Matcher<View> matcher, String entryText){
        switch(activity){
            case "LI":
                openLastInteractions();
                break;
            case "Log":
                openInteractions();
                break;
            case "Friends":
                openFriends();
                break;
        }

        onView(withText(entryText)).perform(longClick());

        onView(matcher).check(matches(isDisplayed()));

        mActivityTestRule.getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        sleep(500);

        onView(matcher).check(matches(isDisplayed()));

        mActivityTestRule.getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        sleep(500);

        onView(matcher).check(matches(isDisplayed()));

        onView(withText(entryText)).perform(click());

        sleep(250);

        onView(matcher).check(doesNotExist());
    }
}
