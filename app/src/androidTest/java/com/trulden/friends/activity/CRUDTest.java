package com.trulden.friends.activity;

import com.trulden.friends.AbstractTest;
import com.trulden.friends.DatabaseTestingHandler;
import com.trulden.friends.R;
import com.trulden.friends.database.entity.Friend;
import com.trulden.friends.database.entity.InteractionType;
import com.trulden.friends.util.Util;

import org.junit.Test;

import java.util.Calendar;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.*;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.*;
import static org.hamcrest.Matchers.*;

public class CRUDTest extends AbstractTest {

    @Test
    public void createFriendTest(){

        Friend oldFriend = DatabaseTestingHandler.friends[0];
        Friend newFriend = new Friend("David", "A young shepherd");

        openAddFriend();

        onView(withText(R.string.add_friend)).check(matches(isDisplayed()));

        onView(withId(R.id.edit_friends_name)).check(matches(withText("")));
        onView(withId(R.id.edit_friends_info)).check(matches(withText("")));

        onView(withId(R.id.edit_friends_name)).perform(typeText(oldFriend.getName()));
        clickSaveOnActionBar();

        // Can't check toasts while running all tests without sleeping a lot
        //checkIfToastAppeared(R.string.toast_warning_friend_exists);

        onView(withId(R.id.edit_friends_name)).perform(clearText(), typeText(newFriend.getName()));
        onView(withId(R.id.edit_friends_info)).perform(typeText(newFriend.getInfo()));
        clickSaveOnActionBar();

        //TODO checkIfToastAppeared("«David» is created");

        openFriends();

        onView(withText(newFriend.getName())).perform(click());

        onView(withText(newFriend.getName())).check(matches(isDisplayed()));
        onView(withText(newFriend.getInfo())).check(matches(isDisplayed()));
    }

    @Test
    public void createInteractionTest() {

        openAddInteraction();

        onView(withId(R.id.interaction_type_spinner)).perform(click());

        sleep(250);

        onData(anything())
                .inAdapterView(withClassName(is("androidx.appcompat.widget.DropDownListView")))
                .atPosition(2)
                .perform(click());

        onView(withId(R.id.edit_interaction_type_name))
                .perform(replaceText("Meeting"));

        onView(withId(R.id.edit_interaction_type_frequency))
                .perform(replaceText("7"));

        onView(withText("Save")).perform(scrollTo(), click()); // Won't work because type already exists

        onView(withId(R.id.edit_interaction_type_name))
                .perform(replaceText("Mess"));

        onView(withText("Save")).perform(scrollTo(), click());

        onView(withId(R.id.editDate)).perform(click());

        sleep(250);

        Calendar tomorrow = Calendar.getInstance();
        tomorrow.add(Calendar.DAY_OF_MONTH, 1);

        setDatePicker(tomorrow);
        onView(withText("OK")).perform(click()); // And it won't work and it's okay

        setDatePicker(Calendar.getInstance());
        onView(withText("OK")).perform(click());

        String today = Util.formatDate(Calendar.getInstance().getTimeInMillis());

        onView(withId(R.id.editDate)).check(matches(withText(today)));

        onView(withId(R.id.editFriends)).perform(replaceText("Baron, Goliath"), closeSoftKeyboard());

        onView(withId(R.id.editComment))
                .perform(replaceText("Death to everybody"), closeSoftKeyboard());

        onView(withId(R.id.icon_save)).perform(click());

        onView(withText("You don't have friend named «Baron»"))
                .check(matches(isDisplayed()));

        onView(withText("Edit")).perform(scrollTo(), click());

        onView(withText("Baron")).perform(replaceText("Aaron"));

        onView(withText("Save")).perform(scrollTo(), click());

        onView(withText("You don't have friend named «Goliath»"))
                .check(matches(isDisplayed()));

        onView(withText("Create")).perform(scrollTo(), click());

        sleep(250);

        onView(withText("Mess")).perform(click());

        onView(allOf(withId(R.id.last_interaction_name), withText("Goliath")))
                .check(matches(isDisplayed()));

        onView(allOf(withId(R.id.last_interaction_time), isDisplayed(), hasSibling(withText("Goliath"))))
                .check(matches(withText("0 d. ago")));

        onView(allOf(withId(R.id.last_interaction_time), isDisplayed(), hasSibling(withText("Aaron"))))
                .check(matches(withText("0 d. ago")));

        onView(withId(R.id.bottom_interactions)).perform(click());

        onView(allOf(withSubstring("Aaron"),withSubstring("Goliath")))
                .check(matches(allOf(
                        isDisplayed(),
                        hasSibling(allOf(
                            hasDescendant(withText("Mess")),
                            hasDescendant(withText(today)))
                        ),
                        hasSibling(withText("Death to everybody")))));
    }

    @Test
    public void createTypeTest(){

        InteractionType type = DatabaseTestingHandler.types[0];
        String call = "Call";
        String freq = "20";

        openTypes();

        onView(withId(R.id.add)).perform(click());

        onView(withText(R.string.new_interaction_type)).check(matches(isDisplayed()));

        onView(withId(R.id.edit_interaction_type_name))
                .perform(typeText(type.getInteractionTypeName()));
        onView(withId(R.id.edit_interaction_type_frequency))
                .perform(typeText(String.valueOf(type.getFrequency())));

        onView(withText(R.string.save)).perform(click());

        onView(withId(R.id.edit_interaction_type_name))
                .perform(replaceText(call));
        onView(withId(R.id.edit_interaction_type_frequency))
                .perform(replaceText(freq));

        onView(withText(R.string.save)).perform(click());

        onView(withText(call)).check(matches(hasSibling(withSubstring(freq))));

        navigateUp();

        onView(withId(R.id.bottom_last_interactions)).perform(click());

        onView(withText(call)).check(matches(isDisplayed()));
    }
}
