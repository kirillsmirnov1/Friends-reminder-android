package com.trulden.friends.activity;

import androidx.test.filters.LargeTest;

import com.trulden.friends.DatabaseTestingHandler;
import com.trulden.friends.R;
import com.trulden.friends.database.entity.BindFriendInteraction;
import com.trulden.friends.database.entity.Friend;
import com.trulden.friends.database.entity.Interaction;
import com.trulden.friends.database.entity.InteractionType;
import com.trulden.friends.util.Util;

import org.junit.Test;

import static androidx.test.espresso.Espresso.*;
import static androidx.test.espresso.action.ViewActions.*;
import static androidx.test.espresso.assertion.ViewAssertions.*;
import static androidx.test.espresso.matcher.ViewMatchers.*;
import static org.hamcrest.Matchers.allOf;

@LargeTest
public class OpenActivitiesTest extends AbstractMATest {

    @Test
    public void openEditFriendActivityTest(){
        openFriends();

        String friendsName = DatabaseTestingHandler.friends[0].getName();
        String friendsNotes = DatabaseTestingHandler.friends[0].getInfo();

        onView(withText(friendsName)).perform(longClick());

        editSelection();

        onView(withId(R.id.ms_save)).check(matches(isDisplayed()));

        onView(withId(R.id.aef_edit_name)).check(matches(allOf(isDisplayed(), withText(friendsName))));
        onView(withText(friendsNotes)).check(matches(isDisplayed()));
    }

    @Test
    public void openEditInteractionActivityTest(){
        openInteractions();

        Interaction interaction = DatabaseTestingHandler.interactions[0];
        Friend[] friends = DatabaseTestingHandler.friends;

        String type = DatabaseTestingHandler.types[(int) (interaction.getInteractionTypeId()-1)]
                .getInteractionTypeName();
        String date = Util.formatDate(interaction.getDate());
        String comment = interaction.getComment();

        onView(withText(comment)).perform(longClick());

        editSelection();

        onView(withText(R.string.edit_interaction)).check(matches(isDisplayed()));

        onView(withText(type)).check(matches(isDisplayed()));
        onView(withText(comment)).check(matches(isDisplayed()));
        onView(withText(date)).check(matches(isDisplayed()));

        // Check friend names
        for (BindFriendInteraction bind : DatabaseTestingHandler.binds) {
            if (bind.getInteractionId() == interaction.getId()) {
                String friendsName = friends[(int) (bind.getFriendId() - 1)].getName();
                onView(withSubstring(friendsName)).check(matches(isDisplayed()));
            }
        }

    }

    @Test
    public void openEditTypeTest(){

        InteractionType type = DatabaseTestingHandler.types[0];

        openTypes();

        onView(withText(type.getInteractionTypeName())).perform(longClick());

        editSelection();

        onView(withText(R.string.edit_interaction_type)).check(matches(isDisplayed()));

        onView(withId(R.id.deit_name)).check(matches(withText(type.getInteractionTypeName())));
        onView(withId(R.id.deit_frequency))
                .check(matches(withText(String.valueOf(type.getFrequency()))));
    }

    @Test
    public void openFriendTest(){

        Friend friend = DatabaseTestingHandler.friends[0];

        openFriends();

        onView(withText(friend.getName())).perform(click());

        sleep(250);

        onView(childAtPosition(withId(R.id.toolbar),0))
                .check(matches(withText("Aaron")));

        onView(withId(R.id.afp_notes)).check(matches(withText(friend.getInfo())));
    }

    @Test
    public void switchFragmentsTest(){
        openInteractions();

        onView(childAtPosition(withId(R.id.toolbar), 0))
                .check(matches(withText(R.string.interactions_journal)));

        openLastInteractions();

        onView(childAtPosition(withId(R.id.toolbar), 0))
                .check(matches(withText(R.string.last_interactions)));

        openFriends();

        onView(childAtPosition(withId(R.id.toolbar),0))
                .check(matches(withText(R.string.friends)));
    }

    @Test
    public void openEIAthroughTFTest(){

        openLastInteractions();

        onView(withText("Caleb")).perform(click());

        onView(withText("B + C")).perform(longClick());

        onView(withText(R.string.edit_interaction)).check(matches(isDisplayed()));
    }
}
