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
import org.junit.Rule;
import org.junit.Test;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.longClick;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withClassName;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.is;

@LargeTest
public class DeselectionTest {

    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<>(MainActivity.class);

    @Test
    public void manualDeselectionLogTest() {
        DatabaseTestingHandler.initAndFillDatabase(
                (FragmentActivity) TestUtil.getActivityInstance());

        ViewInteraction bottomNavigationItemView = onView(
                allOf(withId(R.id.bottom_interactions), withContentDescription("Log"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.bottom_navigation),
                                        0),
                                0),
                        isDisplayed()));
        bottomNavigationItemView.perform(click());

        ViewInteraction constraintLayout = onView(
                allOf(withId(R.id.interaction_entry_layout),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.interactions_recyclerview),
                                        0),
                                0),
                        isDisplayed()));
        constraintLayout.perform(longClick());

        try {
            Thread.sleep(250);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ViewInteraction textView = onView(
                allOf(withText("1"),
                        isDisplayed()));
        textView.check(matches(withText("1")));

        ViewInteraction constraintLayout2 = onView(
                allOf(withId(R.id.interaction_entry_layout),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.interactions_recyclerview),
                                        0),
                                0),
                        isDisplayed()));
        constraintLayout2.perform(click());

        try {
            Thread.sleep(250);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        textView.check(doesNotExist());

        ViewInteraction constraintLayout3 = onView(
                allOf(withId(R.id.interaction_entry_layout),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.interactions_recyclerview),
                                        1),
                                0),
                        isDisplayed()));
        constraintLayout3.perform(longClick());

        try {
            Thread.sleep(250);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ViewInteraction textView2 = onView(
                allOf(withText("1"),
                        isDisplayed()));
        textView2.check(matches(withText("1")));
    }

    @Test
    public void manualDeselectionFriendsTest() {
        DatabaseTestingHandler.initAndFillDatabase(
                (FragmentActivity) TestUtil.getActivityInstance());

        ViewInteraction bottomNavigationItemView = onView(
                allOf(withId(R.id.bottom_friends), withContentDescription("Friends"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.bottom_navigation),
                                        0),
                                2),
                        isDisplayed()));
        bottomNavigationItemView.perform(click());

        ViewInteraction relativeLayout = onView(
                allOf(withId(R.id.friend_entry_layout),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.friends_recyclerView),
                                        0),
                                0),
                        isDisplayed()));
        relativeLayout.perform(longClick());

        try {
            Thread.sleep(250);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ViewInteraction textView = onView(
                allOf(withText("1"),
                        isDisplayed()));
        textView.check(matches(withText("1")));

        ViewInteraction relativeLayout2 = onView(
                allOf(withId(R.id.friend_entry_layout),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.friends_recyclerView),
                                        0),
                                0),
                        isDisplayed()));
        relativeLayout2.perform(click());

        try {
            Thread.sleep(250);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        textView.check(doesNotExist());

        ViewInteraction constraintLayout3 = onView(
                allOf(withId(R.id.friend_entry_layout),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.friends_recyclerView),
                                        1),
                                0),
                        isDisplayed()));
        constraintLayout3.perform(longClick());

        try {
            Thread.sleep(250);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ViewInteraction textView2 = onView(
                allOf(withText("1"),
                        isDisplayed()));
        textView2.check(matches(withText("1")));
    }

    @Test
    public void manualDeselectionTypesTest() {
        DatabaseTestingHandler.initAndFillDatabase(
                (FragmentActivity) TestUtil.getActivityInstance());

        ViewInteraction overflowMenuButton2 = onView(
                allOf(childAtPosition(
                        childAtPosition(
                                withId(R.id.toolbar),
                                1),
                        0),
                        isDisplayed()));
        overflowMenuButton2.perform(click());

        // Added a sleep statement to match the app's execution delay.
        // The recommended way to handle such scenarios is to use Espresso idling resources:
        // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
        try {
            Thread.sleep(250);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ViewInteraction appCompatTextView2 = onView(
                allOf(withId(R.id.title), withText("Interaction types"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.content),
                                        0),
                                0),
                        isDisplayed()));
        appCompatTextView2.perform(click());

        // Added a sleep statement to match the app's execution delay.
        // The recommended way to handle such scenarios is to use Espresso idling resources:
        // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
        try {
            Thread.sleep(700);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ViewInteraction relativeLayout = onView(
                allOf(withId(R.id.interaction_type_entry_layout),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.interaction_type_recyclerview),
                                        0),
                                0),
                        isDisplayed()));
        relativeLayout.perform(longClick());

        try {
            Thread.sleep(250);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ViewInteraction textView = onView(
                allOf(withText("1"),
                        isDisplayed()));
        textView.check(matches(withText("1")));

        ViewInteraction relativeLayout2 = onView(
                allOf(withId(R.id.interaction_type_entry_layout),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.interaction_type_recyclerview),
                                        0),
                                0),
                        isDisplayed()));
        relativeLayout2.perform(click());

        try {
            Thread.sleep(250);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        textView.check(doesNotExist());

        ViewInteraction constraintLayout3 = onView(
                allOf(withId(R.id.interaction_type_entry_layout),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.interaction_type_recyclerview),
                                        1),
                                0),
                        isDisplayed()));
        constraintLayout3.perform(longClick());

        try {
            Thread.sleep(250);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ViewInteraction textView2 = onView(
                allOf(withText("1"),
                        isDisplayed()));
        textView2.check(matches(withText("1")));
    }

    @Test
    public void logAddInteractionDeselectionTest() {
        DatabaseTestingHandler.initAndFillDatabase(
                (FragmentActivity) TestUtil.getActivityInstance());

        ViewInteraction bottomNavigationItemView = onView(
                allOf(withId(R.id.bottom_interactions), withContentDescription("Log"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.bottom_navigation),
                                        0),
                                0),
                        isDisplayed()));
        bottomNavigationItemView.perform(click());

        ViewInteraction constraintLayout = onView(
                allOf(withId(R.id.interaction_entry_layout),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.interactions_recyclerview),
                                        0),
                                0),
                        isDisplayed()));
        constraintLayout.perform(longClick());

        ViewInteraction textView = onView(
                allOf(withText("1"),
                        isDisplayed()));
        textView.check(matches(withText("1")));

        ViewInteraction viewInteraction = onView(
                allOf(withId(R.id.fab_expand_menu_button),
                        childAtPosition(
                                allOf(withId(R.id.fab_main_activity),
                                        childAtPosition(
                                                withClassName(is("android.widget.RelativeLayout")),
                                                1)),
                                2),
                        isDisplayed()));
        viewInteraction.perform(click());

        ViewInteraction floatingActionButton = onView(
                allOf(withId(R.id.fab_add_interaction),
                        childAtPosition(
                                allOf(withId(R.id.fab_main_activity),
                                        childAtPosition(
                                                withClassName(is("android.widget.RelativeLayout")),
                                                1)),
                                0),
                        isDisplayed()));
        floatingActionButton.perform(click());

        // Added a sleep statement to match the app's execution delay.
        // The recommended way to handle such scenarios is to use Espresso idling resources:
        // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
        try {
            Thread.sleep(700);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ViewInteraction appCompatImageButton = onView(
                allOf(withContentDescription("Navigate up"),
                        childAtPosition(
                                allOf(withId(R.id.action_bar),
                                        childAtPosition(
                                                withId(R.id.action_bar_container),
                                                0)),
                                1),
                        isDisplayed()));
        appCompatImageButton.perform(click());

        // Added a sleep statement to match the app's execution delay.
        // The recommended way to handle such scenarios is to use Espresso idling resources:
        // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
        try {
            Thread.sleep(700);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        textView.check(doesNotExist());

        ViewInteraction constraintLayout2 = onView(
                allOf(withId(R.id.interaction_entry_layout),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.interactions_recyclerview),
                                        1),
                                0),
                        isDisplayed()));
        constraintLayout2.perform(longClick());

        ViewInteraction textView2 = onView(
                allOf(withText("1"),
                        isDisplayed()));
        textView2.check(matches(withText("1")));
    }

    @Test
    public void logAddFriendDeselectionTest() {
        DatabaseTestingHandler.initAndFillDatabase(
                (FragmentActivity) TestUtil.getActivityInstance());

        ViewInteraction bottomNavigationItemView = onView(
                allOf(withId(R.id.bottom_interactions), withContentDescription("Log"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.bottom_navigation),
                                        0),
                                0),
                        isDisplayed()));
        bottomNavigationItemView.perform(click());

        ViewInteraction constraintLayout = onView(
                allOf(withId(R.id.interaction_entry_layout),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.interactions_recyclerview),
                                        0),
                                0),
                        isDisplayed()));
        constraintLayout.perform(longClick());

        ViewInteraction textView = onView(
                allOf(withText("1"),
                        isDisplayed()));
        textView.check(matches(withText("1")));

        ViewInteraction viewInteraction = onView(
                allOf(withId(R.id.fab_expand_menu_button),
                        childAtPosition(
                                allOf(withId(R.id.fab_main_activity),
                                        childAtPosition(
                                                withClassName(is("android.widget.RelativeLayout")),
                                                1)),
                                2),
                        isDisplayed()));
        viewInteraction.perform(click());

        ViewInteraction floatingActionButton = onView(
                allOf(withId(R.id.fab_add_friend),
                        isDisplayed()));
        floatingActionButton.perform(click());

        // Added a sleep statement to match the app's execution delay.
        // The recommended way to handle such scenarios is to use Espresso idling resources:
        // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
        try {
            Thread.sleep(700);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ViewInteraction appCompatImageButton = onView(
                allOf(withContentDescription("Navigate up"),
                        childAtPosition(
                                allOf(withId(R.id.action_bar),
                                        childAtPosition(
                                                withId(R.id.action_bar_container),
                                                0)),
                                1),
                        isDisplayed()));
        appCompatImageButton.perform(click());

        // Added a sleep statement to match the app's execution delay.
        // The recommended way to handle such scenarios is to use Espresso idling resources:
        // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
        try {
            Thread.sleep(700);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        textView.check(doesNotExist());

        ViewInteraction constraintLayout2 = onView(
                allOf(withId(R.id.interaction_entry_layout),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.interactions_recyclerview),
                                        1),
                                0),
                        isDisplayed()));
        constraintLayout2.perform(longClick());

        ViewInteraction textView2 = onView(
                allOf(withText("1"),
                        isDisplayed()));
        textView2.check(matches(withText("1")));
    }

    @Test
    public void friendsAddInteractionDeselectionTest() {
        DatabaseTestingHandler.initAndFillDatabase(
                (FragmentActivity) TestUtil.getActivityInstance());

        ViewInteraction bottomNavigationItemView = onView(
                allOf(withId(R.id.bottom_friends),
                        isDisplayed()));
        bottomNavigationItemView.perform(click());

        ViewInteraction constraintLayout = onView(
                allOf(withId(R.id.friend_entry_layout),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.friends_recyclerView),
                                        0),
                                0),
                        isDisplayed()));
        constraintLayout.perform(longClick());

        ViewInteraction textView = onView(
                allOf(withText("1"),
                        isDisplayed()));
        textView.check(matches(withText("1")));

        ViewInteraction viewInteraction = onView(
                allOf(withId(R.id.fab_expand_menu_button),
                        childAtPosition(
                                allOf(withId(R.id.fab_main_activity),
                                        childAtPosition(
                                                withClassName(is("android.widget.RelativeLayout")),
                                                1)),
                                2),
                        isDisplayed()));
        viewInteraction.perform(click());

        ViewInteraction floatingActionButton = onView(
                allOf(withId(R.id.fab_add_interaction),
                        childAtPosition(
                                allOf(withId(R.id.fab_main_activity),
                                        childAtPosition(
                                                withClassName(is("android.widget.RelativeLayout")),
                                                1)),
                                0),
                        isDisplayed()));
        floatingActionButton.perform(click());

        // Added a sleep statement to match the app's execution delay.
        // The recommended way to handle such scenarios is to use Espresso idling resources:
        // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
        try {
            Thread.sleep(700);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ViewInteraction appCompatImageButton = onView(
                allOf(withContentDescription("Navigate up"),
                        childAtPosition(
                                allOf(withId(R.id.action_bar),
                                        childAtPosition(
                                                withId(R.id.action_bar_container),
                                                0)),
                                1),
                        isDisplayed()));
        appCompatImageButton.perform(click());

        // Added a sleep statement to match the app's execution delay.
        // The recommended way to handle such scenarios is to use Espresso idling resources:
        // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
        try {
            Thread.sleep(700);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        textView.check(doesNotExist());

        ViewInteraction constraintLayout2 = onView(
                allOf(withId(R.id.friend_entry_layout),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.friends_recyclerView),
                                        1),
                                0),
                        isDisplayed()));
        constraintLayout2.perform(longClick());

        ViewInteraction textView2 = onView(
                allOf(withText("1"),
                        isDisplayed()));
        textView2.check(matches(withText("1")));
    }

    @Test
    public void friendsAddFriendDeselectionTest() {
        DatabaseTestingHandler.initAndFillDatabase(
                (FragmentActivity) TestUtil.getActivityInstance());

        ViewInteraction bottomNavigationItemView = onView(
                allOf(withId(R.id.bottom_friends),
                        isDisplayed()));
        bottomNavigationItemView.perform(click());

        ViewInteraction constraintLayout = onView(
                allOf(withId(R.id.friend_entry_layout),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.friends_recyclerView),
                                        0),
                                0),
                        isDisplayed()));
        constraintLayout.perform(longClick());

        ViewInteraction textView = onView(
                allOf(withText("1"),
                        isDisplayed()));
        textView.check(matches(withText("1")));

        ViewInteraction viewInteraction = onView(
                allOf(withId(R.id.fab_expand_menu_button),
                        childAtPosition(
                                allOf(withId(R.id.fab_main_activity),
                                        childAtPosition(
                                                withClassName(is("android.widget.RelativeLayout")),
                                                1)),
                                2),
                        isDisplayed()));
        viewInteraction.perform(click());

        ViewInteraction floatingActionButton = onView(
                allOf(withId(R.id.fab_add_friend),
                        isDisplayed()));
        floatingActionButton.perform(click());

        // Added a sleep statement to match the app's execution delay.
        // The recommended way to handle such scenarios is to use Espresso idling resources:
        // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
        try {
            Thread.sleep(700);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ViewInteraction appCompatImageButton = onView(
                allOf(withContentDescription("Navigate up"),
                        childAtPosition(
                                allOf(withId(R.id.action_bar),
                                        childAtPosition(
                                                withId(R.id.action_bar_container),
                                                0)),
                                1),
                        isDisplayed()));
        appCompatImageButton.perform(click());

        // Added a sleep statement to match the app's execution delay.
        // The recommended way to handle such scenarios is to use Espresso idling resources:
        // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
        try {
            Thread.sleep(700);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        textView.check(doesNotExist());

        ViewInteraction constraintLayout2 = onView(
                allOf(withId(R.id.friend_entry_layout),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.friends_recyclerView),
                                        1),
                                0),
                        isDisplayed()));
        constraintLayout2.perform(longClick());

        ViewInteraction textView2 = onView(
                allOf(withText("1"),
                        isDisplayed()));
        textView2.check(matches(withText("1")));
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
