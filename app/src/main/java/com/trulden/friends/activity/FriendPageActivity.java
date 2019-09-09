package com.trulden.friends.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.trulden.friends.R;
import com.trulden.friends.database.entity.Friend;
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
                intent.getLongExtra(EXTRA_FRIEND_ID, -1),
                intent.getStringExtra(EXTRA_FRIEND_NAME),
                intent.getStringExtra(EXTRA_FRIEND_INFO));

        setFriendInfo(friend);

        mFriendsViewModel = ViewModelProviders.of(this).get(FriendsViewModel.class);
    }

    private void setFriendInfo(Friend friend){
        this.friend = friend;
        getSupportActionBar().setTitle(friend.getName());
        mPersonInfo.setText(friend.getInfo());
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
                Intent intent = new Intent(this, AddFriendActivity.class);

                intent.putExtra(EXTRA_FRIEND_ID, friend.getId());
                intent.putExtra(EXTRA_FRIEND_NAME, friend.getName());
                intent.putExtra(EXTRA_FRIEND_INFO, friend.getInfo());

                startActivityForResult(intent, UPDATE_FRIEND_REQUEST);
                break;
            }

            case R.id.delete_selection: {
                mFriendsViewModel.delete(friend);
                makeToast(this, friend.getName() + " deleted");
                finish();
                break;
            }
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent resultingIntent) {
        super.onActivityResult(requestCode, resultCode, resultingIntent);

        switch (requestCode){
            case UPDATE_FRIEND_REQUEST: {
                if(resultCode == RESULT_OK) {
                    long id = resultingIntent.getLongExtra(EXTRA_FRIEND_ID, -1);
                    if(id != -1){
                        String name = resultingIntent.getStringExtra(EXTRA_FRIEND_NAME);
                        String info = resultingIntent.getStringExtra(EXTRA_FRIEND_INFO);

                        Friend friend = new Friend(id, name, info);
                        setFriendInfo(friend);
                        mFriendsViewModel.update(friend);
                    }
                }
            }
        }
    }
}
