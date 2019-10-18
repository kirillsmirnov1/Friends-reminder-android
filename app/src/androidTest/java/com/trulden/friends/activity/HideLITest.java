package com.trulden.friends.activity;

import com.trulden.friends.AbstractTest;
import com.trulden.friends.R;

import junit.framework.AssertionFailedError;

import org.junit.Before;
import org.junit.Test;

import java.util.Calendar;

import static androidx.test.espresso.Espresso.*;
import static androidx.test.espresso.action.ViewActions.*;
import static androidx.test.espresso.assertion.ViewAssertions.*;
import static androidx.test.espresso.matcher.ViewMatchers.*;
import static org.hamcrest.Matchers.*;

public class HideLITest extends AbstractTest {

    @Before
    public void before(){
        openLastInteractions();
    }

    @Test
    public void hideEntriesTest(){
        openOverflow();

        // Need to be sure it's checked off
        guaranteeCheckOffShowHiddenLI();

        onView(withText("Caleb")).check(matches(allOf(isDisplayed(),
                hasSibling(allOf(withId(R.id.eli_hidden_icon), not(isDisplayed()))))));


        onView(withText("Caleb")).perform(longClick());

        onView(withId(R.id.msli_hide)).perform(click());

        sleep(250);

        onView(withText("Caleb")).check(doesNotExist());

        onView(withText("Meeting")).check(matches(hasSibling(withText("0"))));

        openOverflow();

        onView(withText(R.string.show_hidden_li_entries)).perform(click());

        sleep(250);

        onView(withText("Caleb")).check(matches(allOf(isDisplayed(),
                hasSibling(allOf(withId(R.id.eli_hidden_icon), isDisplayed())))));
    }

    @Test
    public void hiddenPersistenceTest(){

        openOverflow();

        guaranteeCheckOnShowHiddenLI();

        onView(withText("Caleb")).check(matches(allOf(isDisplayed(),
            hasSibling(allOf(withId(R.id.eli_hidden_icon), not(isDisplayed()))))));

        onView(withText("Caleb")).perform(longClick());
        onView(withText("Aaron")).perform(click());

        onView(withId(R.id.msli_hide)).perform(click());

        sleep(250);

        openAddInteraction();

        onView(withId(R.id.aei_edit_date)).perform(click());
        onView(withText("OK")).perform(click());

        onView(withId(R.id.aei_edit_friends)).perform(typeText("Caleb"));

        // TODO rename menus to match notation
        onView(withId(R.id.menu_save_save)).perform(click());

        sleep(250);

        onView(withText("Caleb")).check(matches(hasSibling(allOf(withId(R.id.eli_hidden_icon), isDisplayed()))));

        openLog();

        onView(withText("Caleb")).perform(longClick());

        onView(withId(R.id.msed_edit)).perform(click());

        onView(withId(R.id.aei_edit_date)).perform(click());

        sleep(250);

        Calendar yesterday = Calendar.getInstance();
        yesterday.add(Calendar.DAY_OF_MONTH, -1);

        setDatePicker(yesterday);

        onView(withText("OK")).perform(click());

        onView(withId(R.id.aei_edit_friends)).perform(typeText(", Aaron"));

        onView(withId(R.id.menu_save_save)).perform(click());

        openLastInteractions();

        onView(withText("Caleb")).check(matches(hasSibling(allOf(withId(R.id.eli_hidden_icon), isDisplayed()))));
        onView(withText("Aaron")).check(matches(hasSibling(allOf(withId(R.id.eli_hidden_icon), isDisplayed()))));

        openLog();

        onView(withText("Aaron, Caleb")).perform(longClick());
        onView(withId(R.id.msed_delete)).perform(click());

        openLastInteractions();

        onView(withText("Caleb")).check(matches(hasSibling(allOf(withId(R.id.eli_hidden_icon), isDisplayed()))));
        onView(withText("Aaron")).check(matches(hasSibling(allOf(withId(R.id.eli_hidden_icon), isDisplayed()))));
    }

    // TODO combine into one function
    private void guaranteeCheckOffShowHiddenLI(){
        try {
            onView(withText(R.string.show_hidden_li_entries)).check(matches(not(isChecked())));
            openLastInteractions();

        } catch (AssertionFailedError e){
            onView(withText(R.string.show_hidden_li_entries)).perform(click());
        }
    }

    private void guaranteeCheckOnShowHiddenLI(){
        try {
            onView(withText(R.string.show_hidden_li_entries)).check(matches(isChecked()));
            openLastInteractions();

        } catch (AssertionFailedError e){
            onView(withText(R.string.show_hidden_li_entries)).perform(click());
        }
    }

}
