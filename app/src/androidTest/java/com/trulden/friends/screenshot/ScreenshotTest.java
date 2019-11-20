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

import java.util.Locale;

import tools.fastlane.screengrab.Screengrab;
import tools.fastlane.screengrab.UiAutomatorScreenshotStrategy;
import tools.fastlane.screengrab.locale.LocaleTestRule;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static com.trulden.friends.database.FriendsDatabase.getDatabase;
import static com.trulden.friends.util.Util.wipeDatabaseFiles;

public class ScreenshotTest {

    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<>(MainActivity.class);

    @ClassRule
    public static final LocaleTestRule localTestRule = new LocaleTestRule();

    private String mLanguage;
    private String mFriend;

    @Before
    public void setUp(){
        Screengrab.setDefaultScreenshotStrategy(new UiAutomatorScreenshotStrategy());

        mLanguage = Locale.getDefault().getLanguage();

        String uriString;

        // Paths taken by debugging MainActivity.importDatabaseFromUri

        switch (mLanguage){
            case "ru":
                uriString = "content://com.android.externalstorage.documents/document/131B-0B08%3ADownload%2Ffriends_db_%D0%BF%D0%BE%D1%8D%D1%82%D1%8B.zip";
                mFriend = "Максим Горький";
                break;
            case "en":
            default:
                uriString = "content://com.android.externalstorage.documents/document/131B-0B08%3ADownload%2Ffriends_db_stoics.zip";
                mFriend = "Seneca";
        }

        Activity activity = TestUtil.getActivityInstance();

        getDatabase(activity).close();
        wipeDatabaseFiles(activity);

        new ImportDatabaseAsyncTask(activity).execute(Uri.parse(uriString));

        sleep(3000);
    }

    @Test
    public void captureScreen(){
        sleep(500);
        Screengrab.screenshot(mLanguage + "_trackers");

        onView(withId(R.id.mbn_interactions)).perform(click());
        sleep(500);

        Screengrab.screenshot(mLanguage + "_journal");

        onView(withId(R.id.mbn_friends)).perform(click());

        onView(withText(mFriend)).perform(click());
        sleep(500);;

        Screengrab.screenshot(mLanguage + "_friend");
    }

    private void sleep(int millis){
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
