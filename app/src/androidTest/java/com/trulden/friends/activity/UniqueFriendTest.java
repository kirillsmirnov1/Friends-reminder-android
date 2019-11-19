package com.trulden.friends.activity;

import com.trulden.friends.DatabaseTestingHandler;
import com.trulden.friends.R;

import org.junit.Test;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.*;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.*;

public class UniqueFriendTest extends AbstractMATest {

    @Test
    public void createNonUniqueFriendTest(){

        String aaron = DatabaseTestingHandler.friends[0].getName();

        openAddFriend();

        onView(withId(R.id.aef_edit_name)).perform(typeText(aaron));

        onView(withId(R.id.ms_save)).perform(click());

        onView(withText(R.string.add_friend)).check(matches(isDisplayed()));

        navigateUp();
    }

    @Test
    public void renameFriendToExistingNameTest(){
        String aaron = DatabaseTestingHandler.friends[0].getName();
        String balaam = DatabaseTestingHandler.friends[1].getName();

        openFriends();

        onView(withText(balaam)).perform(click());

        onView(withId(R.id.mam_edit)).perform(click());

        onView(withId(R.id.aef_edit_name)).perform(replaceText(aaron));

        onView(withId(R.id.ms_save)).perform(click());

        onView(withText(aaron)).check(matches(isDisplayed()));

        navigateUp();
    }
}
