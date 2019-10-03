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
import org.junit.Rule;
import org.junit.Test;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.*;
import static androidx.test.espresso.assertion.ViewAssertions.*;
import static androidx.test.espresso.matcher.ViewMatchers.*;
import static org.hamcrest.Matchers.*;

@LargeTest
public class DeselectionTest extends AbstractTest {

    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<>(MainActivity.class);

    @Test
    public void logManualDeselectionTest() {

        openLog();

        selectEntry(0, R.id.interaction_entry_layout, R.id.interactions_recyclerview, true);

        sleep(250);

        ViewInteraction textView = checkSelectionCounterValue("1");

        selectEntry(0, R.id.interaction_entry_layout, R.id.interactions_recyclerview, false);

        sleep(250);

        textView.check(doesNotExist());

        selectEntry(1, R.id.interaction_entry_layout, R.id.interactions_recyclerview, true);

        sleep(250);

        checkSelectionCounterValue("1");
    }

    @Test
    public void friendsManualDeselectionTest() {

        openFriends();

        selectEntry(0, R.id.friend_entry_layout, R.id.friends_recyclerView, true);

        sleep(250);

        ViewInteraction textView = checkSelectionCounterValue("1");

        selectEntry(0, R.id.friend_entry_layout, R.id.friends_recyclerView, false);

        sleep(250);

        textView.check(doesNotExist());

        selectEntry(1, R.id.friend_entry_layout, R.id.friends_recyclerView, true);

        sleep(250);

        checkSelectionCounterValue("1");
    }

    @Test
    public void typesManualDeselectionTest() {

        openTypes();

        selectEntry(0, R.id.interaction_type_entry_layout, R.id.interaction_type_recyclerview, true);

        sleep(250);

        ViewInteraction textView = checkSelectionCounterValue("1");

        selectEntry(0, R.id.interaction_type_entry_layout, R.id.interaction_type_recyclerview, false);

        sleep(250);

        textView.check(matches(not(isDisplayed())));

        selectEntry(1, R.id.interaction_type_entry_layout, R.id.interaction_type_recyclerview, true);

        sleep(250);

        checkSelectionCounterValue("1");
    }

    @Test
    public void logAddInteractionDeselectionTest() {

        openLog();

        selectEntry(0, R.id.interaction_entry_layout, R.id.interactions_recyclerview, true);

        ViewInteraction textView = checkSelectionCounterValue("1");

        openFab();

        fabClickAddInteraction();

        sleep(250);

        navigateUp();

        sleep(250);

        textView.check(doesNotExist());

        selectEntry(1, R.id.interaction_entry_layout, R.id.interactions_recyclerview, true);

        checkSelectionCounterValue("1");
    }

    @Test
    public void logAddFriendDeselectionTest() {

        openLog();

        selectEntry(0, R.id.interaction_entry_layout, R.id.interactions_recyclerview, true);

        ViewInteraction textView = checkSelectionCounterValue("1");

        openFab();

        fabClickAddFriend();

        sleep(250);

        navigateUp();

        sleep(250);

        textView.check(doesNotExist());

        selectEntry(1, R.id.interaction_entry_layout, R.id.interactions_recyclerview, true);

        checkSelectionCounterValue("1");
    }

    @Test
    public void friendsAddInteractionDeselectionTest() {

        openFriends();

        selectEntry(0, R.id.friend_entry_layout, R.id.friends_recyclerView, true);

        ViewInteraction textView = checkSelectionCounterValue("1");

        openFab();

        fabClickAddInteraction();

        sleep(250);

        navigateUp();

        sleep(250);

        textView.check(doesNotExist());

        selectEntry(1, R.id.friend_entry_layout, R.id.friends_recyclerView, true);

        checkSelectionCounterValue("1");
    }

