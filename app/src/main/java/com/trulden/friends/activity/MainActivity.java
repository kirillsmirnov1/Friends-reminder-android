package com.trulden.friends.activity;

import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.michaelflisar.changelog.ChangelogBuilder;
import com.michaelflisar.changelog.internal.ChangelogPreferenceUtil;
import com.trulden.friends.BuildConfig;
import com.trulden.friends.R;
import com.trulden.friends.activity.interfaces.RecyclerViewContainer;
import com.trulden.friends.activity.interfaces.SelectionHandler;
import com.trulden.friends.activity.interfaces.TrackerOverActivity;
import com.trulden.friends.async.ExportDatabaseAsyncTask;
import com.trulden.friends.async.ImportDatabaseAsyncTask;
import com.trulden.friends.database.FriendsDatabase;
import com.trulden.friends.database.FriendsViewModel;
import com.trulden.friends.database.entity.Friend;
import com.trulden.friends.database.entity.Interaction;
import com.trulden.friends.database.entity.LastInteraction;
import com.trulden.friends.database.wrappers.LastInteractionWrapper;
import com.trulden.friends.util.CustomBroadcastReceiver;
import com.trulden.friends.util.Util;

import java.util.Calendar;
import java.util.HashSet;

import static androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO;
import static androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES;
import static com.trulden.friends.database.FriendsDatabase.getDatabase;
import static com.trulden.friends.util.Util.*;
import static com.trulden.friends.util.ViewUtil.hideView;
import static com.trulden.friends.util.ViewUtil.showView;

/**
 * Holds {@link InteractionsFragment}, {@link LastInteractionsFragment}, {@link FriendsFragment}.
 * Handles some interactions with database: export and import, queries.
 */
