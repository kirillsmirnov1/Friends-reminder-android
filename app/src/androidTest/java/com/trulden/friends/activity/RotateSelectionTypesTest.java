package com.trulden.friends.activity;

import android.content.pm.ActivityInfo;
import android.view.View;

import androidx.fragment.app.FragmentActivity;
import androidx.test.rule.ActivityTestRule;

import com.trulden.friends.AbstractTest;
import com.trulden.friends.DatabaseTestingHandler;
import com.trulden.friends.TestUtil;

import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.*;
import static androidx.test.espresso.assertion.ViewAssertions.*;
import static androidx.test.espresso.matcher.ViewMatchers.*;

public class RotateSelectionTypesTest extends AbstractTest{
    @Rule
    public ActivityTestRule<InteractionTypesActivity> mRule = new ActivityTestRule<>(InteractionTypesActivity.class);

    @Before
    public void initDB(){
        DatabaseTestingHandler.initAndFillDatabase(
                (FragmentActivity) TestUtil.getActivityInstance());
    }

    @Test
    public void rotateTypesTest(){

        String entryText = "Meeting";
        Matcher<View> matcher = withText("1");

        onView(withText(entryText)).perform(longClick());

        onView(matcher).check(matches(isDisplayed()));

        mRule.getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        sleep(500);

        onView(matcher).check(matches(isDisplayed()));

        mRule.getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        sleep(500);

        onView(matcher).check(matches(isDisplayed()));

        onView(withText(entryText)).perform(click());

        sleep(250);

        onView(matcher).check(doesNotExist());
    }
}
