package com.trulden.friends;

import android.content.Intent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

public class AddInteractionActivity extends AppCompatActivity {

    private EditText mType;
    private EditText mDate;
    private EditText mFriends;
    private EditText mComment;

    public static final String EXTRA_NEW_INTERACTION = "EXTRA_NEW_INTERACTION";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_interaction);

        mType = findViewById(R.id.editType);
        mDate = findViewById(R.id.editDate);
        mFriends = findViewById(R.id.editPersons);
        mComment = findViewById(R.id.editComment);
    }

    public void saveInteraction(View view) {
        String result =
                        mDate.getText().toString() + " • " +
                        mType.getText().toString() + "\n" +
                        mFriends.getText().toString() + "\n" +
                        mComment.getText().toString() + "\n";

        Intent replyIntent = new Intent();
        replyIntent.putExtra(EXTRA_NEW_INTERACTION, result);
        setResult(RESULT_OK, replyIntent);
        finish();
    }
}
