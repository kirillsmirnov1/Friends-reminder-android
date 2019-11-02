package com.trulden.friends.activity;

import androidx.test.espresso.ViewInteraction;
import androidx.test.filters.LargeTest;

import com.trulden.friends.R;

import org.hamcrest.Matcher;
import org.junit.Test;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.*;
import static androidx.test.espresso.assertion.ViewAssertions.*;
import static androidx.test.espresso.matcher.ViewMatchers.*;
import static org.hamcrest.Matchers.*;

@LargeTest
public class DeselectionTest extends AbstractMATest {

    @Test
    public void logManualDeselectionTest() {

        openInteractions();

        selectEntry(0, R.id.ei_layout, R.id.fi_recycler_view, true);

        sleep(250);

        ViewInteraction textView = checkSelectionCounterValue("1");

        selectEntry(0, R.id.ei_layout, R.id.fi_recycler_view, false);

        sleep(250);

        textView.check(doesNotExist());

        selectEntry(1, R.id.ei_layout, R.id.fi_recycler_view, true);

        sleep(250);

        checkSelectionCounterValue("1");
    }

    @Test
    public void friendsManualDeselectionTest() {

        openFriends();

        selectEntry(0, R.id.ef_layout, R.id.ff_recycler_view, true);

        sleep(250);

        ViewInteraction textView = checkSelectionCounterValue("1");

        selectEntry(0, R.id.ef_layout, R.id.ff_recycler_view, false);

        sleep(250);

        textView.check(doesNotExist());

        selectEntry(1, R.id.ef_layout, R.id.ff_recycler_view, true);

        sleep(250);

        checkSelectionCounterValue("1");
    }

    @Test
    public void typesManualDeselectionTest() {

        openTypes();

        selectEntry(0, R.id.eit_layout, R.id.ait_recycler_view, true);

        sleep(250);

        ViewInteraction textView = checkSelectionCounterValue("1");

        selectEntry(0, R.id.eit_layout, R.id.ait_recycler_view, false);

        sleep(250);

        textView.check(matches(not(isDisplayed())));

        selectEntry(1, R.id.eit_layout, R.id.ait_recycler_view, true);

        sleep(250);

        checkSelectionCounterValue("1");
    }

    @Test
    public void logAddInteractionDeselectionTest() {

        openInteractions();

        selectEntry(0, R.id.ei_layout, R.id.fi_recycler_view, true);

        ViewInteraction textView = checkSelectionCounterValue("1");

        openAddInteraction();

        sleep(250);

        navigateUp();

        sleep(250);

        textView.check(doesNotExist());

        selectEntry(1, R.id.ei_layout, R.id.fi_recycler_view, true);

        checkSelectionCounterValue("1");
    }

    @Test
    public void logAddFriendDeselectionTest() {

        openInteractions();

        selectEntry(0, R.id.ei_layout, R.id.fi_recycler_view, true);

        ViewInteraction textView = checkSelectionCounterValue("1");

        openAddFriend();

        sleep(250);

        navigateUp();

        sleep(250);

        textView.check(doesNotExist());

        selectEntry(1, R.id.ei_layout, R.id.fi_recycler_view, true);

        checkSelectionCounterValue("1");
    }

    @Test
    public void friendsAddInteractionDeselectionTest() {

        openFriends();

        selectEntry(0, R.id.ef_layout, R.id.ff_recycler_view, true);

        ViewInteraction textView = checkSelectionCounterValue("1");

        openAddInteraction();

        sleep(250);

        navigateUp();

        sleep(250);

        textView.check(doesNotExist());

        selectEntry(1, R.id.ef_layout, R.id.ff_recycler_view, true);

        checkSelectionCounterValue("1");
    }

    @Test
    public void friendsAddFriendDeselectionTest() {

        openFriends();

        selectEntry(0, R.id.ef_layout, R.id.ff_recycler_view, true);

        ViewInteraction textView = checkSelectionCounterValue("1");

        openAddFriend();

        navigateUp();

        sleep(250);

        textView.check(doesNotExist());

        selectEntry(1, R.id.ef_layout, R.id.ff_recycler_view, true);

        checkSelectionCounterValue("1");
    }

