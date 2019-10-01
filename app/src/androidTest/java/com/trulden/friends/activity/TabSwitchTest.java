package com.trulden.friends.activity;


import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import androidx.fragment.app.FragmentActivity;
import androidx.test.espresso.ViewInteraction;
import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;

import com.trulden.friends.DatabaseTestingHandler;
import com.trulden.friends.R;
import com.trulden.friends.TestUtil;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.hamcrest.core.IsInstanceOf;
import org.junit.Rule;
import org.junit.Test;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;

@LargeTest
public class TabSwitchTest {

    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<>(MainActivity.class);

    @Test
    public void tabSwitchTest() {

        DatabaseTestingHandler.initAndFillDatabase(
                (FragmentActivity) TestUtil.getActivityInstance());

        ViewInteraction textView = onView(
                allOf(withId(R.id.last_interaction_name), withText("Caleb"),
                        childAtPosition(
                                allOf(withId(R.id.last_interaction_entry_layout),
                                        childAtPosition(
                                                IsInstanceOf.<View>instanceOf(android.widget.FrameLayout.class),
                                                0)),
                                0),
                        isDisplayed()));
        textView.check(matches(withText("Caleb")));

        ViewInteraction relativeLayout = onView(
                allOf(withId(R.id.last_interaction_entry_layout),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.tab_last_interaction_recyclerview),
                                        0),
                                0),
                        isDisplayed()));
        relativeLayout.check(matches(isDisplayed()));

        ViewInteraction tabView3 = onView(
                allOf(childAtPosition(
                        childAtPosition(
                                withId(R.id.last_interactions_tab_layout),
                                0),
                        1),
                        isDisplayed()));
        tabView3.perform(click());

        ViewInteraction relativeLayout2 = onView(
                allOf(withId(R.id.last_interaction_entry_layout),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.tab_last_interaction_recyclerview),
                                        0),
                                0),
                        isDisplayed()));
        relativeLayout2.check(doesNotExist());
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
}
