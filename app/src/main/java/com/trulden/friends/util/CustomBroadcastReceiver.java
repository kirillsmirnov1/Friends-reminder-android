package com.trulden.friends.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.WindowManager;

import com.trulden.friends.R;
import com.trulden.friends.activity.MainActivity;

import java.lang.ref.WeakReference;

import static com.trulden.friends.util.Util.*;

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

                    mMainActivity.get().findViewById(R.id.progress_bar_main).setVisibility(View.INVISIBLE);
                    mMainActivity.get().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                    break;

                case ACTION_DATABASE_IMPORT_FINISHED:
                    if(intent.getBooleanExtra(EXTRA_IMPORT_RESULT, false)){
                        makeToast(context, context.getString(R.string.toast_notice_import_succeeded));
                    } else {
                        makeToast(context, context.getString(R.string.toast_notice_import_failed));
                    }

                    Intent restartIntent = new Intent(context, MainActivity.class);
                    restartIntent.putExtra(EXTRA_FRAGMENT_TO_LOAD, MainActivity.getFragmentToLoad());

                    mMainActivity.get().finish();
                    mMainActivity.get().startActivity(restartIntent);

                    break;

            }
        }

    }
}