    @Test
    public void logDeleteDeselectionTest() {

        openInteractions();

        selectEntry(0, R.id.ei_layout, R.id.fi_recycler_view, true);

        selectEntry(1, R.id.ei_layout, R.id.fi_recycler_view, false);

        ViewInteraction textView = checkSelectionCounterValue("2");

        deleteSelection();

        sleep(250);

        textView.check(doesNotExist());

        selectEntry(0, R.id.ei_layout, R.id.fi_recycler_view, true);

        checkSelectionCounterValue("1");
    }

    @Test
    public void friendsDeleteDeselectionTest() {

        openFriends();

        selectEntry(0, R.id.ef_layout, R.id.ff_recycler_view, true);

        selectEntry(1, R.id.ef_layout, R.id.ff_recycler_view, false);

        ViewInteraction textView = checkSelectionCounterValue("2");

        onView(withId(R.id.msed_delete)).perform(click());
        onView(withText(android.R.string.ok)).perform(click());

        sleep(250);

        textView.check(doesNotExist());

        selectEntry(0, R.id.ef_layout, R.id.ff_recycler_view, true);

        checkSelectionCounterValue("1");
    }

    @Test
    public void typesDeleteDeselectionTest() {

        openTypes();

        selectEntry(0, R.id.eit_layout, R.id.ait_recycler_view, true);

        ViewInteraction textView = checkSelectionCounterValue("1");

        onView(withId(R.id.msed_delete)).perform(click());
        onView(withText(android.R.string.ok)).perform(click());

        textView.check(matches(not(isDisplayed())));

        selectEntry(0, R.id.eit_layout, R.id.ait_recycler_view, true);

        checkSelectionCounterValue("1");
    }

    @Test
    public void logEditDeselectionTest() {

        openInteractions();

        selectEntry(0, R.id.ei_layout, R.id.fi_recycler_view, true);

        ViewInteraction textView = checkSelectionCounterValue("1");

        editSelection();

        sleep(250);

        clickSaveOnActionBar();

        sleep(250);

        textView.check(doesNotExist());

        selectEntry(1, R.id.ei_layout, R.id.fi_recycler_view, true);

        checkSelectionCounterValue("1");
    }

    @Test
    public void friendsEditDeselectionTest() {

        openFriends();

        selectEntry(0, R.id.ef_layout, R.id.ff_recycler_view, true);

        ViewInteraction textView = checkSelectionCounterValue("1");

        editSelection();

        sleep(250);

        clickSaveOnActionBar();

        sleep(250);

        textView.check(doesNotExist());

        selectEntry(1, R.id.ef_layout, R.id.ff_recycler_view, true);

        checkSelectionCounterValue("1");
    }

    @Test
    public void typesEditDeselectionTest() {

        openTypes();

        selectEntry(0, R.id.eit_layout, R.id.ait_recycler_view, true);

        ViewInteraction textView = checkSelectionCounterValue("1");

        editSelection();

        sleep(250);

        clickSaveOnDialog();

        sleep(250);

        textView.check(matches(not(isDisplayed())));

        selectEntry(1, R.id.eit_layout, R.id.ait_recycler_view, true);

        checkSelectionCounterValue("1");
    }

    @Test
    public void logSwitchFragmentDeselectionTest(){
        openInteractions();

        selectEntry(0, R.id.ei_layout, R.id.fi_recycler_view, true);
        selectEntry(1, R.id.ei_layout, R.id.fi_recycler_view, false);

        ViewInteraction selectionCounter = checkSelectionCounterValue("2");

        openFriends();

        sleep(250);

        selectionCounter.check(doesNotExist());

        openInteractions();

        selectEntry(2, R.id.ei_layout, R.id.fi_recycler_view, true);

        checkSelectionCounterValue("1");
    }

    @Test
    public void friendsSwitchFragmentDeselectionTest(){
        openFriends();

        selectEntry(0, R.id.ef_layout, R.id.ff_recycler_view, true);
        selectEntry(1, R.id.ef_layout, R.id.ff_recycler_view, false);

        ViewInteraction selectionCounter = checkSelectionCounterValue("2");

        openInteractions();

        sleep(250);

        selectionCounter.check(doesNotExist());

        openFriends();

        selectEntry(2, R.id.ef_layout, R.id.ff_recycler_view, true);

        checkSelectionCounterValue("1");
    }

