package com.trulden.friends.util;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.trulden.friends.BuildConfig;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import static com.trulden.friends.database.FriendsDatabase.DATABASE_NAME;

public class Util {

    private static final String LOG_TAG = Util.class.getSimpleName();

    public static final SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy");

    // Can't access db version from Room in runtime, but want to save backup with version in name
    // Probably not the best way to do it, but can't think of something else
    public static final int DATABASE_VERSION = 4;

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
    public static final String EXTRA_INTERACTION_DATE = "EXTRA_INTERACTION_DATE";
    public static final String EXTRA_INTERACTION_COMMENT = "EXTRA_INTERACTION_COMMENT";

    public static final String EXTRA_FRIEND_ID   = "EXTRA_FRIEND_ID";
    public static final String EXTRA_FRIEND_NAME = "EXTRA_FRIEND_NAME";
    public static final String EXTRA_FRIEND_INFO = "EXTRA_FRIEND_INFO";

    public static final String EXTRA_FRAGMENT_TO_LOAD = "EXTRA_FRAGMENT_TO_LOAD";

    public static final String EXTRA_EXPORT_RESULT = "EXTRA_EXPORT_RESULT";
    public static final String EXTRA_IMPORT_RESULT = "EXTRA_IMPORT_RESULT";

    public static final String ACTION_DATABASE_EXPORT_FINISHED =
            BuildConfig.APPLICATION_ID + ".ACTION_DATABASE_EXPORT_FINISHED";
    public static final String ACTION_DATABASE_IMPORT_FINISHED =
            BuildConfig.APPLICATION_ID + ".ACTION_DATABASE_IMPORT_FINISHED";

    public static void makeToast(Context context, String text){
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
    }

    public static void makeSnackbar(View view, String text){
        Snackbar.make(view, text, Snackbar.LENGTH_SHORT).show();
    }

    public static String getInnerBackupFilePath(Context context){
        return context.getFilesDir().getAbsolutePath() + "/backup.zip";
    }

    public static String generateBackupFileName() {
        String date = dateFormat.format(Calendar.getInstance().getTime());

        String backupFileName = String.format("friends_db_v%d_%s.zip", DATABASE_VERSION, date);

        return backupFileName;
    }

    public static String[] getDbPaths(Context context) {
        String dbPath = context.getDatabasePath(DATABASE_NAME).getAbsolutePath();
        return new String[]{dbPath, dbPath + "-wal", dbPath + "-shm"};
    }

    public static void wipeDatabase(Context context) {
        String[] dbPaths = getDbPaths(context);
        for(String str : dbPaths){
            if(! new File(str).delete()){
                Log.e(LOG_TAG, "Error wiping database");
            }
        }
    }
}
