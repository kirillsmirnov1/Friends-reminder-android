package com.trulden.friends.activity;

import com.trulden.friends.AbstractTest;
import com.trulden.friends.DatabaseTestingHandler;
import com.trulden.friends.R;
import com.trulden.friends.database.entity.Friend;

import org.junit.Test;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.*;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.*;

public class CRUDTest extends AbstractTest {

    @Test
    public void createFriendTest(){

        Friend oldFriend = DatabaseTestingHandler.friends[0];
        Friend newFriend = new Friend("David", "A young shepherd");

        openAddFriend();

        onView(withText(R.string.add_friend)).check(matches(isDisplayed()));

        onView(withId(R.id.edit_friends_name)).check(matches(withText("")));
        onView(withId(R.id.edit_friends_info)).check(matches(withText("")));

        onView(withId(R.id.edit_friends_name)).perform(typeText(oldFriend.getName()));
        clickSaveOnActionBar();

        checkIfToastAppeared(R.string.toast_warning_friend_exists);

        onView(withId(R.id.edit_friends_name)).perform(clearText(), typeText(newFriend.getName()));
        onView(withId(R.id.edit_friends_info)).perform(typeText(newFriend.getInfo()));
        clickSaveOnActionBar();

        //TODO checkIfToastAppeared("«David» is created");

        openFriends();

        onView(withText(newFriend.getName())).perform(click());

        onView(withText(newFriend.getName())).check(matches(isDisplayed()));
        onView(withText(newFriend.getInfo())).check(matches(isDisplayed()));
    }
}
