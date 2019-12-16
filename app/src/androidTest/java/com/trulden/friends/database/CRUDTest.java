package com.trulden.friends.database;

import com.trulden.friends.DatabaseTestingHandler;
import com.trulden.friends.R;
import com.trulden.friends.activity.AbstractMATest;
import com.trulden.friends.database.entity.Friend;
import com.trulden.friends.database.entity.InteractionType;
import com.trulden.friends.util.Util;

import org.junit.Test;

import java.util.Calendar;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.*;
import static androidx.test.espresso.assertion.ViewAssertions.*;
import static androidx.test.espresso.matcher.ViewMatchers.*;
import static org.hamcrest.Matchers.*;

public class CRUDTest extends AbstractMATest {

    @Test
    public void createFriendTest(){

        Friend oldFriend = DatabaseTestingHandler.friends[0];
        Friend newFriend = new Friend("David", "A young shepherd");

        openAddFriend();

        onView(withText(R.string.add_friend)).check(matches(isDisplayed()));

        onView(withId(R.id.aef_edit_name)).check(matches(withText("")));
        onView(withId(R.id.aef_edit_info)).check(matches(withText("")));

        onView(withId(R.id.aef_edit_name)).perform(typeText(oldFriend.getName()));
        clickSaveOnActionBar();

        // Can't check toasts while running all tests without sleeping a lot
        //checkIfToastAppeared(R.string.toast_warning_friend_exists);

        onView(withId(R.id.aef_edit_name)).perform(clearText(), typeText(newFriend.getName()));
        onView(withId(R.id.aef_edit_info)).perform(typeText(newFriend.getInfo()));
        clickSaveOnActionBar();

        openFriends();

        onView(withText(newFriend.getName())).perform(click());

        onView(withText(newFriend.getName())).check(matches(isDisplayed()));
        onView(withText(newFriend.getInfo())).check(matches(isDisplayed()));
    }

    @Test
    public void createInteractionTest() {

        openAddInteraction();

        onView(withId(R.id.aei_type_spinner)).perform(click());

        sleep(250);

        chooseDropDownOption(2);

        onView(withId(R.id.deit_name))
                .perform(replaceText("Meeting"));

        onView(withId(R.id.deit_frequency))
                .perform(replaceText("7"));

        onView(withText("Save")).perform(scrollTo(), click()); // Won't work because type already exists

        onView(withId(R.id.deit_name))
                .perform(replaceText("Mess"));

        onView(withText("Save")).perform(scrollTo(), click());

        // Press save button manually, bc espresso fails to
        // FIXME breakpoint must be there until fixed
        onView(withId(R.id.aei_edit_date)).perform(click());

        sleep(250);

        Calendar tomorrow = Calendar.getInstance();
        tomorrow.add(Calendar.DAY_OF_MONTH, 1);

        setDatePicker(tomorrow);
        onView(withText("OK")).perform(click()); // And it won't work and it's okay

        setDatePicker(Calendar.getInstance());
        onView(withText("OK")).perform(click());

        String today = Util.formatDate(Calendar.getInstance());

        onView(withId(R.id.aei_edit_date)).check(matches(withText(today)));

        onView(withId(R.id.aei_edit_friends)).perform(replaceText("Baron, Goliath"), closeSoftKeyboard());

        onView(withId(R.id.aei_edit_comment))
                .perform(replaceText("Death to everybody"), closeSoftKeyboard());

        onView(withId(R.id.ms_save)).perform(click());

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

        onView(allOf(withId(R.id.eli_friend_name), withText("Goliath")))
                .check(matches(isDisplayed()));

        onView(allOf(withId(R.id.eli_time_passed), isDisplayed(), hasSibling(withText("Goliath"))))
                .check(matches(withText("0 d. ago")));

        onView(allOf(withId(R.id.eli_time_passed), isDisplayed(), hasSibling(withText("Aaron"))))
                .check(matches(withText("0 d. ago")));

        onView(withId(R.id.mbn_interactions)).perform(click());

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

        onView(withId(R.id.ma_add)).perform(click());

        onView(withText(R.string.new_interaction_type)).check(matches(isDisplayed()));

        onView(withId(R.id.deit_name))
                .perform(typeText(type.getInteractionTypeName()));
        onView(withId(R.id.deit_frequency))
                .perform(typeText(String.valueOf(type.getFrequency())));

        onView(withText(R.string.save)).perform(click());

        onView(withId(R.id.deit_name))
                .perform(replaceText(call));
        onView(withId(R.id.deit_frequency))
                .perform(replaceText(freq));

        onView(withText(R.string.save)).perform(click());

        onView(withText(call)).check(matches(hasSibling(withSubstring(freq))));

        navigateUp();

        onView(withId(R.id.mbn_last_interactions)).perform(click());

        onView(withText(call)).check(matches(isDisplayed()));
    }

