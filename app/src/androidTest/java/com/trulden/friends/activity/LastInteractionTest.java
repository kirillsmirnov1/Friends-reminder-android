package com.trulden.friends.activity;

import com.trulden.friends.R;

import org.junit.Before;
import org.junit.Test;

import java.util.Calendar;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.*;
import static androidx.test.espresso.assertion.ViewAssertions.*;
import static androidx.test.espresso.matcher.ViewMatchers.*;

public class LastInteractionTest extends AbstractMATest {

    @Before
    public void openLITab(){
        openLastInteractions();
    }

    @Test
    public void newInteractionTest(){

        onView(withText("Caleb")).check(matches(hasSibling(withText("30 d. ago"))));
        onView(withText("Aaron")).check(matches(hasSibling(withText("29 d. ago"))));
        onView(withText("Balaam")).check(matches(hasSibling(withText("29 d. ago"))));

        openAddInteraction();

        onView(withId(R.id.aei_edit_date)).perform(click());

        onView(withText("OK")).perform(click());

        onView(withId(R.id.aei_edit_friends)).perform(typeText("Aaron, Caleb"));

        onView(withId(R.id.ms_save)).perform(click());

        onView(withText("Balaam")).check(matches(hasSibling(withText("29 d. ago"))));
        onView(withText("Aaron")).check(matches(hasSibling(withText("0 d. ago"))));
        onView(withText("Caleb")).check(matches(hasSibling(withText("0 d. ago"))));

        openAddInteraction();

        onView(withId(R.id.aei_edit_date)).perform(click());

        Calendar yesterday = Calendar.getInstance();
        yesterday.add(Calendar.DATE, -1);

        setDatePicker(yesterday);

        onView(withText("OK")).perform(click());

        onView(withId(R.id.aei_edit_friends)).perform(typeText("Aaron, Balaam"));

        onView(withId(R.id.ms_save)).perform(click());

        onView(withText("Balaam")).check(matches(hasSibling(withText("1 d. ago"))));
        onView(withText("Aaron")).check(matches(hasSibling(withText("0 d. ago"))));
        onView(withText("Caleb")).check(matches(hasSibling(withText("0 d. ago"))));
    }

    @Test
    public void deleteInteractionTest(){

        onView(withText("Caleb")).check(matches(hasSibling(withText("30 d. ago"))));
        onView(withText("Aaron")).check(matches(hasSibling(withText("29 d. ago"))));
        onView(withText("Balaam")).check(matches(hasSibling(withText("29 d. ago"))));

        openInteractions();

        onView(withText("A + B")).perform(longClick());
        onView(withId(R.id.ms_delete)).perform(click());

        openLastInteractions();

        onView(withText("Aaron")).check(matches(hasSibling(withText("31 d. ago"))));
        onView(withText("Caleb")).check(matches(hasSibling(withText("30 d. ago"))));
        onView(withText("Balaam")).check(matches(hasSibling(withText("30 d. ago"))));

        openInteractions();

        onView(withText("B + C")).perform(longClick());
        onView(withId(R.id.ms_delete)).perform(click());

        openLastInteractions();

        onView(withText("Aaron")).check(matches(hasSibling(withText("31 d. ago"))));
        onView(withText("Caleb")).check(matches(hasSibling(withText("31 d. ago"))));
        onView(withText("Balaam")).check(doesNotExist());
    }

    // Update is handled by CRUDTest.updateInteractionTest()
}
