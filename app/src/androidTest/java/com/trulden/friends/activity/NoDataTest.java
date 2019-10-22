package com.trulden.friends.activity;

import com.trulden.friends.DatabaseTestingHandler;
import com.trulden.friends.R;
import com.trulden.friends.database.entity.Interaction;

import org.junit.Test;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.*;
import static androidx.test.espresso.assertion.ViewAssertions.*;
import static androidx.test.espresso.matcher.ViewMatchers.*;
import static org.hamcrest.Matchers.not;

public class NoDataTest extends AbstractMATest {
    @Test
    public void noFriendsTest(){
        openFriends();

        onView(withId(R.id.ff_no_data)).check(matches(not(isDisplayed())));

        onView(withText("Aaron")).perform(longClick());
        onView(withText("Balaam")).perform(click());
        onView(withText("Caleb")).perform(click());

        onView(withId(R.id.msed_delete)).perform(click());

        onView(withId(R.id.ff_no_data)).check(matches(isDisplayed()));

        openAddFriend();

        onView(withId(R.id.aef_edit_name)).perform(typeText("Dante"));

        onView(withId(R.id.ms_save)).perform(click());

        onView(withId(R.id.ff_no_data)).check(matches(not(isDisplayed())));
    }

    @Test
    public void noInteractionsTest(){
        openInteractions();

        onView(withId(R.id.fi_no_data)).check(matches(not(isDisplayed())));

        for(Interaction i : DatabaseTestingHandler.interactions){
            onView(withText(i.getComment())).perform(longClick());
        }

        onView(withId(R.id.msed_delete)).perform(click());

        onView(withId(R.id.fi_no_data)).check(matches(isDisplayed()));

        openAddInteraction();

        onView(withId(R.id.aei_edit_date)).perform(click());
        onView(withText("OK")).perform(click());

        onView(withId(R.id.aei_edit_friends)).perform(typeText("Aaron"));

        onView(withId(R.id.ms_save)).perform(click());

        onView(withId(R.id.fi_no_data)).check(matches(not(isDisplayed())));
    }
}