    @Test
    public void friendsAddFriendDeselectionTest() {

        openFriends();

        selectEntry(0, R.id.friend_entry_layout, R.id.friends_recyclerView, true);

        ViewInteraction textView = checkSelectionCounterValue("1");

        openFab();

        fabClickAddFriend();

        sleep(250);

        navigateUp();

        sleep(250);

        textView.check(doesNotExist());

        selectEntry(1, R.id.friend_entry_layout, R.id.friends_recyclerView, true);

        checkSelectionCounterValue("1");
    }

    @Test
    public void logDeleteDeselectionTest() {

        openLog();

        selectEntry(0, R.id.interaction_entry_layout, R.id.interactions_recyclerview, true);

        selectEntry(1, R.id.interaction_entry_layout, R.id.interactions_recyclerview, false);

        ViewInteraction textView = checkSelectionCounterValue("2");

        deleteSelection();

        sleep(250);

        textView.check(doesNotExist());

        selectEntry(0, R.id.interaction_entry_layout, R.id.interactions_recyclerview, true);

        checkSelectionCounterValue("1");
    }

    @Test
    public void friendsDeleteDeselectionTest() {

        openFriends();

        selectEntry(0, R.id.friend_entry_layout, R.id.friends_recyclerView, true);

        selectEntry(1, R.id.friend_entry_layout, R.id.friends_recyclerView, false);

        ViewInteraction textView = checkSelectionCounterValue("2");

        deleteSelection();

        sleep(250);

        textView.check(doesNotExist());

        selectEntry(0, R.id.friend_entry_layout, R.id.friends_recyclerView, true);

        checkSelectionCounterValue("1");
    }

    @Test
    public void typesDeleteDeselectionTest() {

        openTypes();

        selectEntry(0, R.id.interaction_type_entry_layout, R.id.interaction_type_recyclerview, true);

        ViewInteraction textView = checkSelectionCounterValue("1");

        deleteSelection();

        textView.check(matches(not(isDisplayed())));

        selectEntry(0, R.id.interaction_type_entry_layout, R.id.interaction_type_recyclerview, true);

        checkSelectionCounterValue("1");
    }

    @Test
    public void logEditDeselectionTest() {

        openLog();

        selectEntry(0, R.id.interaction_entry_layout, R.id.interactions_recyclerview, true);

        ViewInteraction textView = checkSelectionCounterValue("1");

        editSelection();

        sleep(250);

        clickSaveOnActionBar();

        sleep(250);

        textView.check(doesNotExist());

        selectEntry(1, R.id.interaction_entry_layout, R.id.interactions_recyclerview, true);

        checkSelectionCounterValue("1");
    }

    @Test
    public void friendsEditDeselectionTest() {

        openFriends();

        selectEntry(0, R.id.friend_entry_layout, R.id.friends_recyclerView, true);

        ViewInteraction textView = checkSelectionCounterValue("1");

        editSelection();

        sleep(250);

        clickSaveOnActionBar();

        sleep(250);

        textView.check(doesNotExist());

        selectEntry(1, R.id.friend_entry_layout, R.id.friends_recyclerView, true);

        checkSelectionCounterValue("1");
    }

    @Test
    public void typesEditDeselectionTest() {

        openTypes();

        selectEntry(0, R.id.interaction_type_entry_layout, R.id.interaction_type_recyclerview, true);

        ViewInteraction textView = checkSelectionCounterValue("1");

        editSelection();

        sleep(250);

        clickSaveOnDialog();

        sleep(250);

        textView.check(matches(not(isDisplayed())));

        selectEntry(1, R.id.interaction_type_entry_layout, R.id.interaction_type_recyclerview, true);

        checkSelectionCounterValue("1");
    }

    @Test
    public void logSwitchFragmentDeselectionTest(){
        openLog();

        selectEntry(0, R.id.interaction_entry_layout, R.id.interactions_recyclerview, true);
        selectEntry(1, R.id.interaction_entry_layout, R.id.interactions_recyclerview, false);

        ViewInteraction selectionCounter = checkSelectionCounterValue("2");

        openFriends();

        sleep(250);

        selectionCounter.check(doesNotExist());

        openLog();

        selectEntry(2, R.id.interaction_entry_layout, R.id.interactions_recyclerview, true);

        checkSelectionCounterValue("1");
    }

