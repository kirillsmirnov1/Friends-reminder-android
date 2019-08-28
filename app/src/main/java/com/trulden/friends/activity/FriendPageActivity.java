package com.trulden.friends.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.trulden.friends.R;
import com.trulden.friends.database.Friend;
import com.trulden.friends.database.FriendsViewModel;

import static com.trulden.friends.util.Util.*;

public class FriendPageActivity extends AppCompatActivity {

    private TextView mPersonInfo;

    private Friend friend;

    private FriendsViewModel mFriendsViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_page);

        mPersonInfo = findViewById(R.id.friend_page_info);

        Intent intent = getIntent();

        friend = new Friend(
                intent.getIntExtra(EXTRA_FRIEND_ID, -1),
                intent.getStringExtra(EXTRA_FRIEND_NAME),
                intent.getStringExtra(EXTRA_FRIEND_INFO));

        getSupportActionBar().setTitle(friend.getName());
        mPersonInfo.setText(friend.getInfo());

        mFriendsViewModel = ViewModelProviders.of(this).get(FriendsViewModel.class);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.selection_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch(item.getItemId()){
            case R.id.edit_selection: {
                // TODO
                break;
            }

            case R.id.delete_selection: {
                mFriendsViewModel.deleteFriend(friend);
                Toast.makeText(this, friend.getName() + " deleted", Toast.LENGTH_SHORT).show();
                finish();
                break;
            }
        }

        return super.onOptionsItemSelected(item);
    }
}
