package com.trulden.friends.activity;

import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.view.Menu;
import android.view.View;
import android.view.MenuItem;
import android.view.WindowManager;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.trulden.friends.R;
import com.trulden.friends.async.ExportDatabaseAsyncTask;
import com.trulden.friends.async.ImportDatabaseAsyncTask;
import com.trulden.friends.database.entity.Friend;
import com.trulden.friends.database.FriendsViewModel;
import com.trulden.friends.database.entity.Interaction;
import com.trulden.friends.util.CustomBroadcastReceiver;
import com.trulden.friends.util.Util;

import java.util.Calendar;
import java.util.HashSet;

import static com.trulden.friends.database.FriendsDatabase.getDatabase;
import static com.trulden.friends.util.Util.*;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    private static FragmentToLoad mFragmentToLoad = FragmentToLoad.LAST_INTERACTIONS_FRAGMENT;

    private FloatingActionsMenu mFabMenu;
    private Toolbar mToolbar;

    private FriendsViewModel mFriendsViewModel;

    private CustomBroadcastReceiver mReceiver;

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
                case LAST_INTERACTIONS_FRAGMENT:
                    findViewById(R.id.bottom_last_interactions).performClick();
                    break;
                case FRIENDS_FRAGMENT:
                    findViewById(R.id.bottom_friends).performClick();
                    break;
            }
        }

        mFriendsViewModel = ViewModelProviders.of(this).get(FriendsViewModel.class);

        mReceiver = new CustomBroadcastReceiver(this);

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION_DATABASE_EXPORT_FINISHED);
        intentFilter.addAction(ACTION_DATABASE_IMPORT_FINISHED);

        LocalBroadcastManager.getInstance(this)
                .registerReceiver(mReceiver, intentFilter);
    }

    @Override
    protected void onDestroy() {

        LocalBroadcastManager.getInstance(this)
                .unregisterReceiver(mReceiver);

        super.onDestroy();
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
                onClickExportDatabase();
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

    private void onClickExportDatabase() {

        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);

        intent.addCategory(Intent.CATEGORY_OPENABLE);

        intent.setType("application/zip");

        intent.putExtra(Intent.EXTRA_TITLE, Util.generateBackupFileName());

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

                    // Getting data from intent

                    HashSet<String> friendNames = (HashSet<String>)
                            resultingIntent.getSerializableExtra(EXTRA_NEW_INTERACTION_FRIEND_NAMES);
                    HashSet<Long> friendsIds = (HashSet<Long>)
                            resultingIntent.getSerializableExtra(EXTRA_NEW_INTERACTION_FRIEND_IDS);
                    long interactionTypeId = resultingIntent.getLongExtra(EXTRA_NEW_INTERACTION_TYPE, -1);
                    long date = resultingIntent.getLongExtra(EXTRA_NEW_INTERACTION_DATE, -1);
                    String comment = resultingIntent.getStringExtra(EXTRA_NEW_INTERACTION_COMMENT);

                    Interaction interaction = new Interaction(
                            interactionTypeId, date,
                            comment, friendNames.toString().replaceAll("\\[|\\]", ""));

                    mFriendsViewModel.add(interaction, friendsIds);

                    // TODO update interaction
                }
                break;
            }

            case NEW_FRIEND_REQUEST:{
                if(resultCode == RESULT_OK && resultingIntent != null) {
                    String name = resultingIntent.getStringExtra(EXTRA_FRIEND_NAME);
                    String info = resultingIntent.getStringExtra(EXTRA_FRIEND_INFO);

                    assert name != null;
                    Friend friend = new Friend(name, info);
                    mFriendsViewModel.add(friend);
                }
                break;
            }

            case UPDATE_FRIEND_REQUEST: {
                if(resultCode == RESULT_OK && resultingIntent != null) {
                    long id = resultingIntent.getLongExtra(EXTRA_FRIEND_ID, -1);
                    if(id != -1){
                        String name = resultingIntent.getStringExtra(EXTRA_FRIEND_NAME);
                        String info = resultingIntent.getStringExtra(EXTRA_FRIEND_INFO);

                        assert name != null;
                        Friend friend = new Friend(id, name, info);
                        mFriendsViewModel.update(friend);
                    }
                }
                break;
            }

            case IMPORT_DATABASE_REQUEST: {
                if(resultCode == RESULT_OK){
                    if (resultingIntent != null) {
                        importDatabaseFromUri(resultingIntent.getData());
                    }
                }
                break;
            }

            case EXPORT_DATABASE_REQUEST:{
                if(resultCode == RESULT_OK && resultingIntent != null) {

                    makeSnackbar(findViewById(R.id.root_layout), "Export in progress");
                    findViewById(R.id.progress_bar_main).setVisibility(View.VISIBLE);

                    getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                    new ExportDatabaseAsyncTask(this).execute(resultingIntent.getData());
                }
            }
        }
    }

    private void importDatabaseFromUri(Uri uri) {

        makeSnackbar(findViewById(R.id.root_layout), "Import in progress");
        findViewById(R.id.progress_bar_main).setVisibility(View.VISIBLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

        getDatabase(this).close();
        wipeDatabase(this);

        new ImportDatabaseAsyncTask(this).execute(uri);
    }

    private boolean loadFragment(FragmentToLoad fragmentToLoad){
        Fragment mFragment = null;
        mFragmentToLoad = fragmentToLoad;
        switch (fragmentToLoad){
            case LOG_FRAGMENT:
                mFragment = new LogFragment();
                break;
            case LAST_INTERACTIONS_FRAGMENT:
                mFragment = new LastInteractionsFragment();
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
            case LAST_INTERACTIONS_FRAGMENT:
                mToolbar.setTitle("Last Interactions");
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
            case R.id.bottom_last_interactions:
                return loadFragment(FragmentToLoad.LAST_INTERACTIONS_FRAGMENT);
            case R.id.bottom_friends:
                return loadFragment(FragmentToLoad.FRIENDS_FRAGMENT);
        }

        return false;
    }

    public enum FragmentToLoad{
        LOG_FRAGMENT,
        LAST_INTERACTIONS_FRAGMENT,
        FRIENDS_FRAGMENT
    }

    public static FragmentToLoad getFragmentToLoad() {
        return mFragmentToLoad;
    }
}
