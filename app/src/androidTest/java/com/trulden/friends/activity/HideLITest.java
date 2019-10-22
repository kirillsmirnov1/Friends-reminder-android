package com.trulden.friends.activity;

import android.view.View;
import android.widget.CheckBox;

import androidx.test.espresso.Espresso;
import androidx.test.espresso.ViewInteraction;

import com.trulden.friends.R;

import junit.framework.AssertionFailedError;

import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;

import java.util.Calendar;

import static androidx.test.espresso.Espresso.*;
import static androidx.test.espresso.action.ViewActions.*;
import static androidx.test.espresso.assertion.ViewAssertions.*;
import static androidx.test.espresso.matcher.ViewMatchers.*;
import static org.hamcrest.Matchers.*;

public class HideLITest extends AbstractMATest {

    @Before
    public void before(){
        openLastInteractions();
    }

    @Test
    public void hideEntriesTest(){
        openOverflow();

        sleep(250);

        // Need to be sure it's checked off
        guaranteeCheckShowHiddenLI(false);

        onView(withText("Meeting")).check(matches(hasSibling(withText("1"))));

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

        sleep(250);

        guaranteeCheckShowHiddenLI(true);

        sleep(250);

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

        onView(withId(R.id.ms_save)).perform(click());

        sleep(250);

        onView(withText("Caleb")).check(matches(hasSibling(allOf(withId(R.id.eli_hidden_icon), isDisplayed()))));

        openInteractions();

        onView(withText("Caleb")).perform(longClick());

        onView(withId(R.id.msed_edit)).perform(click());

        onView(withId(R.id.aei_edit_date)).perform(click());

        sleep(250);

        Calendar yesterday = Calendar.getInstance();
        yesterday.add(Calendar.DAY_OF_MONTH, -1);

        setDatePicker(yesterday);

        onView(withText("OK")).perform(click());

        onView(withId(R.id.aei_edit_friends)).perform(typeText(", Aaron"));

        onView(withId(R.id.ms_save)).perform(click());

        openLastInteractions();

        onView(withText("Caleb")).check(matches(hasSibling(allOf(withId(R.id.eli_hidden_icon), isDisplayed()))));
        onView(withText("Aaron")).check(matches(hasSibling(allOf(withId(R.id.eli_hidden_icon), isDisplayed()))));

        openInteractions();

        onView(withText("Aaron, Caleb")).perform(longClick());
        onView(withId(R.id.msed_delete)).perform(click());

        openLastInteractions();

        onView(withText("Caleb")).check(matches(hasSibling(allOf(withId(R.id.eli_hidden_icon), isDisplayed()))));
        onView(withText("Aaron")).check(matches(hasSibling(allOf(withId(R.id.eli_hidden_icon), isDisplayed()))));
    }

    @Test
    public void openOtherActivitySustainCheckTest(){
        openOverflow();
        guaranteeCheckShowHiddenLI(false);

        onView(withText("Caleb")).perform(longClick());

        onView(withId(R.id.msli_hide)).perform(click());

        sleep(250);

        onView(withText("Caleb")).check(doesNotExist());

        openTypes();

        navigateUp();

        onView(withText("Caleb")).check(doesNotExist());
    }

    private void guaranteeCheckShowHiddenLI(boolean checked){
        try {
            // The checkbox of menu item is hidden pretty deep
            ViewInteraction v = onView(allOf(Matchers.<View>instanceOf(CheckBox.class), hasSibling(withChild(withText(R.string.show_hidden_li_entries)))));

            if(checked) {
                v.check(matches(isChecked()));
            }
            else {
                v.check(matches(not(isChecked())));
            }

            Espresso.pressBack();

        } catch (AssertionFailedError e){
            onView(withText(R.string.show_hidden_li_entries)).perform(click());
        }
    }
}
