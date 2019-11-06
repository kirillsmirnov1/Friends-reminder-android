package com.trulden.friends.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.WindowManager;

import com.trulden.friends.R;
import com.trulden.friends.activity.MainActivity;

import java.lang.ref.WeakReference;

import static com.trulden.friends.util.Util.ACTION_DATABASE_EXPORT_FINISHED;
import static com.trulden.friends.util.Util.ACTION_DATABASE_IMPORT_FINISHED;
import static com.trulden.friends.util.Util.EXTRA_EXPORT_RESULT;
import static com.trulden.friends.util.Util.EXTRA_FRAGMENT_TO_LOAD;
import static com.trulden.friends.util.Util.EXTRA_IMPORT_RESULT;
import static com.trulden.friends.util.Util.makeToast;

/**
 * Receives in-app broadcasts, such as finish of database export and import
 */
public class CustomBroadcastReceiver extends BroadcastReceiver {

    private WeakReference<MainActivity> mMainActivity;

    public CustomBroadcastReceiver(MainActivity mainActivity){
        mMainActivity = new WeakReference<>(mainActivity);
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        String intentAction = intent.getAction();

        if(intentAction != null){
            switch (intentAction) {
                case ACTION_DATABASE_EXPORT_FINISHED:
                    if(intent.getBooleanExtra(EXTRA_EXPORT_RESULT, false)){
                        makeToast(context, context.getString(R.string.toast_notice_export_succeeded));
                    } else {
                        makeToast(context, context.getString(R.string.toast_notice_export_failed));
                    }

                    // Hide progress bar
                    mMainActivity.get().findViewById(R.id.am_progress_bar).setVisibility(View.INVISIBLE);

                    // Make window touchable again
                    mMainActivity.get().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                    Intent restartIntent = new Intent(context, MainActivity.class);
                    restartIntent.putExtra(EXTRA_FRAGMENT_TO_LOAD, MainActivity.getFragmentToLoad());

                    // Easiest way to ensure correct update of data — restart an app
                    mMainActivity.get().finish();
                    mMainActivity.get().startActivity(restartIntent);

                    break;

                case ACTION_DATABASE_IMPORT_FINISHED:
                    if(intent.getBooleanExtra(EXTRA_IMPORT_RESULT, false)){
                        makeToast(context, context.getString(R.string.toast_notice_import_succeeded));
                    } else {
                        makeToast(context, context.getString(R.string.toast_notice_import_failed));
                    }

                    break;

            }
        }

    }
}
