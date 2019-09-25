package com.trulden.friends.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.trulden.friends.BuildConfig;
import com.trulden.friends.database.wrappers.LastInteraction;

import java.io.File;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Locale;

import static com.trulden.friends.database.FriendsDatabase.DATABASE_NAME;

/**
 * Stores keys and static functions used in different parts of app
 */
public abstract class Util {

    private static final String LOG_TAG = Util.class.getSimpleName();

    private static final int MILLISECONDS_IN_DAYS = 1000 * 60 * 60 * 24;

    public static DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.getDefault());

    // Can't access db version from Room in runtime, but want to save backup with version in name
    // Probably not the best way to do it, but can't think of something else

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // Keys
    ////////////////////////////////////////////////////////////////////////////////////////////////

    /** Version of database used in app */
    public static final int DATABASE_VERSION = 6;

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

    public static void makeToast(Context context, String text){
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
    }

    public static void makeSnackbar(View view, String text){
        Snackbar.make(view, text, Snackbar.LENGTH_SHORT).show();
    }

    public static String getInnerBackupFilePath(Context context){
        return context.getFilesDir().getAbsolutePath() + "/backup.zip";
    }

    @SuppressLint("DefaultLocale")
    public static String generateBackupFileName() {
        String date = dateFormat.format(Calendar.getInstance().getTime());

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
