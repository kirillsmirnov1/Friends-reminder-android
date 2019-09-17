package com.trulden.friends.activity;

import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.trulden.friends.R;
import com.trulden.friends.adapter.FriendsAdapter;
import com.trulden.friends.database.entity.Friend;

import static com.trulden.friends.util.Util.*;

public class EditFriendActivity extends AppCompatActivity {

    private EditText mName;
    private EditText mInfo;

    private long updatedFriendId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_friend);

        mName = findViewById(R.id.edit_friends_name);
        mInfo = findViewById(R.id.edit_friends_info);

        Intent intent = getIntent();
        updatedFriendId = intent.getLongExtra(EXTRA_FRIEND_ID, -1);
        mName.setText(intent.getStringExtra(EXTRA_FRIEND_NAME));
        mInfo.setText(intent.getStringExtra(EXTRA_FRIEND_NOTES));

        if(updatedFriendId == -1){
            getSupportActionBar().setTitle(getString(R.string.action_bar_title_add_friend));
        } else {
            getSupportActionBar().setTitle(getString(R.string.action_bar_title_edit_friend));
        }
    }

    public void saveFriend() {

        Intent replyIntent = new Intent();

        String name = mName.getText().toString();
        String info = mInfo.getText().toString();

        if(name.isEmpty()) {
            makeToast(this, getString(R.string.toast_warning_empty_name));
        } else if(updatedFriendId == -1 && FriendsAdapter.friendExists(name)) {
            makeToast(this, getString(R.string.toast_warning_friend_exists));
        } else {

            replyIntent.putExtra(EXTRA_FRIEND_ID, updatedFriendId);
            replyIntent.putExtra(EXTRA_FRIEND_NAME, name);
            replyIntent.putExtra(EXTRA_FRIEND_NOTES, info);

            String toastMessage = updatedFriendId == -1
                    ? "«" + name + "»" + getString(R.string.toast_notice_friend_created)
                    : "«" + name + "»" + getString(R.string.toast_notice_friend_updated);

            makeToast(this, toastMessage);

            setResult(RESULT_OK, replyIntent);
            finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_save, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){
            case R.id.icon_save:{
                saveFriend();
                return true;
            }

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
