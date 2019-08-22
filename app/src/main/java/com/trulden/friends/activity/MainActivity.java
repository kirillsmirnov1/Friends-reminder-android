package com.trulden.friends.activity;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.view.MenuItem;
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
    private static final int NEW_INTERACTION_REQUEST = 1;
    private static final int NEW_PERSON_REQUEST = 2;

    private BottomNavigationView mBottomNavigation;
    private FloatingActionsMenu mFabMenu;

    private FriendsViewModel mFriendsViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loadFragment(new ReminderFragment());
        mBottomNavigation = findViewById(R.id.bottom_navigation);
        mBottomNavigation.setOnNavigationItemSelectedListener(this);

        mFabMenu = findViewById(R.id.fab_main_activity);

        if(savedInstanceState == null) {
            findViewById(R.id.bottom_reminder).performClick();
        }

        mFriendsViewModel = ViewModelProviders.of(this).get(FriendsViewModel.class);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void addPerson(View view) {
        Intent intent = new Intent(this, AddPersonActivity.class);
        startActivityForResult(intent, NEW_PERSON_REQUEST);
        mFabMenu.collapse();
    }

    public void addMeeting(View view) { // TODO переименовать
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

            case NEW_PERSON_REQUEST:{
                if(resultCode == RESULT_OK) {
                    String name = resultingIntent.getStringExtra(AddPersonActivity.EXTRA_FRIEND_NAME);//TODO
                    String info = resultingIntent.getStringExtra(AddPersonActivity.EXTRA_FRIEND_INFO);

                    Friend friend = new Friend(name, info);

                    mFriendsViewModel.addFriend(friend);
                }
            }
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
        Fragment fragment = null;

        switch (menuItem.getItemId()){
            case R.id.bottom_log:
                fragment = new LogFragment();
                break;
            case R.id.bottom_reminder:
                fragment = new ReminderFragment();
                break;
            case R.id.bottom_persons:
                fragment = new FriendsFragment();
        }

        return loadFragment(fragment);
    }
}