    @Test
    public void updateFriendTest(){
        Friend oldFriend = DatabaseTestingHandler.friends[0];
        Friend newFriend = new Friend("Adam", "First man");

        openFriends();

        onView(withText(oldFriend.getName())).perform(longClick());

        onView(withId(R.id.mam_edit)).perform(click());

        onView(withId(R.id.aef_edit_name))
                .perform(replaceText(newFriend.getName()));
        onView(withId(R.id.aef_edit_info))
                .perform(replaceText(newFriend.getInfo()));

        onView(withId(R.id.ms_save)).perform(click());

        onView(withText(oldFriend.getName())).check(doesNotExist());

        onView(withText(newFriend.getName())).perform(click());

        onView(withText(newFriend.getInfo())).check(matches(isDisplayed()))
                .perform(pressBack());

        onView(withId(R.id.mbn_interactions)).perform(click());

        onView(withText("A + B")).check(matches(allOf(
                hasSibling(withSubstring(newFriend.getName())),
                not(hasSibling(withSubstring(oldFriend.getName()))))));

        onView(withSubstring(oldFriend.getName())).check(doesNotExist());

        onView(withId(R.id.mbn_last_interactions)).perform(click());

        onView(withText("Meeting")).perform(click());

        onView(withText(newFriend.getName())).check(matches(isDisplayed()));

        onView(withSubstring(oldFriend.getName())).check(doesNotExist());

    }

    @Test
    public void updateFriendFromFPATest(){
        openFriends();
        onView(withText("Aaron")).perform(click());

        openOverflow();
        onView(withText(R.string.edit)).perform(click());

        onView(withId(R.id.aef_edit_name)).perform(replaceText("August"));
        onView(withId(R.id.ms_save)).perform(click());

        onView(withText("Aaron")).check(doesNotExist());
        onView(withText("August")).check(matches(isDisplayed()));
    }

    @Test
    public void updateInteractionTest(){

        Calendar yesterday = Calendar.getInstance();
        yesterday.add(Calendar.DAY_OF_MONTH, -1);
        String yesterdayString = Util.formatDate(yesterday);

        openInteractions();

        onView(withText("A + B")).perform(longClick());

        onView(withId(R.id.mam_edit)).perform(click());

        sleep(250);

        onView(withId(R.id.aei_type_spinner)).perform(click());
        chooseDropDownOption(1);

        onView(withId(R.id.aei_edit_date)).perform(click());

        setDatePicker(yesterday);
        onView(withText("OK")).perform(click());

        onView(withId(R.id.aei_edit_friends))
                .perform(typeText(", Caleb"), closeSoftKeyboard());
        onView(withId(R.id.aei_edit_comment))
                .perform(click(), typeText(" + C"));

        onView(withId(R.id.ms_save)).perform(click());

        onView(withText("A + B + C")).check(matches(allOf(
                hasSibling(allOf(
                        withSubstring("Aaron"),
                        withSubstring("Balaam"),
                        withSubstring("Caleb")
                        )),
                hasSibling(allOf(
                        hasDescendant(withText(yesterdayString)),
                        hasDescendant(withText("Texting"))))
        )));

        openLastInteractions();

        onView(withText("Meeting")).perform(click());

        onView(allOf(withText("Caleb"), isDisplayed()))
                .check(matches(hasSibling(withText("30 d. ago"))));
        onView(withText("Meeting")).check(matches(hasSibling(withText("3"))));

        onView(withText("Texting")).perform(click());

        onView(allOf(withText("Aaron"), isDisplayed()))
                .check(matches(hasSibling(withText("1 d. ago"))));
        onView(allOf(withText("Balaam"), isDisplayed()))
                .check(matches(hasSibling(withText("1 d. ago"))));
        onView(allOf(withText("Caleb"), isDisplayed()))
                .check(matches(hasSibling(withText("1 d. ago"))));
    }