    @Test
    public void logDeselectAllTest() {

        openInteractions();

        selectEntry(0, R.id.ei_layout, R.id.fi_recycler_view, true);
        selectEntry(1, R.id.ei_layout, R.id.fi_recycler_view, false);

        ViewInteraction selectionCounter = checkSelectionCounterValue("2");

        deselectAll();

        sleep(250);

        selectionCounter.check(doesNotExist());

        selectEntry(2, R.id.ei_layout, R.id.fi_recycler_view, true);

        checkSelectionCounterValue("1");
    }

    @Test
    public void friendsDeselectAllTest(){

        openFriends();

        selectEntry(0, R.id.ef_layout, R.id.ff_recycler_view, true);
        selectEntry(1, R.id.ef_layout, R.id.ff_recycler_view, false);

        ViewInteraction selectionCounter = checkSelectionCounterValue("2");

        deselectAll();

        sleep(250);

        selectionCounter.check(doesNotExist());

        selectEntry(2, R.id.ef_layout, R.id.ff_recycler_view, true);

        checkSelectionCounterValue("1");
    }

    @Test
    public void typesDeselectAllTest(){
        openTypes();

        selectEntry(0, R.id.eit_layout, R.id.ait_recycler_view, true);
        selectEntry(1, R.id.eit_layout, R.id.ait_recycler_view, false);

        ViewInteraction selectionCounter = checkSelectionCounterValue("2");

        deselectAll();

        sleep(250);

        selectionCounter.check(matches(not(isDisplayed())));

        selectEntry(1, R.id.eit_layout, R.id.ait_recycler_view, true);

        checkSelectionCounterValue("1");
    }

    @Test
    public void LISelectionTest(){

        LISelectionTemplateTest(withId(R.id.am_fab_add_friend), withContentDescription("Navigate up"));

        LISelectionTemplateTest(withText("Texting"), withText("Meeting"));
        LISelectionTemplateTest(withId(R.id.mbn_interactions), withId(R.id.mbn_last_interactions));
        LISelectionTemplateTest(withId(R.id.mbn_friends), withId(R.id.mbn_last_interactions));
    }

    @Test
    public void typesNoDeleteNoDeselectionTest(){
        openTypes();

        onView(withText("Meeting")).perform(longClick());

        onView(withId(R.id.msed_delete)).perform(click());

        onView(withText(android.R.string.cancel)).perform(click());

        onView(withText("1")).check(matches(isDisplayed()));
    }

    @Test
    public void friendsNoDeleteNoDeselectionTest(){
        openFriends();

        onView(withText("Caleb")).perform(longClick());

        onView(withId(R.id.msed_delete)).perform(click());

        onView(withText(android.R.string.cancel)).perform(click());

        onView(withText("1")).check(matches(isDisplayed()));
    }

    private void LISelectionTemplateTest(Matcher goTo, Matcher comebackTo){
        openLastInteractions();

        onView(withText("Caleb")).perform(longClick());

        ViewInteraction selCounter = onView(allOf(withText("1"), not(hasSibling(withText("Meeting")))));
        selCounter.check(matches(isDisplayed()));

        onView(goTo).perform(click());

        if(goTo.toString().equals("with id: com.trulden.friends:id/am_fab_add_friend")) {
            sleep(250);
            onView(goTo).perform(click());
        }

        sleep(250);

        selCounter.check(doesNotExist());

        onView(comebackTo).perform(click());

        onView(withText("Aaron")).perform(longClick());
        onView(withText("Balaam")).perform(click());

        checkSelectionCounterValue("2");
    }

    // UI interactions

    private ViewInteraction checkSelectionCounterValue(String value) {
        ViewInteraction textView = onView(withText(value));
        textView.check(matches(isDisplayed()));

        return textView;
    }

    private void deleteSelection() {
        onView(withId(R.id.msed_delete)).perform(click());
    }

    private void clickSaveOnDialog() {
        onView(withText("Save")).perform(scrollTo(), click());
    }

    private void deselectAll() {
        onView(withId(R.id.action_mode_close_button)).perform(click());
    }

}