public class MainActivity
        extends AppCompatActivity
        implements
            BottomNavigationView.OnNavigationItemSelectedListener,
            TrackerOverActivity {

    private static FragmentToLoad mFragmentToLoad = FragmentToLoad.LAST_INTERACTIONS_FRAGMENT;
    private boolean mTrackerOverShown = false;

    private static final String SHOW_HIDDEN_LAST_INTERACTION_ENTRIES = "SHOW_HIDDEN_LAST_INTERACTION_ENTRIES";
    private static final String NIGHT_MODE = "Night mode";

    private FloatingActionsMenu mFabMenu;
    private Toolbar mToolbar;
    private FrameLayout mTrackerOverLayout;

    private SharedPreferences mPreferences;

    private FriendsViewModel mViewModel;

    private CustomBroadcastReceiver mReceiver;
    private Fragment mFragment;
    private TrackerFragment mTrackerOverFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mPreferences = getSharedPreferences(BuildConfig.APPLICATION_ID, MODE_PRIVATE);

        mViewModel = ViewModelProviders.of(this).get(FriendsViewModel.class);

        checkLastInteractionsReadiness(getIntent());

        mViewModel.setShowHiddenLI(
                mPreferences.getBoolean(SHOW_HIDDEN_LAST_INTERACTION_ENTRIES, false));

        mViewModel.setNightMode(mPreferences.getBoolean(NIGHT_MODE, false));

        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        setToolbarTitle();

        mToolbar.setPopupTheme(R.style.AppTheme_PopupMenu);

        mTrackerOverLayout = findViewById(R.id.am_tracker_over_layout);

        if(getIntent().getSerializableExtra(EXTRA_FRAGMENT_TO_LOAD) != null){
            mFragmentToLoad = (FragmentToLoad) getIntent().getSerializableExtra(EXTRA_FRAGMENT_TO_LOAD);
        }

        ((BottomNavigationView) findViewById(R.id.am_bottom_navigation))
            .setOnNavigationItemSelectedListener(this);

        mFabMenu = findViewById(R.id.am_fab);

        if(savedInstanceState == null) {
            switch (mFragmentToLoad){
                case INTERACTIONS_FRAGMENT:
                    findViewById(R.id.mbn_interactions).performClick();
                    break;
                case LAST_INTERACTIONS_FRAGMENT:
                    findViewById(R.id.mbn_last_interactions).performClick();
                    break;
                case FRIENDS_FRAGMENT:
                    findViewById(R.id.mbn_friends).performClick();
                    break;
            }
        }

        mReceiver = new CustomBroadcastReceiver(this);

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION_DATABASE_EXPORT_FINISHED);
        intentFilter.addAction(ACTION_DATABASE_IMPORT_FINISHED);

        LocalBroadcastManager.getInstance(this)
                .registerReceiver(mReceiver, intentFilter);

        mViewModel.getNightMode().observe(this, nightMode ->
                AppCompatDelegate.setDefaultNightMode(
                        nightMode ? MODE_NIGHT_YES : MODE_NIGHT_NO
                ));

        mViewModel.getSelectionModeActivated().observe(this, selectionMode -> {
                    if (selectionMode)
                        hideView(mFabMenu);
                    else
                        showView(mFabMenu);
                }
        );

        checkIfTrackerFragmentNeedsToBeShown();

        showChangelog();
    }

    private void checkLastInteractionsReadiness(Intent intent) {

        // Used only here, so there is no need to move it to Util
        String key = "LAST_TIME_LAST_INTERACTIONS_CHECKED_FOR_READINESS";

        long lastTime = mPreferences.getLong(key, -1);

        if(lastTime == -1 || Util.daysPassed(lastTime) > 0 || intent.hasExtra(EXTRA_CHECK_READINESS_AFTER_IMPORT)){
            mViewModel.checkLastInteractionsReadiness();
        }
        mPreferences.edit().putLong(key, Calendar.getInstance().getTimeInMillis()).apply();
    }

    @Override
    public void checkIfTrackerFragmentNeedsToBeShown(){
        int fragmentsId = R.id.am_tracker_over_layout;

        if(getSupportFragmentManager().findFragmentById(fragmentsId) != null){
            mTrackerOverShown = true;
            setTrackerOverActivityVisibility(View.VISIBLE);
            mTrackerOverFragment = (TrackerFragment) getSupportFragmentManager().findFragmentById(fragmentsId);
        }
    }

    private void showChangelog() {

        // If changelog was never shown — show it
        if(ChangelogPreferenceUtil.getAlreadyShownChangelogVersion(this) == -1){
            new ChangelogBuilder()
                    .buildAndShowDialog(this, false);
        }

        // Show changelog on version change
        new ChangelogBuilder()
                .withManagedShowOnStart(true)
                .buildAndShowDialog(this, false);
    }

    @Override
    protected void onPause() {
        mPreferences
            .edit()
            .putBoolean(SHOW_HIDDEN_LAST_INTERACTION_ENTRIES, mViewModel.getShowHiddenLIValue())
            .putBoolean(NIGHT_MODE, mViewModel.getNightModeValue())
            .apply();

        super.onPause();
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
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.mm_show_hidden_li).setChecked(mViewModel.getShowHiddenLIValue());
        menu.findItem(R.id.mm_show_hidden_li).setVisible(mFragmentToLoad == FragmentToLoad.LAST_INTERACTIONS_FRAGMENT);

        menu.findItem(R.id.mm_night_mode).setChecked(mViewModel.getNightModeValue());

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        switch (item.getItemId()) {
            case R.id.mm_export_database: {
                onClickExportDatabase();
                return true;
            }

            case R.id.mm_import_database: {
                onClickImportDatabase();
                return true;
            }

            case R.id.mm_interaction_types: {

                saveSelectedLastInteractionTab();

                Intent intent = new Intent(this, InteractionTypesActivity.class);
                startActivityForResult(intent, NO_REQUEST);
                return true;
            }

            case R.id.mm_show_hidden_li: {
                boolean checkedStatus = !item.isChecked();

                mViewModel.setShowHiddenLI(checkedStatus);
                item.setChecked(checkedStatus);

                return true;
            }

            case R.id.mm_night_mode: {
                boolean checkedStatus = !item.isChecked();

                mViewModel.setNightMode(checkedStatus);
                item.setChecked(checkedStatus);

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

        saveSelectedLastInteractionTab();

        if(mFragment instanceof SelectionHandler){
            ((SelectionHandler) mFragment ).finishActionMode();
        }

        Intent intent = new Intent(this, EditFriendActivity.class);
        startActivityForResult(intent, NEW_FRIEND_REQUEST);
        mFabMenu.collapse();
    }

    public void onAddInteractionClick(View view) {

        //saveSelectedLastInteractionTab();
        int tabPos= mViewModel.getSelectedLITabPos();

        if(mFragment instanceof SelectionHandler){
            ((SelectionHandler) mFragment ).finishActionMode();
        }

        Intent intent = new Intent(this, EditInteractionActivity.class);

        intent.putExtra(EXTRA_INTERACTION_TYPE_POS, tabPos);

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

                    @SuppressWarnings("unchecked")
                    HashSet<Long> friendsIds = (HashSet<Long>)
                            resultingIntent.getSerializableExtra(EXTRA_INTERACTION_FRIEND_IDS);

                    mViewModel.add(getInteractionFromIntent(resultingIntent), friendsIds);
                }
                break;
            }

            case UPDATE_INTERACTION_REQUEST: {
                if (resultCode == RESULT_OK && resultingIntent != null) {

                    @SuppressWarnings("unchecked")
                    HashSet<Long> friendsIds = (HashSet<Long>)
                            resultingIntent.getSerializableExtra(EXTRA_INTERACTION_FRIEND_IDS);

                    mViewModel.update(getInteractionFromIntent(resultingIntent), friendsIds);
                }
                break;
            }

            case NEW_FRIEND_REQUEST:{
                if(resultCode == RESULT_OK && resultingIntent != null) {
                    mViewModel.add(getFriendFromIntent(resultingIntent));
                }
                break;
            }

            case UPDATE_FRIEND_REQUEST: {
                if(resultCode == RESULT_OK && resultingIntent != null) {
                        mViewModel.update(getFriendFromIntent(resultingIntent));
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

                    FriendsDatabase.closeDatabase();

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
                mToolbar.setTitle(getString(R.string.interactions_journal));
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

            case R.id.mbn_interactions: {
                if(mFragmentToLoad == FragmentToLoad.INTERACTIONS_FRAGMENT
                    && mFragment != null){
                    ((RecyclerViewContainer)mFragment).scrollUp();
                    return true;
                } else {
                    saveSelectedLastInteractionTab();
                    return loadFragment(FragmentToLoad.INTERACTIONS_FRAGMENT);
                }
            }
            case R.id.mbn_last_interactions: {
                if(mFragmentToLoad == FragmentToLoad.LAST_INTERACTIONS_FRAGMENT
                   && mFragment != null){ // On first load it is null
                    ((RecyclerViewContainer)mFragment).scrollUp();
                    return true;
                } else {
                    return loadFragment(FragmentToLoad.LAST_INTERACTIONS_FRAGMENT);
                }
            }
            case R.id.mbn_friends: {
                if(mFragmentToLoad == FragmentToLoad.FRIENDS_FRAGMENT
                    && mFragment != null) {
                    ((RecyclerViewContainer)mFragment).scrollUp();
                    return true;
                } else {
                    saveSelectedLastInteractionTab();
                    return loadFragment(FragmentToLoad.FRIENDS_FRAGMENT);
                }
            }
        }

        return false;
    }

    @Override
    public void showTrackerOverActivity(LastInteractionWrapper lastInteractionWrapper) {

        mTrackerOverShown = true;

        mTrackerOverFragment = TrackerFragment
            .newInstance(
                lastInteractionWrapper.getType().getId(),
                lastInteractionWrapper.getFriend().getId());

        getSupportFragmentManager()
            .beginTransaction()
            .replace(R.id.am_tracker_over_layout, mTrackerOverFragment)
            .commit();

        setTrackerOverActivityVisibility(View.VISIBLE);
    }

    @Override
    public void setTrackerOverActivityVisibility(int visibility){
        findViewById(R.id.am_fade_background).setVisibility(visibility);
        mTrackerOverLayout.setVisibility(visibility);
    }

    @Override
    public void updateLastInteraction(LastInteraction lastInteraction) {
        mViewModel.update(lastInteraction);
    }

    @Override
    public void closeTrackerOverActivity() {
        saveSelectedLastInteractionTab();
        setTrackerOverActivityVisibility(View.GONE);
        mTrackerOverShown = false;
        getSupportFragmentManager()
                .beginTransaction()
                .remove(mTrackerOverFragment)
                .commit();

        mViewModel.setTrackerInFragment(null);
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

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {

        if(mTrackerOverShown){
            Rect outRect = new Rect();
            mTrackerOverLayout.getGlobalVisibleRect(outRect);
            if(!outRect.contains((int)ev.getRawX(), (int)ev.getRawY())){
                closeTrackerOverActivity();
                return true;
            } else {
                return super.dispatchTouchEvent(ev);
            }

        }

        // Hide fab if touch event outside of it
        // (Other cases handled by gab itself)
        // Based on https://github.com/futuresimple/android-floating-action-button/issues/204#issuecomment-158073034

        if (mFabMenu.isExpanded()) {

            Rect outRect = new Rect();
            mFabMenu.getGlobalVisibleRect(outRect);

            if(!outRect.contains((int)ev.getRawX(), (int)ev.getRawY()))
                mFabMenu.collapse();
        }

        return super.dispatchTouchEvent(ev);
    }

    @Override
    public void onBackPressed() {
        if(mTrackerOverShown){

            closeTrackerOverActivity();

            return;
        }
        super.onBackPressed();
    }
}
