package com.trulden.friends.activity;

import android.content.pm.ActivityInfo;

import com.trulden.friends.AbstractTest;

import org.junit.Test;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.*;
import static androidx.test.espresso.assertion.ViewAssertions.*;
import static androidx.test.espresso.matcher.ViewMatchers.*;

public class RotateSelectionTest extends AbstractTest {

    @Test
    public void rotateInteractionsTest(){
        openLog();

        onView(withText("A + B")).perform(longClick());

        onView(withText("1")).check(matches(isDisplayed()));

        mActivityTestRule.getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        sleep(500);

        onView(withText("1")).check(matches(isDisplayed()));

        mActivityTestRule.getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        sleep(500);

        onView(withText("1")).check(matches(isDisplayed()));

        onView(withText("A + B")).perform(click());

        sleep(250);

        onView(withText("1")).check(doesNotExist());
    }
}
