package com.trulden.friends;

import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import androidx.fragment.app.FragmentActivity;
import androidx.test.espresso.ViewInteraction;
import androidx.test.rule.ActivityTestRule;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Before;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.*;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.RootMatchers.withDecorView;
import static androidx.test.espresso.matcher.ViewMatchers.*;
import static org.hamcrest.Matchers.*;

public abstract class AbstractTest {

    @Before
    public void initDB(){
        DatabaseTestingHandler.initAndFillDatabase(
                (FragmentActivity) TestUtil.getActivityInstance());
    }

    protected void openLog() {
        onView(withId(R.id.bottom_interactions)).perform(click());
    }

    protected void openLastInteractions(){
        onView(withId(R.id.bottom_last_interactions)).perform(click());
    }

    protected void openFriends() {
        onView(withId(R.id.bottom_friends)).perform(click());
    }

    protected void openTypes() {
        onView(childAtPosition(
                childAtPosition(withId(R.id.toolbar),1),
                0))
                .perform(click());

        sleep(250);

        onView(withText(R.string.manage_interaction_types)).perform(click());

        sleep(250);
    }

    protected void openFab() {
        onView(withId(R.id.fab_expand_menu_button)).perform(click());
    }

    protected void fabClickAddInteraction() {
        onView(withId(R.id.fab_add_interaction)).perform(click());
    }

    protected void fabClickAddFriend() {
        onView(withId(R.id.fab_add_friend)).perform(click());
    }

    protected void openAddInteraction(){
        openFab();
        fabClickAddInteraction();
    }

    protected void openAddFriend(){
        openFab();
        fabClickAddFriend();
    }

    protected void selectEntry(int pos, int entryID, int recyclerviewID, boolean longClick) {
        ViewInteraction constraintLayout = onView(
                allOf(withId(entryID),
                        childAtPosition(
                                childAtPosition(
                                        withId(recyclerviewID),
                                        pos),
                                0),
                        isDisplayed()));
        constraintLayout.perform(longClick ? longClick() : click());
    }

    protected void editSelection() {
        onView(withId(R.id.edit_selection)).perform(click());
    }

    protected void clickSaveOnActionBar() {
        onView(withId(R.id.icon_save)).perform(click());
    }

    protected void sleep(int millis){
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    protected void checkIfToastAppeared(ActivityTestRule activityTestRule, int toastStringId) {
        onView(withText(toastStringId))
                .inRoot(withDecorView(not(activityTestRule.getActivity().getWindow().getDecorView())))
                .check(matches(isDisplayed()));
    }

    protected static Matcher<View> childAtPosition(
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

    protected  <T> Matcher<T> first(final Matcher<T> matcher) {
        return new BaseMatcher<T>() {
            boolean isFirst = true;

            @Override
            public boolean matches(final Object item) {
                if (isFirst && matcher.matches(item)) {
                    isFirst = false;
                    return true;
                }

                return false;
            }

            @Override
            public void describeTo(final Description description) {
                description.appendText("should return first matching item");
            }
        };
    }
}
