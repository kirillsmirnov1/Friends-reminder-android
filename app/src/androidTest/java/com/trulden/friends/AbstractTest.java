package com.trulden.friends;

import android.os.IBinder;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.WindowManager;
import android.widget.DatePicker;

import androidx.fragment.app.FragmentActivity;
import androidx.test.espresso.Root;
import androidx.test.espresso.ViewInteraction;
import androidx.test.espresso.contrib.PickerActions;
import androidx.test.rule.ActivityTestRule;

import com.trulden.friends.activity.MainActivity;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Before;
import org.junit.Rule;

import java.util.Calendar;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static androidx.test.espresso.action.ViewActions.*;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.*;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static org.hamcrest.Matchers.*;

public abstract class AbstractTest {

    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<>(MainActivity.class);

    @Before
    public void initDB(){
        DatabaseTestingHandler.initAndFillDatabase(
                (FragmentActivity) TestUtil.getActivityInstance());
    }

    protected void openLog() {
        onView(withId(R.id.menu_bot_nav_interactions)).perform(click());
    }

    protected void openLastInteractions(){
        onView(withId(R.id.menu_bot_nav_last_interactions)).perform(click());
    }

    protected void openFriends() {
        onView(withId(R.id.menu_bot_nav_friends)).perform(click());
    }

    protected void openTypes() {

        openOverflow();

        sleep(250);

        onView(withText(R.string.manage_interaction_types)).perform(click());

        sleep(250);
    }

    protected void openFab() {
        onView(withId(R.id.fab_expand_menu_button)).perform(click());
    }

    protected void fabClickAddInteraction() {
        onView(withId(R.id.am_fab_add_interaction)).perform(click());
    }

    protected void fabClickAddFriend() {
        onView(withId(R.id.am_fab_add_friend)).perform(click());
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
        onView(withId(R.id.msed_edit)).perform(click());
    }

    protected void clickSaveOnActionBar() {
        onView(withId(R.id.ms_save)).perform(click());
    }

    protected void navigateUp() {
        onView(withContentDescription("Navigate up")).perform(click());
    }

    protected void sleep(int millis){
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    protected void checkIfToastAppeared(int toastStringId) {
        onView(withText(toastStringId))
                .inRoot(isToast())
                .check(matches(isDisplayed()));
    }

    protected void setDatePicker(Calendar c) {
        onView(withClassName(equalTo(DatePicker.class.getName())))
                .perform(PickerActions
                        .setDate(c.get(Calendar.YEAR), c.get(Calendar.MONTH) + 1, c.get(Calendar.DAY_OF_MONTH)));
    }

    protected void chooseDropDownOption(int pos) {
        onData(anything())
                .inAdapterView(withClassName(is("androidx.appcompat.widget.DropDownListView")))
                .atPosition(pos)
                .perform(click());
    }

    protected void openOverflow(){
        openActionBarOverflowOrOptionsMenu(getInstrumentation().getTargetContext());
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

    private static Matcher<Root> isToast() {
        return new TypeSafeMatcher<Root>() {

            @Override
            public void describeTo(Description description) {
                description.appendText("is toast");
            }

            @Override
            public boolean matchesSafely(Root root) {
                int type = root.getWindowLayoutParams().get().type;
                if ((type == WindowManager.LayoutParams.TYPE_TOAST)) {
                    IBinder windowToken = root.getDecorView().getWindowToken();
                    IBinder appToken = root.getDecorView().getApplicationWindowToken();
                    if (windowToken == appToken) {
                        // windowToken == appToken means this window isn't contained by any other windows.
                        // if it was a window for an activity, it would have TYPE_BASE_APPLICATION.
                        return true;
                    }
                }
                return false;
            }
        };
    }
}
