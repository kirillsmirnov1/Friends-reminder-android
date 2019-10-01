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
import static androidx.test.espresso.action.ViewActions.scrollTo;
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
    public void logManualDeselectionTest() {

        openLog();

        selectEntry(0, R.id.interaction_entry_layout, R.id.interactions_recyclerview, true);

        sleep(250);

        ViewInteraction textView = checkSelectionCounter("1");

        selectEntry(0, R.id.interaction_entry_layout, R.id.interactions_recyclerview, false);

        sleep(250);

        textView.check(doesNotExist());

        selectEntry(1, R.id.interaction_entry_layout, R.id.interactions_recyclerview, true);

        sleep(250);

        checkSelectionCounter("1");
    }

    @Test
    public void friendsManualDeselectionTest() {

        openFriends();

        selectEntry(0, R.id.friend_entry_layout, R.id.friends_recyclerView, true);

        sleep(250);

        ViewInteraction textView = checkSelectionCounter("1");

        selectEntry(0, R.id.friend_entry_layout, R.id.friends_recyclerView, false);

        sleep(250);

        textView.check(doesNotExist());

        selectEntry(1, R.id.friend_entry_layout, R.id.friends_recyclerView, true);

        sleep(250);

        checkSelectionCounter("1");
    }

    @Test
    public void typesManualDeselectionTest() {

        openTypes();

        selectEntry(0, R.id.interaction_type_entry_layout, R.id.interaction_type_recyclerview, true);

        sleep(250);

        ViewInteraction textView = checkSelectionCounter("1");

        selectEntry(0, R.id.interaction_type_entry_layout, R.id.interaction_type_recyclerview, false);

        sleep(250);

        textView.check(doesNotExist());

        selectEntry(1, R.id.interaction_type_entry_layout, R.id.interaction_type_recyclerview, true);

        sleep(250);

        checkSelectionCounter("1");
    }

    @Test
    public void logAddInteractionDeselectionTest() {

        openLog();

        selectEntry(0, R.id.interaction_entry_layout, R.id.interactions_recyclerview, true);

        ViewInteraction textView = checkSelectionCounter("1");

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

        sleep(250);

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

        sleep(250);

        textView.check(doesNotExist());

        selectEntry(1, R.id.interaction_entry_layout, R.id.interactions_recyclerview, true);

        checkSelectionCounter("1");
    }

    @Test
    public void logAddFriendDeselectionTest() {

        openLog();

        selectEntry(0, R.id.interaction_entry_layout, R.id.interactions_recyclerview, true);;

        ViewInteraction textView = checkSelectionCounter("1");

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

        sleep(250);

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

        sleep(250);

        textView.check(doesNotExist());

        selectEntry(1, R.id.interaction_entry_layout, R.id.interactions_recyclerview, true);

        checkSelectionCounter("1");
    }

    @Test
    public void friendsAddInteractionDeselectionTest() {

        openFriends();

        selectEntry(0, R.id.friend_entry_layout, R.id.friends_recyclerView, true);

        ViewInteraction textView = checkSelectionCounter("1");

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

        sleep(250);

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

        sleep(250);

        textView.check(doesNotExist());

        selectEntry(1, R.id.friend_entry_layout, R.id.friends_recyclerView, true);

        checkSelectionCounter("1");
    }

    @Test
    public void friendsAddFriendDeselectionTest() {

        openFriends();

        selectEntry(0, R.id.friend_entry_layout, R.id.friends_recyclerView, true);

        ViewInteraction textView = checkSelectionCounter("1");

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

        sleep(250);

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

        sleep(250);

        textView.check(doesNotExist());

        selectEntry(1, R.id.friend_entry_layout, R.id.friends_recyclerView, true);

        checkSelectionCounter("1");
    }

    @Test
    public void logDeleteDeselectionTest() {

        openLog();

        selectEntry(0, R.id.interaction_entry_layout, R.id.interactions_recyclerview, true);

        selectEntry(1, R.id.interaction_entry_layout, R.id.interactions_recyclerview, false);

        ViewInteraction textView = checkSelectionCounter("2");

        ViewInteraction actionMenuItemView = onView(
                allOf(withId(R.id.delete_selection), withContentDescription("Delete selection"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.action_mode_bar),
                                        1),
                                0),
                        isDisplayed()));
        actionMenuItemView.perform(click());

        sleep(250);

        textView.check(doesNotExist());

        selectEntry(0, R.id.interaction_entry_layout, R.id.interactions_recyclerview, true);

        checkSelectionCounter("1");
    }

    @Test
    public void friendsDeleteDeselectionTest() {

        openFriends();

        selectEntry(0, R.id.friend_entry_layout, R.id.friends_recyclerView, true);

        selectEntry(1, R.id.friend_entry_layout, R.id.friends_recyclerView, false);

        ViewInteraction textView = checkSelectionCounter("2");

        ViewInteraction actionMenuItemView = onView(
                allOf(withId(R.id.delete_selection), withContentDescription("Delete selection"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.action_mode_bar),
                                        1),
                                0),
                        isDisplayed()));
        actionMenuItemView.perform(click());

        sleep(250);

        textView.check(doesNotExist());

        selectEntry(0, R.id.friend_entry_layout, R.id.friends_recyclerView, true);

        checkSelectionCounter("1");
    }

    @Test
    public void typesDeleteDeselectionTest() {

        openTypes();

        selectEntry(0, R.id.interaction_type_entry_layout, R.id.interaction_type_recyclerview, true);

        ViewInteraction textView = checkSelectionCounter("1");

        ViewInteraction actionMenuItemView = onView(
                allOf(withId(R.id.delete_selection), withContentDescription("Delete selection"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.action_context_bar),
                                        1),
                                1),
                        isDisplayed()));
        actionMenuItemView.perform(click());

        selectEntry(0, R.id.interaction_type_entry_layout, R.id.interaction_type_recyclerview, true);

        textView.check(doesNotExist());

        checkSelectionCounter("1");
    }

    @Test
    public void logEditDeselectionTest() {

        openLog();

        selectEntry(0, R.id.interaction_entry_layout, R.id.interactions_recyclerview, true);

        ViewInteraction textView = checkSelectionCounter("1");

        ViewInteraction actionMenuItemView = onView(
                allOf(withId(R.id.edit_selection), withContentDescription("Edit selection"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.action_mode_bar),
                                        1),
                                0),
                        isDisplayed()));
        actionMenuItemView.perform(click());

        sleep(250);

        ViewInteraction actionMenuItemView2 = onView(
                allOf(withId(R.id.icon_save), withContentDescription("Save"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.action_bar),
                                        2),
                                0),
                        isDisplayed()));
        actionMenuItemView2.perform(click());

        sleep(250);

        textView.check(doesNotExist());

        selectEntry(1, R.id.interaction_entry_layout, R.id.interactions_recyclerview, true);

        checkSelectionCounter("1");
    }

    @Test
    public void friendsEditDeselectionTest() {

        openFriends();

        selectEntry(0, R.id.friend_entry_layout, R.id.friends_recyclerView, true);

        ViewInteraction textView = checkSelectionCounter("1");

        ViewInteraction actionMenuItemView = onView(
                allOf(withId(R.id.edit_selection), withContentDescription("Edit selection"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.action_mode_bar),
                                        1),
                                0),
                        isDisplayed()));
        actionMenuItemView.perform(click());

        sleep(250);

        ViewInteraction actionMenuItemView2 = onView(
                allOf(withId(R.id.icon_save), withContentDescription("Save"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.action_bar),
                                        2),
                                0),
                        isDisplayed()));
        actionMenuItemView2.perform(click());

        sleep(250);

        textView.check(doesNotExist());

        selectEntry(1, R.id.friend_entry_layout, R.id.friends_recyclerView, true);

        checkSelectionCounter("1");
    }

    @Test
    public void typesEditDeselectionTest() {

        openTypes();

        selectEntry(0, R.id.interaction_type_entry_layout, R.id.interaction_type_recyclerview, true);

        ViewInteraction textView = checkSelectionCounter("1");

        ViewInteraction actionMenuItemView = onView(
                allOf(withId(R.id.edit_selection),
                        isDisplayed()));
        actionMenuItemView.perform(click());

        sleep(250);

        ViewInteraction appCompatButton = onView(
                allOf(withId(android.R.id.button1), withText("Save"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.buttonPanel),
                                        0),
                                3)));
        appCompatButton.perform(scrollTo(), click());

        sleep(250);

        textView.check(doesNotExist());

        selectEntry(1, R.id.interaction_type_entry_layout, R.id.interaction_type_recyclerview, true);

        checkSelectionCounter("1");
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

    private void sleep(int millis){
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    // UI interactions

    private void openLog() {

        ViewInteraction bottomNavigationItemView = onView(
            allOf(
                withId(R.id.bottom_interactions),
                isDisplayed()));

        bottomNavigationItemView.perform(click());
    }

    private void openFriends() {

        ViewInteraction bottomNavigationItemView = onView(
                allOf(
                        withId(R.id.bottom_friends),
                        isDisplayed()));

        bottomNavigationItemView.perform(click());
    }

    private void openTypes() {
        ViewInteraction overflowMenuButton2 = onView(
                allOf(childAtPosition(
                        childAtPosition(
                                withId(R.id.toolbar),
                                1),
                        0),
                        isDisplayed()));
        overflowMenuButton2.perform(click());

        sleep(250);

        ViewInteraction appCompatTextView2 = onView(
                allOf(withId(R.id.title), withText("Interaction types"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.content),
                                        0),
                                0),
                        isDisplayed()));
        appCompatTextView2.perform(click());

        sleep(250);
    }

    private void selectEntry(int pos, int entryID, int recyclerviewID, boolean longClick) {
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

    private ViewInteraction checkSelectionCounter(String value) {
        ViewInteraction textView = onView(
                allOf(withText(value),
                        isDisplayed()));
        textView.check(matches(withText(value)));

        return textView;
    }

}
