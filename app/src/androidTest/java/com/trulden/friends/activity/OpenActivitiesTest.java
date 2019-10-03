package com.trulden.friends.activity;

import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;

import com.trulden.friends.AbstractTest;
import com.trulden.friends.R;

import org.junit.Rule;
import org.junit.Test;

import static androidx.test.espresso.Espresso.*;
import static androidx.test.espresso.assertion.ViewAssertions.*;
import static androidx.test.espresso.matcher.ViewMatchers.*;

@LargeTest
public class OpenActivitiesTest extends AbstractTest {

    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<>(MainActivity.class);

    @Test
    public void openEditFriendActivity(){
        openFriends();

        selectEntry(0, R.id.friend_entry_layout, R.id.friends_recyclerView, true);

        editSelection();

        onView(withText(R.string.action_bar_title_edit_friend)).check(matches(isDisplayed()));
    }
}
