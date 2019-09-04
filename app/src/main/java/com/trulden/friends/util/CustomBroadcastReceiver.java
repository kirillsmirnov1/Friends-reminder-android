package com.trulden.friends.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import static com.trulden.friends.util.Util.*;

public class CustomBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        String intentAction = intent.getAction();

        if(intentAction != null){
            switch (intentAction) {
                case ACTION_DATABASE_EXPORT_FINISHED:
                    if(intent.getBooleanExtra(EXTRA_EXPORT_RESULT, false)){
                        makeToast(context, "Export succeeded");
                    } else {
                        makeToast(context, "Export failed");
                    }
                    // TODO progress bar
                    break;
            }
        }

    }
}
