package com.trulden.friends.activity;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.MenuItem;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;
import com.trulden.friends.R;
import com.trulden.friends.database.Friend;
import com.trulden.friends.database.FriendsViewModel;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    private static final int NEW_INTERACTION_REQUEST    = 1;
    private static final int NEW_FRIEND_REQUEST         = 2;
    public  static final int UPDATE_FRIEND_REQUEST      = 3;

    private static FragmentToLoad mFragmentToLoad = FragmentToLoad.REMINDER_FRAGMENT;

    private BottomNavigationView mBottomNavigation;
    private FloatingActionsMenu mFabMenu;
    private Toolbar mToolbar;

    private FriendsViewModel mFriendsViewModel;
    private Fragment mFragment = null;

    private static boolean once = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        setToolbarTitle();

        if(once) {
            loadFragment(mFragmentToLoad);
            once = false;
        }

        mBottomNavigation = findViewById(R.id.bottom_navigation);
        mBottomNavigation.setOnNavigationItemSelectedListener(this);

        mFabMenu = findViewById(R.id.fab_main_activity);

        if(savedInstanceState == null) {
            findViewById(R.id.bottom_reminder).performClick();
        }

        mFriendsViewModel = ViewModelProviders.of(this).get(FriendsViewModel.class);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        //MenuInflater menuInflater = getMenuInflater();
        //menuInflater.inflate(R.menu.selection_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if(mFragment instanceof FragmentWithSelection){

            if(id == R.id.edit_selection) {
                ((FragmentWithSelection) mFragment).editSelection();
            }

            if(id == R.id.delete_selection){
                ((FragmentWithSelection) mFragment).deleteSelection();
            }
        }

        return super.onOptionsItemSelected(item);
    }

    public void addFriend(View view) {
        Intent intent = new Intent(this, AddFriendActivity.class);
        startActivityForResult(intent, NEW_FRIEND_REQUEST);
        mFabMenu.collapse();
    }

    public void addInteraction(View view) {
        Intent intent = new Intent(this, AddInteractionActivity.class);
        startActivityForResult(intent, NEW_INTERACTION_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent resultingIntent) {
        super.onActivityResult(requestCode, resultCode, resultingIntent);

        switch (requestCode) {

            case NEW_INTERACTION_REQUEST: {
                if (resultCode == RESULT_OK) {
                    String reply = resultingIntent.getStringExtra(AddInteractionActivity.EXTRA_NEW_INTERACTION);
                    // TODO save as log entry
                }
                break;
            }

            case NEW_FRIEND_REQUEST:{
                if(resultCode == RESULT_OK) {
                    String name = resultingIntent.getStringExtra(AddFriendActivity.EXTRA_FRIEND_NAME);
                    String info = resultingIntent.getStringExtra(AddFriendActivity.EXTRA_FRIEND_INFO);

                    Friend friend = new Friend(name, info);

                    mFriendsViewModel.addFriend(friend);
                }
            }

            case UPDATE_FRIEND_REQUEST: {
                if(resultCode == RESULT_OK) {
                    int id = resultingIntent.getIntExtra(AddFriendActivity.EXTRA_FRIEND_ID, -1);
                    if(id != -1){
                        String name = resultingIntent.getStringExtra(AddFriendActivity.EXTRA_FRIEND_NAME);
                        String info = resultingIntent.getStringExtra(AddFriendActivity.EXTRA_FRIEND_INFO);

                        Friend friend = new Friend(id, name, info);
                        mFriendsViewModel.updateFriend(friend);
                    }
                }
            }
        }
    }

    private boolean loadFragment(FragmentToLoad fragmentToLoad){
        mFragment = null;
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

    private enum FragmentToLoad{
        LOG_FRAGMENT,
        REMINDER_FRAGMENT,
        FRIENDS_FRAGMENT
    }
}
