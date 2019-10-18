package com.trulden.friends.activity;

import com.trulden.friends.AbstractTest;
import com.trulden.friends.R;

import junit.framework.AssertionFailedError;

import org.junit.Before;
import org.junit.Test;

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

        onView(withText("Meeting")).check(matches(hasSibling(withText("1"))));

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

    private void guaranteeCheckOffShowHiddenLI(){
        try {
            onView(withText(R.string.show_hidden_li_entries)).check(matches(not(isChecked())));
            openLastInteractions();

        } catch (Exception e){
            onView(withText(R.string.show_hidden_li_entries)).perform(click());
        }
    }

}
