package com.trulden.friends.async;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.core.content.FileProvider;

import com.trulden.friends.util.ZipUtil;

import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.util.Objects;

import static com.trulden.friends.database.FriendsDatabase.DATABASE_NAME;
import static com.trulden.friends.util.Util.getInnerBackupFilePath;

public class ExportDatabaseAsyncTask extends AsyncTask<Bundle, Void, Void> {

    private WeakReference<Context> mContext;

    public ExportDatabaseAsyncTask(Context context){
        mContext = new WeakReference<>(context);
    }

    @Override
    protected Void doInBackground(Bundle... bundles) {

        String backupPath = getInnerBackupFilePath(mContext.get());
        ZipUtil.zip(getDbPaths(), backupPath);

        Uri uriDest = bundles[0].getParcelable("uriDest");

        File outputFile = new File(backupPath);

        Uri uriSrc = FileProvider.getUriForFile(mContext.get(),
                "com.trulden.friends.FileProvider", outputFile);

        try(InputStream inputStream = mContext.get().getContentResolver().openInputStream(uriSrc);
            OutputStream outputStream = mContext.get().getContentResolver().openOutputStream(Objects.requireNonNull(uriDest))){

            IOUtils.copy(Objects.requireNonNull(inputStream), outputStream);
            //makeToast(mContext.get(), "Export succeeded");

        } catch (Exception e) {
            e.printStackTrace();

            //makeToast(mContext.get(), "Export failed");
        }

        return null;
    }

    private String[] getDbPaths() {
        String dbPath = mContext.get().getDatabasePath(DATABASE_NAME).getAbsolutePath();
        return new String[]{dbPath, dbPath + "-wal", dbPath + "-shm"};
    }
}
