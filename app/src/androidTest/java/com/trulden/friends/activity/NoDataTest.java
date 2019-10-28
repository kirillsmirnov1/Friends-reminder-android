package com.trulden.friends.activity;

import androidx.test.espresso.ViewInteraction;

import com.trulden.friends.DatabaseTestingHandler;
import com.trulden.friends.R;
import com.trulden.friends.database.entity.Friend;
import com.trulden.friends.database.entity.Interaction;

import org.junit.Test;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.*;
import static androidx.test.espresso.assertion.ViewAssertions.*;
import static androidx.test.espresso.matcher.ViewMatchers.*;
import static org.hamcrest.Matchers.allOf;
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

    @Test
    public void noTypesTest(){
        openLastInteractions();

        onView(withId(R.id.fli_no_data)).check(matches(not(isDisplayed())));

        openTypes();

        onView(withId(R.id.ait_no_data)).check(matches(not(isDisplayed())));

        selectAllTypes();

        onView(withId(R.id.msed_delete)).perform(click());

        onView(withId(R.id.ait_no_data)).check(matches(isDisplayed()));

        navigateUp();

        onView(withId(R.id.fli_no_data)).check(matches(isDisplayed()));

        openTypes();

        onView(withId(R.id.ma_add)).perform(click());

        onView(withId(R.id.deit_name)).perform(typeText("A"));
        onView(withId(R.id.deit_frequency)).perform(typeText("1"));
        onView(withText("SAVE")).perform(click());

        onView(withId(R.id.ait_no_data)).check(matches(not(isDisplayed())));

        navigateUp();

        onView(withId(R.id.fli_no_data)).check(matches(not(isDisplayed())));
    }

    @Test
    public void noLastInteractionsTest(){
        openLastInteractions();

        openOverflow();

        guaranteeCheckShowHiddenLI(false);

        onView(withText("Meeting")).perform(click());

        for(Friend f : DatabaseTestingHandler.friends){
            onView(withText(f.getName())).perform(longClick());
        }

        onView(withId(R.id.msli_hide)).perform(click());

        sleep(250);

        ViewInteraction noData = onView(allOf(withId(R.id.flit_no_data), isDisplayed()));
        noData.check(matches(isDisplayed()));

        openOverflow();
        guaranteeCheckShowHiddenLI(true);

        sleep(250);

        noData.check(doesNotExist());
    }
}
