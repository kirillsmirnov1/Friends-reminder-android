package com.trulden.friends.screenshot;

import com.trulden.friends.R;
import com.trulden.friends.activity.AbstractMATest;

import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;

import tools.fastlane.screengrab.Screengrab;
import tools.fastlane.screengrab.UiAutomatorScreenshotStrategy;
import tools.fastlane.screengrab.locale.LocaleTestRule;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

public class ScreenshotTest extends AbstractMATest {

    @ClassRule
    public static final LocaleTestRule localTestRule = new LocaleTestRule();

    @Before
    public void setUp(){
        Screengrab.setDefaultScreenshotStrategy(new UiAutomatorScreenshotStrategy());
    }

    @Test
    public void captureScreen(){
        sleep(500);
        Screengrab.screenshot("trackers");

        onView(withId(R.id.mbn_interactions)).perform(click());
        sleep(500);

        Screengrab.screenshot("journal");
    }
}
