package com.trulden.friends.screenshot;

import com.trulden.friends.activity.AbstractMATest;

import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;

import tools.fastlane.screengrab.Screengrab;
import tools.fastlane.screengrab.UiAutomatorScreenshotStrategy;
import tools.fastlane.screengrab.locale.LocaleTestRule;

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
        Screengrab.screenshot("main_screen");
    }
}
