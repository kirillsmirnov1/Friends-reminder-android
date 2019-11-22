package com.trulden.friends.screenshot;

import android.app.Activity;
import android.net.Uri;
import android.view.View;
import android.widget.CheckBox;

import androidx.test.espresso.Espresso;
import androidx.test.espresso.ViewInteraction;
import androidx.test.rule.ActivityTestRule;

import com.trulden.friends.R;
import com.trulden.friends.TestUtil;
import com.trulden.friends.activity.MainActivity;
import com.trulden.friends.async.ImportDatabaseAsyncTask;

import junit.framework.AssertionFailedError;

import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import java.util.Locale;

import tools.fastlane.screengrab.Screengrab;
import tools.fastlane.screengrab.UiAutomatorScreenshotStrategy;
import tools.fastlane.screengrab.locale.LocaleTestRule;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.*;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static com.trulden.friends.database.FriendsDatabase.getDatabase;
import static com.trulden.friends.util.Util.wipeDatabaseFiles;
import static org.hamcrest.Matchers.*;

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

        guaranteeCheck(R.string.show_hidden_li_entries, true);
        guaranteeCheck(R.string.night_mode, false);

        sleep(3000);
    }

    @Test
    public void captureScreen(){
        sleep(500);
        Screengrab.screenshot(mLanguage + "_trackers");

        onView(allOf(withText(mFriend), isDisplayed())).perform(click());
        sleep(500);
        Screengrab.screenshot(mLanguage + "_tracker");

        Espresso.pressBack();
        sleep(500);

        onView(withId(R.id.mbn_interactions)).perform(click());
        sleep(500);

        Screengrab.screenshot(mLanguage + "_journal");

        onView(withId(R.id.mbn_friends)).perform(click());

        try {
            onView(withText(mFriend)).perform(click());
        } catch (Exception e){

        }
        sleep(500);

        Screengrab.screenshot(mLanguage + "_friend");
    }

    private void sleep(int millis){
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void guaranteeCheck(int stringId, boolean checked){
        openActionBarOverflowOrOptionsMenu(getInstrumentation().getTargetContext());

        try {
            // The checkbox of menu item is hidden pretty deep
            ViewInteraction v = onView(allOf(Matchers.<View>instanceOf(CheckBox.class), hasSibling(withChild(withText(stringId)))));

            if(checked) {
                v.check(matches(isChecked()));
            }
            else {
                v.check(matches(not(isChecked())));
            }

            Espresso.pressBack();

        } catch (AssertionFailedError e){
            onView(withText(stringId)).perform(click());
        }
    }
}
