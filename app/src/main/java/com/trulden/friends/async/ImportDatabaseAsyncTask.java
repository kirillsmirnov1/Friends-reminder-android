package com.trulden.friends.async;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.trulden.friends.database.FriendsDatabase;
import com.trulden.friends.util.ZipUtil;

import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.util.Objects;

import static com.trulden.friends.database.FriendsDatabase.DATABASE_NAME;
import static com.trulden.friends.util.Util.ACTION_DATABASE_IMPORT_FINISHED;
import static com.trulden.friends.util.Util.EXTRA_IMPORT_RESULT;
import static com.trulden.friends.util.Util.getInnerBackupFilePath;

public class ImportDatabaseAsyncTask extends AsyncTask<Uri, Void, Boolean> {

    private WeakReference<Context> mContext;

    public ImportDatabaseAsyncTask(Context context){
        mContext = new WeakReference<>(context);
    }

    @Override
    protected Boolean doInBackground(Uri... uris) {

        String innerBackupFilePath = getInnerBackupFilePath(mContext.get());
        String databasePath = Objects.requireNonNull(mContext.get().getDatabasePath(DATABASE_NAME).getParentFile()).getAbsolutePath() + "/";

        File innerBackupFile = new File(innerBackupFilePath);

        try(InputStream inputStream = mContext.get().getContentResolver().openInputStream(uris[0]);
            OutputStream outputStream = new FileOutputStream(innerBackupFile)){

            IOUtils.copy(Objects.requireNonNull(inputStream), outputStream);

            ZipUtil.unzip(innerBackupFilePath, databasePath);

            FriendsDatabase.wipeDatabaseInstance();

            return true;

        } catch (Exception e){
            e.printStackTrace();
        }

        return false;
    }

    @Override
    protected void onPostExecute(Boolean importResult) {
        Intent broadcast = new Intent(ACTION_DATABASE_IMPORT_FINISHED);
        broadcast.putExtra(EXTRA_IMPORT_RESULT, importResult);
        LocalBroadcastManager.getInstance(mContext.get()).sendBroadcast(broadcast);
    }
}
