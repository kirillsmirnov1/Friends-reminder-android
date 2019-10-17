package com.trulden.friends.activity;

import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.trulden.friends.R;
import com.trulden.friends.activity.interfaces.SelectionHandler;
import com.trulden.friends.async.ExportDatabaseAsyncTask;
import com.trulden.friends.async.ImportDatabaseAsyncTask;
import com.trulden.friends.database.FriendsViewModel;
import com.trulden.friends.database.entity.Friend;
import com.trulden.friends.database.entity.Interaction;
import com.trulden.friends.util.CustomBroadcastReceiver;
import com.trulden.friends.util.Util;

import java.util.HashSet;

import static com.trulden.friends.database.FriendsDatabase.getDatabase;
import static com.trulden.friends.util.Util.*;

/**
 * Holds {@link InteractionsFragment}, {@link LastInteractionsFragment}, {@link FriendsFragment}.
 * Handles some interactions with database: export and import, queries.
 */
public class MainActivity
        extends AppCompatActivity
        implements BottomNavigationView.OnNavigationItemSelectedListener {

    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    private static FragmentToLoad mFragmentToLoad = FragmentToLoad.LAST_INTERACTIONS_FRAGMENT;
    private static boolean mShowHiddenLastInteractionEntries = false;

    private FloatingActionsMenu mFabMenu;
    private Toolbar mToolbar;

    private FriendsViewModel mFriendsViewModel;

    private CustomBroadcastReceiver mReceiver;
    private Fragment mFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mFriendsViewModel = ViewModelProviders.of(this).get(FriendsViewModel.class);

        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        setToolbarTitle();

        if(getIntent().getSerializableExtra(EXTRA_FRAGMENT_TO_LOAD) != null){
            mFragmentToLoad = (FragmentToLoad) getIntent().getSerializableExtra(EXTRA_FRAGMENT_TO_LOAD);
        }

        BottomNavigationView mBottomNavigation = findViewById(R.id.am_bottom_navigation);
        mBottomNavigation.setOnNavigationItemSelectedListener(this);

        mFabMenu = findViewById(R.id.am_fab);

        if(savedInstanceState == null) {
            switch (mFragmentToLoad){
                case INTERACTIONS_FRAGMENT:
                    findViewById(R.id.menu_bot_nav_interactions).performClick();
                    break;
                case LAST_INTERACTIONS_FRAGMENT:
                    findViewById(R.id.menu_bot_nav_last_interactions).performClick();
                    break;
                case FRIENDS_FRAGMENT:
                    findViewById(R.id.menu_bot_nav_friends).performClick();
                    break;
            }
        }

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
            case R.id.menu_main_export_database: {
                onClickExportDatabase();
                return true;
            }

            case R.id.menu_main_import_database: {
                onClickImportDatabase();
                return true;
            }

            case R.id.menu_main_interaction_types: {

                saveSelectedLastInteractionTab();

                Intent intent = new Intent(this, InteractionTypesActivity.class);
                startActivityForResult(intent, NO_REQUEST);
            }

            case R.id.menu_main_show_hidden_li: {
                mShowHiddenLastInteractionEntries = !item.isChecked();

                item.setChecked(mShowHiddenLastInteractionEntries);
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

        saveSelectedLastInteractionTab();

        if(mFragment instanceof SelectionHandler){
            ((SelectionHandler) mFragment ).finishActionMode();
        }

        Intent intent = new Intent(this, EditFriendActivity.class);
        startActivityForResult(intent, NEW_FRIEND_REQUEST);
        mFabMenu.collapse();
    }

    public void addInteraction(View view) {

        saveSelectedLastInteractionTab();

        if(mFragment instanceof SelectionHandler){
            ((SelectionHandler) mFragment ).finishActionMode();
        }

        Intent intent = new Intent(this, EditInteractionActivity.class);
        startActivityForResult(intent, NEW_INTERACTION_REQUEST);
        mFabMenu.collapse();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent resultingIntent) {
        super.onActivityResult(requestCode, resultCode, resultingIntent);

        if(mFragment instanceof LastInteractionsFragment) {
            ((LastInteractionsFragment) mFragment).retrieveSelectedTab();
        }

        // Always check resultingIntent for null.
        // When result is not ok, intent might be null

        switch (requestCode) {

            case NEW_INTERACTION_REQUEST: {
                if (resultCode == RESULT_OK && resultingIntent != null) {

                    // Getting data from intent

                    HashSet<Long> friendsIds = (HashSet<Long>)
                            resultingIntent.getSerializableExtra(EXTRA_INTERACTION_FRIEND_IDS);

                    mFriendsViewModel.add(getInteractionFromIntent(resultingIntent), friendsIds);
                }
                break;
            }

            case UPDATE_INTERACTION_REQUEST: {
                if (resultCode == RESULT_OK && resultingIntent != null) {
                    HashSet<Long> friendsIds = (HashSet<Long>)
                            resultingIntent.getSerializableExtra(EXTRA_INTERACTION_FRIEND_IDS);

                    mFriendsViewModel.update(getInteractionFromIntent(resultingIntent), friendsIds);
                }
                break;
            }

            case NEW_FRIEND_REQUEST:{
                if(resultCode == RESULT_OK && resultingIntent != null) {
                    mFriendsViewModel.add(getFriendFromIntent(resultingIntent));
                }
                break;
            }

            case UPDATE_FRIEND_REQUEST: {
                if(resultCode == RESULT_OK && resultingIntent != null) {
                        mFriendsViewModel.update(getFriendFromIntent(resultingIntent));
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

                    makeToast(this, getString(R.string.export_in_progress));
                    findViewById(R.id.am_progress_bar).setVisibility(View.VISIBLE);

                    getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                    new ExportDatabaseAsyncTask(this).execute(resultingIntent.getData());
                }
            }
        }
    }

    private Friend getFriendFromIntent(Intent resultingIntent) {
        long id = resultingIntent.getLongExtra(EXTRA_FRIEND_ID, -1);
        String name = resultingIntent.getStringExtra(EXTRA_FRIEND_NAME);
        String info = resultingIntent.getStringExtra(EXTRA_FRIEND_NOTES);

        assert name != null;

        return
            id == -1
            ? new Friend(name, info)
            : new Friend(id, name, info);
    }

    private Interaction getInteractionFromIntent(Intent resultingIntent) {

        long id = resultingIntent.getLongExtra(EXTRA_INTERACTION_ID, -1);
        long interactionTypeId = resultingIntent.getLongExtra(EXTRA_INTERACTION_TYPE_ID, -1);
        long date = resultingIntent.getLongExtra(EXTRA_INTERACTION_DATE, -1);
        String comment = resultingIntent.getStringExtra(EXTRA_INTERACTION_COMMENT);

        return
            id == -1
                ? new Interaction(interactionTypeId, date, comment)
                : new Interaction(id, interactionTypeId, date, comment);
    }

    private void importDatabaseFromUri(Uri uri) {

        makeSnackbar(findViewById(R.id.am_root_layout), getString(R.string.import_in_progress));
        findViewById(R.id.am_progress_bar).setVisibility(View.VISIBLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

        getDatabase(this).close();
        wipeDatabaseFiles(this);

        new ImportDatabaseAsyncTask(this).execute(uri);
    }

    private boolean loadFragment(FragmentToLoad fragmentToLoad){
        mFragment = null;
        mFragmentToLoad = fragmentToLoad;
        switch (fragmentToLoad){
            case INTERACTIONS_FRAGMENT:
                mFragment = new InteractionsFragment();
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
            case INTERACTIONS_FRAGMENT:
                mToolbar.setTitle(getString(R.string.log));
                break;
            case LAST_INTERACTIONS_FRAGMENT:
                mToolbar.setTitle(getString(R.string.last_interactions));
                break;
            case FRIENDS_FRAGMENT:
                mToolbar.setTitle(getString(R.string.friends));
                break;
        }
    }

    private boolean loadFragment(Fragment fragment){
        if (fragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.am_fragment_container, fragment)
                    .commit();
            return true;
        }
        return false;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()){

            case R.id.menu_bot_nav_interactions: {
                saveSelectedLastInteractionTab();
                return loadFragment(FragmentToLoad.INTERACTIONS_FRAGMENT);
            }
            case R.id.menu_bot_nav_last_interactions: {
                return loadFragment(FragmentToLoad.LAST_INTERACTIONS_FRAGMENT);
            }
            case R.id.menu_bot_nav_friends: {
                saveSelectedLastInteractionTab();
                return loadFragment(FragmentToLoad.FRIENDS_FRAGMENT);
            }
        }

        return false;
    }

    public enum FragmentToLoad{
        INTERACTIONS_FRAGMENT,
        LAST_INTERACTIONS_FRAGMENT,
        FRIENDS_FRAGMENT
    }

    public static FragmentToLoad getFragmentToLoad() {
        return mFragmentToLoad;
    }

    private void saveSelectedLastInteractionTab(){
        if(mFragment instanceof LastInteractionsFragment){
            ((LastInteractionsFragment)mFragment).saveSelectedTab();
        }
    }
}
