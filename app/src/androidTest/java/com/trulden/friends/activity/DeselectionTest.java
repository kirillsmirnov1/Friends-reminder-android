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
import org.junit.Before;
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

    @Before
    public void initDB(){
        DatabaseTestingHandler.initAndFillDatabase(
                (FragmentActivity) TestUtil.getActivityInstance());
    }

    @Test
    public void manualDeselectionLogTest() {

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

    @Test
    public void logDeleteDeselectionTest() {

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

        ViewInteraction constraintLayout2 = onView(
                allOf(withId(R.id.interaction_entry_layout),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.interactions_recyclerview),
                                        1),
                                0),
                        isDisplayed()));
        constraintLayout2.perform(click());

        ViewInteraction textView = onView(
                allOf(withText("2"),
                        isDisplayed()));
        textView.check(matches(withText("2")));

        ViewInteraction actionMenuItemView = onView(
                allOf(withId(R.id.delete_selection), withContentDescription("Delete selection"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.action_mode_bar),
                                        1),
                                0),
                        isDisplayed()));
        actionMenuItemView.perform(click());

        ViewInteraction constraintLayout3 = onView(
                allOf(withId(R.id.interaction_entry_layout),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.interactions_recyclerview),
                                        0),
                                0),
                        isDisplayed()));
        constraintLayout3.perform(longClick());

        ViewInteraction textView2 = onView(
                allOf(withText("1"),
                        isDisplayed()));
        textView2.check(matches(withText("1")));
    }

    @Test
    public void friendsDeleteDeselectionTest() {

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

        ViewInteraction constraintLayout2 = onView(
                allOf(withId(R.id.friend_entry_layout),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.friends_recyclerView),
                                        1),
                                0),
                        isDisplayed()));
        constraintLayout2.perform(click());

        ViewInteraction textView = onView(
                allOf(withText("2"),
                        isDisplayed()));
        textView.check(matches(withText("2")));

        ViewInteraction actionMenuItemView = onView(
                allOf(withId(R.id.delete_selection), withContentDescription("Delete selection"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.action_mode_bar),
                                        1),
                                0),
                        isDisplayed()));
        actionMenuItemView.perform(click());

        ViewInteraction constraintLayout3 = onView(
                allOf(withId(R.id.friend_entry_layout),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.friends_recyclerView),
                                        0),
                                0),
                        isDisplayed()));
        constraintLayout3.perform(longClick());

        ViewInteraction textView2 = onView(
                allOf(withText("1"),
                        isDisplayed()));
        textView2.check(matches(withText("1")));
    }

    @Test
    public void typesDeleteDeselectionTest() {

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

        ViewInteraction textView = onView(
                allOf(withText("1"),
                        isDisplayed()));
        textView.check(matches(withText("1")));

        ViewInteraction actionMenuItemView = onView(
                allOf(withId(R.id.delete_selection), withContentDescription("Delete selection"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.action_context_bar),
                                        1),
                                1),
                        isDisplayed()));
        actionMenuItemView.perform(click());

        ViewInteraction relativeLayout2 = onView(
                allOf(withId(R.id.interaction_type_entry_layout),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.interaction_type_recyclerview),
                                        0),
                                0),
                        isDisplayed()));
        relativeLayout2.perform(longClick());

        ViewInteraction textView2 = onView(
                allOf( withText("1"),
                        isDisplayed()));
        textView2.check(matches(withText("1")));
    }

    @Test
    public void logEditDeselectionTest() {

        ViewInteraction bottomNavigationItemView = onView(
                allOf(withId(R.id.bottom_interactions),
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

        ViewInteraction actionMenuItemView = onView(
                allOf(withId(R.id.edit_selection), withContentDescription("Edit selection"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.action_mode_bar),
                                        1),
                                0),
                        isDisplayed()));
        actionMenuItemView.perform(click());

        // Added a sleep statement to match the app's execution delay.
        // The recommended way to handle such scenarios is to use Espresso idling resources:
        // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
        try {
            Thread.sleep(700);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ViewInteraction actionMenuItemView2 = onView(
                allOf(withId(R.id.icon_save), withContentDescription("Save"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.action_bar),
                                        2),
                                0),
                        isDisplayed()));
        actionMenuItemView2.perform(click());

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

        ViewInteraction textView3 = onView(
                allOf(withText("1"),
                        isDisplayed()));
        textView3.check(matches(withText("1")));
    }

    @Test
    public void friendsEditDeselectionTest() {

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

        ViewInteraction actionMenuItemView = onView(
                allOf(withId(R.id.edit_selection), withContentDescription("Edit selection"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.action_mode_bar),
                                        1),
                                0),
                        isDisplayed()));
        actionMenuItemView.perform(click());

        // Added a sleep statement to match the app's execution delay.
        // The recommended way to handle such scenarios is to use Espresso idling resources:
        // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
        try {
            Thread.sleep(700);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ViewInteraction actionMenuItemView2 = onView(
                allOf(withId(R.id.icon_save), withContentDescription("Save"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.action_bar),
                                        2),
                                0),
                        isDisplayed()));
        actionMenuItemView2.perform(click());

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

        ViewInteraction textView3 = onView(
                allOf(withText("1"),
                        isDisplayed()));
        textView3.check(matches(withText("1")));
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
