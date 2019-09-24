package com.trulden.friends.async;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;

import androidx.core.content.FileProvider;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.trulden.friends.util.ZipUtil;

import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.util.Objects;

import static com.trulden.friends.util.Util.ACTION_DATABASE_EXPORT_FINISHED;
import static com.trulden.friends.util.Util.EXTRA_EXPORT_RESULT;
import static com.trulden.friends.util.Util.getDbPaths;
import static com.trulden.friends.util.Util.getInnerBackupFilePath;

/**
 * Async task to export database.
 * Takes uri of file and writes database there.
 * Returns false if something failed
 */
public class ExportDatabaseAsyncTask extends AsyncTask<Uri, Void, Boolean> {

    private WeakReference<Context> mContext;

    public ExportDatabaseAsyncTask(Context context){
        mContext = new WeakReference<>(context);
    }

    @Override
    protected Boolean doInBackground(Uri... uris) {

        String backupPath = getInnerBackupFilePath(mContext.get());

        // First — archive database

        ZipUtil.zip(getDbPaths(mContext.get()), backupPath);

        Uri uriDest = uris[0];

        File outputFile = new File(backupPath);

        Uri uriSrc = FileProvider.getUriForFile(mContext.get(),
                "com.trulden.friends.FileProvider", outputFile);

        // Second — write archive to specified location

        try(InputStream inputStream = mContext.get().getContentResolver().openInputStream(uriSrc);
            OutputStream outputStream = mContext.get().getContentResolver().openOutputStream(Objects.requireNonNull(uriDest))){

            IOUtils.copy(Objects.requireNonNull(inputStream), outputStream);

            return true;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    @Override
    protected void onPostExecute(Boolean exportResult) {
        Intent broadcast = new Intent(ACTION_DATABASE_EXPORT_FINISHED);
        broadcast.putExtra(EXTRA_EXPORT_RESULT, exportResult);
        LocalBroadcastManager.getInstance(mContext.get()).sendBroadcast(broadcast);
    }
}