    @Test
    public void friendsSwitchFragmentDeselectionTest(){
        openFriends();

        selectEntry(0, R.id.friend_entry_layout, R.id.friends_recyclerView, true);
        selectEntry(1, R.id.friend_entry_layout, R.id.friends_recyclerView, false);

        ViewInteraction selectionCounter = checkSelectionCounterValue("2");

        openLog();

        sleep(250);

        selectionCounter.check(doesNotExist());

        openFriends();

        selectEntry(2, R.id.friend_entry_layout, R.id.friends_recyclerView, true);

        checkSelectionCounterValue("1");
    }

    @Test
    public void logDeselectAllTest() {

        openLog();

        selectEntry(0, R.id.interaction_entry_layout, R.id.interactions_recyclerview, true);
        selectEntry(1, R.id.interaction_entry_layout, R.id.interactions_recyclerview, false);

        ViewInteraction selectionCounter = checkSelectionCounterValue("2");

        deselectAll();

        sleep(250);

        selectionCounter.check(doesNotExist());

        selectEntry(2, R.id.interaction_entry_layout, R.id.interactions_recyclerview, true);

        checkSelectionCounterValue("1");
    }

    @Test
    public void friendsDeselectAllTest(){

        openFriends();

        selectEntry(0, R.id.friend_entry_layout, R.id.friends_recyclerView, true);
        selectEntry(1, R.id.friend_entry_layout, R.id.friends_recyclerView, false);

        ViewInteraction selectionCounter = checkSelectionCounterValue("2");

        deselectAll();

        sleep(250);

        selectionCounter.check(doesNotExist());

        selectEntry(2, R.id.friend_entry_layout, R.id.friends_recyclerView, true);

        checkSelectionCounterValue("1");
    }

    @Test
    public void typesDeselectAllTest(){
        openTypes();

        selectEntry(0, R.id.interaction_type_entry_layout, R.id.interaction_type_recyclerview, true);
        selectEntry(1, R.id.interaction_type_entry_layout, R.id.interaction_type_recyclerview, false);

        ViewInteraction selectionCounter = checkSelectionCounterValue("2");

        deselectAll();

        sleep(250);

        selectionCounter.check(matches(not(isDisplayed())));

        selectEntry(1, R.id.interaction_type_entry_layout, R.id.interaction_type_recyclerview, true);

        checkSelectionCounterValue("1");
    }

    // UI interactions

    private void openLog() {
        onView(withId(R.id.bottom_interactions)).perform(click());
    }

    private void openTypes() {
        onView(childAtPosition(
                childAtPosition(withId(R.id.toolbar),1),
                0))
                .perform(click());

        sleep(250);

        onView(withText(R.string.manage_interaction_types)).perform(click());

        sleep(250);
    }

    private ViewInteraction checkSelectionCounterValue(String value) {
        ViewInteraction textView = onView(withText(value));
        textView.check(matches(isDisplayed()));

        return textView;
    }

    private void openFab() {
        onView(withId(R.id.fab_expand_menu_button)).perform(click());
    }

    private void fabClickAddInteraction() {
        onView(withId(R.id.fab_add_interaction)).perform(click());
    }

    private void fabClickAddFriend() {
        onView(withId(R.id.fab_add_friend)).perform(click());
    }

    private void navigateUp() {
        onView(withContentDescription("Navigate up")).perform(click());
    }

    private void deleteSelection() {
        onView(withId(R.id.delete_selection)).perform(click());
    }

    private void clickSaveOnActionBar() {
        onView(withId(R.id.icon_save)).perform(click());
    }

    private void clickSaveOnDialog() {
        onView(withText("Save")).perform(scrollTo(), click());
    }

    private void deselectAll() {
        onView(withId(R.id.action_mode_close_button)).perform(click());
    }

}
