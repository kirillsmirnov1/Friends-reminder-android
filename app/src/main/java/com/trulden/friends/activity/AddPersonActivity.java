package com.trulden.friends.activity;

import android.content.Intent;
import android.view.View;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.trulden.friends.R;

public class AddPersonActivity extends AppCompatActivity {

    private static final String EXTRA_NEW_PERSON = "EXTRA_NEW_PERSON";
    private EditText mName;
    private EditText mInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_person);

        mName = findViewById(R.id.edit_persons_name);
        mInfo = findViewById(R.id.edit_persons_info);
    }

    public void savePerson(View view) {
        String result = // TODO save to db
            mName.getText().toString() + "\n" + mInfo.getText().toString();

        Intent replyIntent = new Intent();
        replyIntent.putExtra(EXTRA_NEW_PERSON, result);
        setResult(RESULT_OK, replyIntent);
        finish();
    }
}