    @Test
    public void updateTypeTest(){

        openLastInteractions();

        onView(allOf(withId(R.id.vtlwc_count), hasSibling(withText("Meeting"))))
                .check(matches(withText("1")));

        openTypes();
        onView(withText("Meeting")).perform(longClick());
        onView(withId(R.id.mam_edit)).perform(click());

        onView(withId(R.id.deit_frequency))
                .perform(replaceText("29"));

        onView(withText("SAVE")).perform(click());

        navigateUp();

        onView(allOf(withId(R.id.vtlwc_count), hasSibling(withText("Meeting"))))
                .check(matches(withText("3")));
    }

    @Test
    public void deleteFriendTest(){
        openFriends();

        String aaron = DatabaseTestingHandler.friends[0].getName();
        String balaam = DatabaseTestingHandler.friends[1].getName();
        String caleb = DatabaseTestingHandler.friends[2].getName();

        onView(withText(aaron)).perform(longClick());
        onView(withText(balaam)).perform(click());

        onView(withId(R.id.mam_delete)).perform(click());
        onView(withText(android.R.string.ok)).perform(click());

        onView(withText(aaron)).check(doesNotExist());
        onView(withText(balaam)).check(doesNotExist());

        openLastInteractions();

        onView(withText(aaron)).check(doesNotExist());
        onView(withText(balaam)).check(doesNotExist());

        onView(withText("Caleb")).check(matches(isDisplayed()));

        openInteractions();

        onView(withText("A + B")).check(doesNotExist());

        onView(withText("B + C")).check(matches(allOf(
            hasSibling(withText(caleb)),
            not(hasSibling(withText(balaam)))
        )));

        onView(withText("C + A")).check(matches(allOf(
                hasSibling(withText(caleb)),
                not(hasSibling(withText(aaron)))
        )));
    }

    @Test
    public void deleteFriendFromFPATest(){
        openFriends();
        onView(withText("Aaron")).perform(click());

        openOverflow();
        onView(withText(R.string.delete_selection)).perform(click());

        onView(withText("Aaron")).check(doesNotExist());
    }

    @Test
    public void deleteInteractionTest(){

        openLastInteractions();

        String aaron = DatabaseTestingHandler.friends[0].getName();
        String balaam = DatabaseTestingHandler.friends[1].getName();
        String caleb = DatabaseTestingHandler.friends[2].getName();

        onView(withText(aaron)).check(matches(hasSibling(withText("29 d. ago"))));
        onView(withText(balaam)).check(matches(hasSibling(withText("29 d. ago"))));
        onView(withText(caleb)).check(matches(hasSibling(withText("30 d. ago"))));

        openInteractions();

        onView(withText("A + B")).perform(longClick());
        onView(withText("B + C")).perform(click());

        onView(withId(R.id.mam_delete)).perform(click());

        onView(withText("A + B")).check(doesNotExist());
        onView(withText("B + C")).check(doesNotExist());

        openLastInteractions();

        onView(withText(aaron)).check(matches(hasSibling(withText("31 d. ago"))));
        onView(withText(balaam)).check(doesNotExist());
        onView(withText(caleb)).check(matches(hasSibling(withText("31 d. ago"))));
    }

    @Test
    public void deleteTypeTest(){

        String meeting = "Meeting";

        openTypes();

        onView(withText(meeting)).perform(longClick());

        onView(withId(R.id.mam_delete)).perform(click());
        onView(withText(android.R.string.ok)).perform(click());

        navigateUp();

        onView(withText(meeting)).check(doesNotExist());

        openInteractions();

        onView(withText(meeting)).check(doesNotExist());
    }

    /**
     * Check if deletion of types doesn't cause NPE
     */
    @Test
    public void noTypesTest(){
        openTypes();

        selectAllTypes();

        onView(withId(R.id.mam_delete)).perform(click());
        onView(withText(android.R.string.ok)).perform(click());

        navigateUp();

        openFriends();

        openInteractions();

        openAddInteraction();
    }
}
