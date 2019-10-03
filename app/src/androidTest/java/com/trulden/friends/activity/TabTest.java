package com.trulden.friends.activity;

import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import androidx.test.espresso.ViewInteraction;
import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;

import com.trulden.friends.AbstractTest;
import com.trulden.friends.R;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.swipeLeft;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.Matchers.allOf;

@LargeTest
public class TabTest  extends AbstractTest {

    @Before
    public void openTabsAtStart(){
        openLastInteractionFragment();
    }

    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<>(MainActivity.class);

    @Test
    public void tabPersistenceTest() {

        onView(first(withId(R.id.last_interaction_entry_layout))).check(matches(isDisplayed()));

        clickOnLITab(1);

        sleep(250);

        onView(first(withId(R.id.last_interaction_entry_layout))).check(matches(not(isDisplayed())));

        onView(withId(R.id.bottom_friends)).perform(click());

        openLastInteractionFragment();

        onView(first(withId(R.id.last_interaction_entry_layout))).check(matches(not(isDisplayed())));
    }

    @Test
    public void tabClickSwitchTest() {

        onView(first(withId(R.id.last_interaction_entry_layout))).check(matches(isDisplayed()));

        clickOnLITab(1);

        sleep(250);

        onView(first(withId(R.id.last_interaction_entry_layout))).check(matches(not(isDisplayed())));
    }

    @Test
    public void tabSwipeSwitchTest() {

        onView(first(withId(R.id.last_interaction_entry_layout))).check(matches(isDisplayed()));

        onView(withId(R.id.root_layout)).perform(swipeLeft());

        sleep(250);

        onView(first(withId(R.id.last_interaction_entry_layout))).check(matches(not(isDisplayed())));
    }

    private static Matcher<View> childAtPosition(
            final Matcher<View> parentMatcher, final int position) {

        return new TypeSafeMatcher<View>() {
            @Override
            public void describeTo(Description description) {
                description.appendText("Child at position " + position + " in parent ");
                parentMatcher.describeTo(description);
            }

            @Override
            public boolean matchesSafely(View view) {
                ViewParent parent = view.getParent();
                return parent instanceof ViewGroup && parentMatcher.matches(parent)
                        && view.equals(((ViewGroup) parent).getChildAt(position));
            }
        };
    }

    private void openLastInteractionFragment(){
        onView(withId(R.id.bottom_last_interactions)).perform(click());
        sleep(250);
    }

    private void clickOnLITab(int position) {
        onView(childAtPosition(
                    childAtPosition(
                            withId(R.id.last_interactions_tab_layout),
                            0),
                    position))
                .perform(click());
    }
}
