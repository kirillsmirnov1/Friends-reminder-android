package com.trulden.friends.activity;

import androidx.test.filters.LargeTest;

import com.trulden.friends.R;

import org.junit.Before;
import org.junit.Test;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.swipeLeft;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.not;

@LargeTest
public class TabTest  extends AbstractMATest {

    @Before
    public void openTabsAtStart(){
        openLastInteractions();
    }

    @Test
    public void tabPersistenceTest() {

        onView(first(withId(R.id.eli_layout))).check(matches(isDisplayed()));

        clickOnLITab(1);

        sleep(250);

        onView(first(withId(R.id.eli_layout))).check(matches(not(isDisplayed())));

        onView(withId(R.id.mbn_friends)).perform(click());

        openLastInteractions();

        onView(first(withId(R.id.eli_layout))).check(matches(not(isDisplayed())));
    }

    @Test
    public void tabClickSwitchTest() {

        onView(withText("Meeting")).perform(click());

        onView(first(withId(R.id.eli_layout))).check(matches(isDisplayed()));

        clickOnLITab(1);

        sleep(250);

        onView(first(withId(R.id.eli_layout))).check(matches(not(isDisplayed())));
    }

    @Test
    public void tabSwipeSwitchTest() {

        onView(first(withId(R.id.eli_layout))).check(matches(isDisplayed()));

        onView(withId(R.id.am_root_layout)).perform(swipeLeft());

        sleep(250);

        onView(first(withId(R.id.eli_layout))).check(matches(not(isDisplayed())));
    }

    private void clickOnLITab(int position) {
        onView(childAtPosition(
                    childAtPosition(
                            withId(R.id.fli_tab_layout),
                            0),
                    position))
                .perform(click());
    }
}
