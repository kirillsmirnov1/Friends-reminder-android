package com.trulden.friends.activity;

import com.trulden.friends.AbstractTest;
import com.trulden.friends.R;

import org.junit.Test;

import java.util.Calendar;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.*;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.*;

public class LastInteractionTest extends AbstractTest {

    @Test
    public void newInteractionTest(){

        onView(withText("Caleb")).check(matches(hasSibling(withText("30 d. ago"))));
        onView(withText("Aaron")).check(matches(hasSibling(withText("29 d. ago"))));
        onView(withText("Balaam")).check(matches(hasSibling(withText("29 d. ago"))));

        openAddInteraction();

        onView(withId(R.id.editDate)).perform(click());

        onView(withText("OK")).perform(click());

        onView(withId(R.id.editFriends)).perform(typeText("Aaron, Caleb"));

        onView(withId(R.id.icon_save)).perform(click());

        onView(withText("Balaam")).check(matches(hasSibling(withText("29 d. ago"))));
        onView(withText("Aaron")).check(matches(hasSibling(withText("0 d. ago"))));
        onView(withText("Caleb")).check(matches(hasSibling(withText("0 d. ago"))));

        openAddInteraction();

        onView(withId(R.id.editDate)).perform(click());

        Calendar yesterday = Calendar.getInstance();
        yesterday.add(Calendar.DATE, -1);

        setDatePicker(yesterday);

        onView(withText("OK")).perform(click());

        onView(withId(R.id.editFriends)).perform(typeText("Aaron, Balaam"));

        onView(withId(R.id.icon_save)).perform(click());

        onView(withText("Balaam")).check(matches(hasSibling(withText("1 d. ago"))));
        onView(withText("Aaron")).check(matches(hasSibling(withText("0 d. ago"))));
        onView(withText("Caleb")).check(matches(hasSibling(withText("0 d. ago"))));
    }
}
