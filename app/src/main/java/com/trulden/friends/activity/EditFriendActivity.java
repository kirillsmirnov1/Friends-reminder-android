package com.trulden.friends.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;

import com.trulden.friends.R;
import com.trulden.friends.database.MainViewModel;
import com.trulden.friends.database.entity.Friend;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.trulden.friends.util.Util.FRIEND_ID;
import static com.trulden.friends.util.Util.FRIEND_NAME;
import static com.trulden.friends.util.Util.FRIEND_NOTES;
import static com.trulden.friends.util.Util.makeToast;

/**
 * Activity for creating or editing Friend objects
 */
public class EditFriendActivity extends AppCompatActivity {

    private EditText mName;
    private EditText mInfo;

    private long mFriendId;
    private String mOldName;

    private List<Friend> mFriends = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_friend);

        MainViewModel viewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        viewModel.getAllFriends().observe(this, friends -> mFriends = friends);

        mName = findViewById(R.id.aef_edit_name);
        mInfo = findViewById(R.id.aef_edit_info);

        Intent intent = getIntent();
        mFriendId = intent.getLongExtra(FRIEND_ID, -1);

        if(mFriendId == -1){
            Objects.requireNonNull(getSupportActionBar()).setTitle(getString(R.string.add_friend));
        } else {
            mOldName = intent.getStringExtra(FRIEND_NAME);
            Objects.requireNonNull(getSupportActionBar()).setTitle(mOldName);
        }

        mName.setText(mOldName);
        mInfo.setText(intent.getStringExtra(FRIEND_NOTES));
    }

    private void saveFriend() {

        Intent replyIntent = new Intent();

        String name = mName.getText().toString();
        String info = mInfo.getText().toString();

        if(name.isEmpty()) {
            makeToast(this, getString(R.string.toast_warning_empty_name));
        } else if(friendExists(name)) {
            makeToast(this, getString(R.string.toast_warning_friend_exists));
        } else {

            replyIntent.putExtra(FRIEND_ID, mFriendId);
            replyIntent.putExtra(FRIEND_NAME, name);
            replyIntent.putExtra(FRIEND_NOTES, info);

            String toastMessage =
                    mFriendId == -1
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
            case R.id.ms_save: {
                saveFriend();
                return true;
            }
            case android.R.id.home: {
                setResult(RESULT_CANCELED, null);
                finish();
                return true;
            }
        }

        return super.onOptionsItemSelected(item);
    }

    private boolean friendExists(String name){

        if(name.equals(mOldName)) return false;

        for(Friend friend : mFriends){
            if(friend.getName().equals(name))
                return true;
        }
        return false;
    }
}
