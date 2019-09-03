package com.trulden.friends.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.MenuItem;

import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.trulden.friends.R;
import com.trulden.friends.database.Friend;
import com.trulden.friends.database.FriendsDatabase;
import com.trulden.friends.database.FriendsViewModel;
import com.trulden.friends.util.ZipUtil;

import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Objects;

import static com.trulden.friends.database.FriendsDatabase.DATABASE_NAME;
import static com.trulden.friends.database.FriendsDatabase.getDatabase;
import static com.trulden.friends.util.Util.*;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    private static FragmentToLoad mFragmentToLoad = FragmentToLoad.REMINDER_FRAGMENT;

    private FloatingActionsMenu mFabMenu;
    private Toolbar mToolbar;

    private FriendsViewModel mFriendsViewModel;

    public static FragmentToLoad getFragmentToLoad() {
        return mFragmentToLoad;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        setToolbarTitle();

        if(getIntent().getSerializableExtra(EXTRA_FRAGMENT_TO_LOAD) != null){
            mFragmentToLoad = (FragmentToLoad) getIntent().getSerializableExtra(EXTRA_FRAGMENT_TO_LOAD);
        }

        BottomNavigationView mBottomNavigation = findViewById(R.id.bottom_navigation);
        mBottomNavigation.setOnNavigationItemSelectedListener(this);

        mFabMenu = findViewById(R.id.fab_main_activity);

        if(savedInstanceState == null) {
            switch (mFragmentToLoad){
                case LOG_FRAGMENT:
                    findViewById(R.id.bottom_log).performClick();
                    break;
                case REMINDER_FRAGMENT:
                    findViewById(R.id.bottom_reminder).performClick();
                    break;
                case FRIENDS_FRAGMENT:
                    findViewById(R.id.bottom_friends).performClick();
                    break;
            }
        }

        mFriendsViewModel = ViewModelProviders.of(this).get(FriendsViewModel.class);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_main, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        switch (item.getItemId()) {
            case R.id.action_export_database: {
                exportDatabase();
                return true;
            }

            case R.id.action_import_database: {
                onClickImportDatabase();
                return true;
            }

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void onClickImportDatabase() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("application/zip");
        startActivityForResult(intent, IMPORT_DATABASE_REQUEST);
    }

    private void exportDatabase() {

        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);

        intent.addCategory(Intent.CATEGORY_OPENABLE);

        intent.setType("application/zip");
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        // TODO specify db version, date and time of backup
        intent.putExtra(Intent.EXTRA_TITLE, "friends_database.zip");

        startActivityForResult(intent, EXPORT_DATABASE_REQUEST);
    }

    public void addFriend(View view) {
        Intent intent = new Intent(this, AddFriendActivity.class);
        startActivityForResult(intent, NEW_FRIEND_REQUEST);
        mFabMenu.collapse();
    }

    public void addInteraction(View view) {
        Intent intent = new Intent(this, AddInteractionActivity.class);
        startActivityForResult(intent, NEW_INTERACTION_REQUEST);
        mFabMenu.collapse();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent resultingIntent) {
        super.onActivityResult(requestCode, resultCode, resultingIntent);

        switch (requestCode) {

            case NEW_INTERACTION_REQUEST: {
                if (resultCode == RESULT_OK && resultingIntent != null) {
                    String reply = resultingIntent.getStringExtra(EXTRA_NEW_INTERACTION);
                    // TODO save as log entry
                }
                break;
            }

            case NEW_FRIEND_REQUEST:{
                if(resultCode == RESULT_OK && resultingIntent != null) {
                    String name = resultingIntent.getStringExtra(EXTRA_FRIEND_NAME);
                    String info = resultingIntent.getStringExtra(EXTRA_FRIEND_INFO);

                    assert name != null;
                    Friend friend = new Friend(name, info);
                    mFriendsViewModel.addFriend(friend);
                }
                break;
            }

            case UPDATE_FRIEND_REQUEST: {
                if(resultCode == RESULT_OK && resultingIntent != null) {
                    int id = resultingIntent.getIntExtra(EXTRA_FRIEND_ID, -1);
                    if(id != -1){
                        String name = resultingIntent.getStringExtra(EXTRA_FRIEND_NAME);
                        String info = resultingIntent.getStringExtra(EXTRA_FRIEND_INFO);

                        assert name != null;
                        Friend friend = new Friend(id, name, info);
                        mFriendsViewModel.updateFriend(friend);
                    }
                }
                break;
            }

            case IMPORT_DATABASE_REQUEST: {
                if(resultCode == RESULT_OK){
                    Uri uri;
                    if (resultingIntent != null) {
                        uri = resultingIntent.getData();
                        assert uri != null;
                        Log.i(LOG_TAG, "Uri: " + uri.toString());
                        importDatabaseFromUri(uri);
                    }
                }
                break;
            }

            case EXPORT_DATABASE_REQUEST:{
                if(resultCode == RESULT_OK && resultingIntent != null) {

                    getDatabase(this).close();

                    // TODO zip in another thread
                    String backupPath = getInnerBackupFilePath(this);
                    ZipUtil.zip(getDbPaths(), backupPath);

                    Uri uriDest = resultingIntent.getData();

                    File outputFile = new File(backupPath);

                    Uri uriSrc = FileProvider.getUriForFile(this,
                            "com.trulden.friends.FileProvider", outputFile);

                    try(InputStream inputStream = getContentResolver().openInputStream(uriSrc);
                        OutputStream outputStream = getContentResolver().openOutputStream(Objects.requireNonNull(uriDest))){

                        IOUtils.copy(Objects.requireNonNull(inputStream), outputStream);
                        makeToast(this, "Export succeeded");

                    } catch (Exception e) {
                        e.printStackTrace();

                        makeToast(this, "Export failed");
                    }
                }
            }
        }
    }

    private void importDatabaseFromUri(Uri uri) {

        String innerBackupFilePath = getInnerBackupFilePath(this);
        String databasePath = Objects.requireNonNull(getDatabasePath(DATABASE_NAME).getParentFile()).getAbsolutePath() + "/";

        File innerBackupFile = new File(innerBackupFilePath);

        // TODO run filework in another thread
        try(InputStream inputStream = getContentResolver().openInputStream(uri);
            OutputStream outputStream = new FileOutputStream(innerBackupFile)){

            IOUtils.copy(Objects.requireNonNull(inputStream), outputStream);

            getDatabase(this).close();
            wipeDatabase();

            ZipUtil.unzip(innerBackupFilePath, databasePath);

            FriendsDatabase.wipeDatabaseInstance();

            makeToast(this, "Import succeeded");

            // TODO add fragmentToLoad

            Intent restartIntent = new Intent(this, MainActivity.class);
            restartIntent.putExtra(EXTRA_FRAGMENT_TO_LOAD, mFragmentToLoad);

            finish();
            startActivity(restartIntent);

        } catch (Exception e){
            e.printStackTrace();
            makeToast(this, "Import failed");
        }

    }

    private void wipeDatabase() {
        String[] dbPaths = getDbPaths();
        for(String str : dbPaths){
            if(! new File(str).delete()){
                Log.e(LOG_TAG, "Error wiping database");
            }
        }
    }

    private String[] getDbPaths() {
        String dbPath = getDatabasePath(DATABASE_NAME).getAbsolutePath();
        return new String[]{dbPath, dbPath + "-wal", dbPath + "-shm"};
    }

    private boolean loadFragment(FragmentToLoad fragmentToLoad){
        Fragment mFragment = null;
        mFragmentToLoad = fragmentToLoad;
        switch (fragmentToLoad){
            case LOG_FRAGMENT:
                mFragment = new LogFragment();
                break;
            case REMINDER_FRAGMENT:
                mFragment = new ReminderFragment();
                break;
            case FRIENDS_FRAGMENT:
                mFragment = new FriendsFragment();
                break;
        }
        setToolbarTitle();
        return loadFragment(mFragment);
    }

    public void setToolbarTitle(){
        switch (mFragmentToLoad){
            case LOG_FRAGMENT:
                mToolbar.setTitle("Log");
                break;
            case REMINDER_FRAGMENT:
                mToolbar.setTitle("Reminder");
                break;
            case FRIENDS_FRAGMENT:
                mToolbar.setTitle("Friends");
                break;
        }
    }

    private boolean loadFragment(Fragment fragment){
        if (fragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .commit();
            return true;
        }
        return false;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()){
            case R.id.bottom_log:
                return loadFragment(FragmentToLoad.LOG_FRAGMENT);
            case R.id.bottom_reminder:
                return loadFragment(FragmentToLoad.REMINDER_FRAGMENT);
            case R.id.bottom_friends:
                return loadFragment(FragmentToLoad.FRIENDS_FRAGMENT);
        }

        return false;
    }

    public enum FragmentToLoad{
        LOG_FRAGMENT,
        REMINDER_FRAGMENT,
        FRIENDS_FRAGMENT
    }
}
