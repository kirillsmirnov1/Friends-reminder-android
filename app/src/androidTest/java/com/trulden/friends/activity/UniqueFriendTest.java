package com.trulden.friends.activity;

import com.trulden.friends.AbstractTest;
import com.trulden.friends.DatabaseTestingHandler;
import com.trulden.friends.R;

import org.junit.Test;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.*;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.*;

public class UniqueFriendTest extends AbstractTest {

    @Test
    public void createNonUniqueFriendTest(){

        String aaron = DatabaseTestingHandler.friends[0].getName();

        openAddFriend();

        onView(withId(R.id.aef_edit_name)).perform(typeText(aaron));

        onView(withId(R.id.menu_save_save)).perform(click());

        // TODO check fab with error

        onView(withText(R.string.add_friend)).check(matches(isDisplayed()));

        navigateUp();
    }

    @Test
    public void renameFriendToExistingNameTest(){
        String aaron = DatabaseTestingHandler.friends[0].getName();
        String balaam = DatabaseTestingHandler.friends[1].getName();

        openFriends();

        onView(withText(balaam)).perform(click());

        onView(withId(R.id.menu_selection_edit)).perform(click());

        onView(withId(R.id.aef_edit_name)).perform(replaceText(aaron));

        onView(withId(R.id.menu_save_save)).perform(click());

        onView(withSubstring("Edit")).check(matches(isDisplayed()));

        navigateUp();
    }
}