package com.trulden.friends.activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.PreferenceFragmentCompat;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.WindowManager;

import com.trulden.friends.R;
import com.trulden.friends.async.ExportDatabaseAsyncTask;
import com.trulden.friends.async.ImportDatabaseAsyncTask;
import com.trulden.friends.database.FriendsDatabase;
import com.trulden.friends.util.Util;

import static androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO;
import static androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES;
import static com.trulden.friends.util.Util.EXPORT_DATABASE_REQUEST;
import static com.trulden.friends.util.Util.IMPORT_DATABASE_REQUEST;
import static com.trulden.friends.util.Util.makeToast;
import static com.trulden.friends.util.Util.wipeDatabaseFiles;

/**
 * Contains app settings
 */
public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportFragmentManager().beginTransaction()
            .replace(android.R.id.content, new SettingsFragment())
            .commit();
    }

    public static class SettingsFragment extends PreferenceFragmentCompat {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.preferences, rootKey);

            findPreference(getString(R.string.preference_night_mode))
                    .setOnPreferenceChangeListener((preference, newValue) -> {

                        AppCompatDelegate.setDefaultNightMode(
                            (boolean)newValue ? MODE_NIGHT_YES : MODE_NIGHT_NO);

                        return true;
                    });

            findPreference(getString(R.string.preference_export))
                    .setOnPreferenceClickListener(preference -> {
                        onClickExportDatabase();
                        return true;
                    });

            findPreference(getString(R.string.preference_import))
                    .setOnPreferenceClickListener(preference -> {
                        onClickImportDatabase();
                        return true;
                    });
        }

        private void onClickImportDatabase() {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("application/zip");
            startActivityForResult(intent, IMPORT_DATABASE_REQUEST);
        }

        private void onClickExportDatabase() {
            Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("application/zip");
            intent.putExtra(Intent.EXTRA_TITLE, Util.generateBackupFileName());
            startActivityForResult(intent, EXPORT_DATABASE_REQUEST);
        }

        @Override
        public void onActivityResult(int requestCode, int resultCode, @Nullable Intent resultingIntent) {
            super.onActivityResult(requestCode, resultCode, resultingIntent);

            switch (requestCode){
                case IMPORT_DATABASE_REQUEST: {
                    if(resultCode == RESULT_OK && resultingIntent != null) {
                        importDatabase(resultingIntent.getData());
                    }
                    break;
                }

                case EXPORT_DATABASE_REQUEST:{
                    if(resultCode == RESULT_OK && resultingIntent != null) {
                        exportDatabase(resultingIntent.getData());
                    }
                    break;
                }
            }
        }

        private void exportDatabase(Uri data) {
            makeToast(getActivity(), getString(R.string.export_in_progress));
            // TODO add progress bar to settings
            //getActivity().findViewById(R.id.am_progress_bar).setVisibility(View.VISIBLE);

            getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

            FriendsDatabase.closeDatabase();

            new ExportDatabaseAsyncTask(getActivity()).execute(data);
        }

        private void importDatabase(Uri data) {
            makeToast(getActivity(), getString(R.string.import_in_progress));

            //getActivity().findViewById(R.id.am_progress_bar).setVisibility(View.VISIBLE);
            getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

            FriendsDatabase.closeDatabase();
            wipeDatabaseFiles(getActivity());

            new ImportDatabaseAsyncTask(getActivity()).execute(data);
        }
    }
}
