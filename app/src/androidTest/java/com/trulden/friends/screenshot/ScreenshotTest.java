package com.trulden.friends.screenshot;

import android.app.Activity;
import android.net.Uri;

import androidx.test.rule.ActivityTestRule;

import com.trulden.friends.R;
import com.trulden.friends.TestUtil;
import com.trulden.friends.activity.MainActivity;
import com.trulden.friends.async.ImportDatabaseAsyncTask;

import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import tools.fastlane.screengrab.Screengrab;
import tools.fastlane.screengrab.UiAutomatorScreenshotStrategy;
import tools.fastlane.screengrab.locale.LocaleTestRule;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static com.trulden.friends.database.FriendsDatabase.getDatabase;
import static com.trulden.friends.util.Util.wipeDatabaseFiles;

public class ScreenshotTest {

    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<>(MainActivity.class);

    @ClassRule
    public static final LocaleTestRule localTestRule = new LocaleTestRule();

    @Before
    public void setUp(){
        Screengrab.setDefaultScreenshotStrategy(new UiAutomatorScreenshotStrategy());

        String uriString = "content://com.android.externalstorage.documents/document/131B-0B08%3ADownload%2Ffriends_db_stoics.zip";

        Activity activity = TestUtil.getActivityInstance();

        getDatabase(activity).close();
        wipeDatabaseFiles(activity);

        new ImportDatabaseAsyncTask(activity).execute(Uri.parse(uriString));

        sleep(3000);
    }

    @Test
    public void captureScreen(){
        sleep(500);
        Screengrab.screenshot("trackers");

        onView(withId(R.id.mbn_interactions)).perform(click());
        sleep(500);

        Screengrab.screenshot("journal");
    }

    private void sleep(int millis){
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
