package com.trulden.friends.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.widget.TextView;

import com.trulden.friends.R;
import static com.trulden.friends.util.Util.*;

public class FriendPageActivity extends AppCompatActivity {

    private TextView mPersonInfo;

    private int personId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_page);

        mPersonInfo = findViewById(R.id.friend_page_info);

        Intent intent = getIntent();
        personId = intent.getIntExtra(EXTRA_FRIEND_ID, -1);
        getSupportActionBar().setTitle(intent.getStringExtra(EXTRA_FRIEND_NAME));
        mPersonInfo.setText(intent.getStringExtra(EXTRA_FRIEND_INFO));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.selection_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }
}
