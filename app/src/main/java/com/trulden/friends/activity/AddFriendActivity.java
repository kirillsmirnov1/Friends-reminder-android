package com.trulden.friends.activity;

import android.content.Intent;
import android.view.View;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Toast;

import com.trulden.friends.R;
import com.trulden.friends.adapter.FriendsAdapter;

public class AddFriendActivity extends AppCompatActivity {

    public static final String EXTRA_FRIEND_ID   = "EXTRA_FRIEND_ID";
    public static final String EXTRA_FRIEND_NAME = "EXTRA_FRIEND_NAME";
    public static final String EXTRA_FRIEND_INFO = "EXTRA_FRIEND_INFO";

    private EditText mName;
    private EditText mInfo;

    private int updatedFriendId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friend);

        mName = findViewById(R.id.edit_friends_name);
        mInfo = findViewById(R.id.edit_friends_info);

        Intent intent = getIntent();
        updatedFriendId = intent.getIntExtra(EXTRA_FRIEND_ID, -1);
        mName.setText(intent.getStringExtra(EXTRA_FRIEND_NAME));
        mInfo.setText(intent.getStringExtra(EXTRA_FRIEND_INFO));
    }

    public void saveFriend(View view) {

        Intent replyIntent = new Intent();

        String name = mName.getText().toString();
        String info = mInfo.getText().toString();

        if(name.isEmpty()) {
            Toast.makeText(this, "Empty name", Toast.LENGTH_SHORT).show();
        } else if(FriendsAdapter.friendExists(name)) {
            Toast.makeText(this, "Friend with name like this already exists", Toast.LENGTH_SHORT).show();
        } else {

            replyIntent.putExtra(EXTRA_FRIEND_ID, updatedFriendId);
            replyIntent.putExtra(EXTRA_FRIEND_NAME, name);
            replyIntent.putExtra(EXTRA_FRIEND_INFO, info);

            setResult(RESULT_OK, replyIntent);
            finish();
        }
    }
}
