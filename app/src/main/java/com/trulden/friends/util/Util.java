package com.trulden.friends.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.fragment.app.FragmentActivity;

import com.google.android.material.snackbar.Snackbar;
import com.trulden.friends.BuildConfig;
import com.trulden.friends.activity.FriendPageActivity;
import com.trulden.friends.database.entity.Friend;
import com.trulden.friends.database.entity.LastInteraction;

import java.io.File;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

import static com.trulden.friends.database.FriendsDatabase.DATABASE_NAME;

/**
 * Stores keys and static functions used in different parts of app
 */
public abstract class Util {

    private static final String LOG_TAG = Util.class.getSimpleName();

    private static DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.getDefault());

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // Keys
    ////////////////////////////////////////////////////////////////////////////////////////////////

    /** Version of database used in app */
    public static final int DATABASE_VERSION = 8;

    // Use when need to get onActivityResult() call, but don't need any actual data
    public static final int NO_REQUEST                 = 0;

    public static final int NEW_INTERACTION_REQUEST    = 1;
    public static final int NEW_FRIEND_REQUEST         = 2;

    public static final int UPDATE_FRIEND_REQUEST      = 3;
    public static final int UPDATE_INTERACTION_REQUEST = 4;

    public static final int IMPORT_DATABASE_REQUEST    = 5;
    public static final int EXPORT_DATABASE_REQUEST    = 6;

    public static final String EXTRA_INTERACTION_ID = "EXTRA_INTERACTION_ID";
    public static final String EXTRA_INTERACTION_FRIEND_NAMES = "EXTRA_INTERACTION_FRIEND_NAMES";
    public static final String EXTRA_INTERACTION_FRIEND_IDS = "EXTRA_INTERACTION_FRIEND_IDS";
    public static final String EXTRA_INTERACTION_TYPE_ID = "EXTRA_INTERACTION_TYPE_ID";
    public static final String EXTRA_INTERACTION_TYPE_NAME = "EXTRA_INTERACTION_TYPE_NAME";
    public static final String EXTRA_INTERACTION_DATE = "EXTRA_INTERACTION_DATE";
    public static final String EXTRA_INTERACTION_COMMENT = "EXTRA_INTERACTION_COMMENT";

    public static final String EXTRA_FRIEND_ID   = "EXTRA_FRIEND_ID";
    public static final String EXTRA_FRIEND_NAME = "EXTRA_FRIEND_NAME";
    public static final String EXTRA_FRIEND_NOTES = "EXTRA_FRIEND_NOTES";

    public static final String EXTRA_FRAGMENT_TO_LOAD = "EXTRA_FRAGMENT_TO_LOAD";

    public static final String EXTRA_EXPORT_RESULT = "EXTRA_EXPORT_RESULT";
    public static final String EXTRA_IMPORT_RESULT = "EXTRA_IMPORT_RESULT";

    public static final String ACTION_DATABASE_EXPORT_FINISHED =
            BuildConfig.APPLICATION_ID + ".ACTION_DATABASE_EXPORT_FINISHED";
    public static final String ACTION_DATABASE_IMPORT_FINISHED =
            BuildConfig.APPLICATION_ID + ".ACTION_DATABASE_IMPORT_FINISHED";

    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Start {@link FriendPageActivity} with given friend
     * @param activity will start an intent
     * @param friend will be shown
     */
    public static void openFriendsPage(FragmentActivity activity, Friend friend){
        Intent intent = new Intent(activity, FriendPageActivity.class);
        intent.putExtra(EXTRA_FRIEND_ID, friend.getId());
        intent.putExtra(EXTRA_FRIEND_NAME, friend.getName());
        intent.putExtra(EXTRA_FRIEND_NOTES, friend.getInfo());
        Objects.requireNonNull(activity).startActivity(intent);
    }

    public static void makeToast(Context context, String text){
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
    }

    public static void makeSnackbar(View view, String text){
        Snackbar.make(view, text, Snackbar.LENGTH_SHORT).show();
    }

    public static String getInnerBackupFilePath(Context context){
        return context.getFilesDir().getAbsolutePath() + "/backup.zip";
    }

    public static String formatDate(Calendar calendar) {
        return formatDate(calendar.getTimeInMillis());
    }

    public static String formatDate(Date date){
        return formatDate(date.getTime());
    }

    public static String formatDate(long millis){
        return dateFormat.format(millis);
    }

    @SuppressLint("DefaultLocale")
    public static String generateBackupFileName() {
        String date = formatDate(Calendar.getInstance());

        return String.format("friends_db_v%d_%s.zip", DATABASE_VERSION, date);
    }

    /** Get paths of all database files */
    public static String[] getDbPaths(Context context) {
        String dbPath = context.getDatabasePath(DATABASE_NAME).getAbsolutePath();
        return new String[]{dbPath, dbPath + "-wal", dbPath + "-shm"};
    }

    /** Delete all database files*/
    public static void wipeDatabaseFiles(Context context) {
        String[] dbPaths = getDbPaths(context);
        for(String str : dbPaths){
            if(! new File(str).delete()){
                Log.e(LOG_TAG, "Error wiping database");
            }
        }
    }

    /**
     * Calculates how many days passed since that interaction
     * @return number of days
     */
    public static int daysPassed(LastInteraction interaction){

        Calendar dateOfLastInteraction = Calendar.getInstance();
        dateOfLastInteraction.setTimeInMillis(interaction.getDate());

        return calendarDaysBetween(dateOfLastInteraction, Calendar.getInstance());
    }

    private static int calendarDaysBetween(Calendar day1, Calendar day2){
        // Taken from here https://stackoverflow.com/a/28865648/11845909

        Calendar dayOne = (Calendar) day1.clone(),
                 dayTwo = (Calendar) day2.clone();

        if (dayOne.get(Calendar.YEAR) == dayTwo.get(Calendar.YEAR)) {
            return Math.abs(dayOne.get(Calendar.DAY_OF_YEAR) - dayTwo.get(Calendar.DAY_OF_YEAR));
        } else {
            if (dayTwo.get(Calendar.YEAR) > dayOne.get(Calendar.YEAR)) {
                //swap them
                Calendar temp = dayOne;
                dayOne = dayTwo;
                dayTwo = temp;
            }
            int extraDays = 0;

            int dayOneOriginalYearDays = dayOne.get(Calendar.DAY_OF_YEAR);

            while (dayOne.get(Calendar.YEAR) > dayTwo.get(Calendar.YEAR)) {
                dayOne.add(Calendar.YEAR, -1);
                // getActualMaximum() important for leap years
                extraDays += dayOne.getActualMaximum(Calendar.DAY_OF_YEAR);
            }

            return extraDays - dayTwo.get(Calendar.DAY_OF_YEAR) + dayOneOriginalYearDays ;
        }
    }
}
