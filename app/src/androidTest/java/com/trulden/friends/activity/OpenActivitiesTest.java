package com.trulden.friends.activity;

import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;

import com.trulden.friends.AbstractTest;
import com.trulden.friends.DatabaseTestingHandler;
import com.trulden.friends.R;

import org.junit.Rule;
import org.junit.Test;

import static androidx.test.espresso.Espresso.*;
import static androidx.test.espresso.action.ViewActions.*;
import static androidx.test.espresso.assertion.ViewAssertions.*;
import static androidx.test.espresso.matcher.ViewMatchers.*;

@LargeTest
public class OpenActivitiesTest extends AbstractTest {

    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<>(MainActivity.class);

    @Test
    public void openEditFriendActivity(){
        openFriends();

        String friendsName = DatabaseTestingHandler.friends[0].getName();
        String friendsNotes = DatabaseTestingHandler.friends[0].getInfo();

        onView(withText(friendsName)).perform(longClick());

        editSelection();

        onView(withText(R.string.action_bar_title_edit_friend)).check(matches(isDisplayed()));

        onView(withText(friendsName)).check(matches(isDisplayed()));
        onView(withText(friendsNotes)).check(matches(isDisplayed()));
    }

    @Test
    public void openEditInteractionActivity(){
        openLog();

        String interactionComment = DatabaseTestingHandler.interactions[0].getComment();

        onView(withText(interactionComment)).perform(longClick());

        editSelection();

        onView(withText(R.string.edit_interaction)).check(matches(isDisplayed()));

        onView(withText(interactionComment)).check(matches(isDisplayed()));
    }
}
