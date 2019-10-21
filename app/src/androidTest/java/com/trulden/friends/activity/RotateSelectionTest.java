package com.trulden.friends.activity;

import android.content.pm.ActivityInfo;
import android.view.View;

import com.trulden.friends.AbstractTest;

import org.hamcrest.Matcher;
import org.junit.Test;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.*;
import static androidx.test.espresso.assertion.ViewAssertions.*;
import static androidx.test.espresso.matcher.ViewMatchers.*;

public class RotateSelectionTest extends AbstractTest {

    @Test
    public void rotateInteractionsTest(){
        rotateTest("Log", withText("1"), "A + B");
    }

    private void rotateTest(String activity, Matcher<View> matcher, String entryText){
        switch(activity){
            case "LI":
                openLastInteractions();
                break;
            case "Log":
                openLog();
                break;
            case "Friends":
                openFriends();
                break;
            case "Types":
                openTypes();
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
