package com.trulden.friends.util;

import android.content.Context;
import android.widget.Toast;

import com.trulden.friends.BuildConfig;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Util {

    // Can't access db version from Room in runtime, but want to save backup with version in name
    // Probably not the best way to do it, but can't think of something else
    public static final int DATABASE_VERSION = 1;

    public static final int NEW_INTERACTION_REQUEST    = 1;
    public static final int NEW_FRIEND_REQUEST         = 2;
    public static final int UPDATE_FRIEND_REQUEST      = 3;

    public static final int IMPORT_DATABASE_REQUEST    = 4;
    public static final int EXPORT_DATABASE_REQUEST    = 5;

    public static final String EXTRA_NEW_INTERACTION = "EXTRA_NEW_INTERACTION";

    public static final String EXTRA_FRIEND_ID   = "EXTRA_FRIEND_ID";
    public static final String EXTRA_FRIEND_NAME = "EXTRA_FRIEND_NAME";
    public static final String EXTRA_FRIEND_INFO = "EXTRA_FRIEND_INFO";

    public static final String EXTRA_FRAGMENT_TO_LOAD = "EXTRA_FRAGMENT_TO_LOAD";

    public static final String EXTRA_EXPORT_RESULT = "EXTRA_EXPORT_RESULT";

    public static final String ACTION_CUSTOM_BROADCAST =
            BuildConfig.APPLICATION_ID + ".ACTION_CUSTOM_BROADCAST";

    public static void makeToast(Context context, String text){
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
    }

    public static String getInnerBackupFilePath(Context context){
        return context.getFilesDir().getAbsolutePath() + "/backup.zip";
    }

    public static String generateBackupFileName() {
        String date = new SimpleDateFormat("dd-MMM-yyyy").format(Calendar.getInstance().getTime());

        String backupFileName = String.format("friends_db_v%d_%s.zip", DATABASE_VERSION, date);

        return backupFileName;
    }
}
