package com.trulden.friends;

import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

public class AddInteractionActivity extends AppCompatActivity implements
        AdapterView.OnItemSelectedListener{

    private Spinner mType;
    private EditText mDate;
    private EditText mFriends;
    private EditText mComment;

    public static final String EXTRA_NEW_INTERACTION = "EXTRA_NEW_INTERACTION";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_interaction);

        mType = findViewById(R.id.interaction_type_spinner);
        mDate = findViewById(R.id.editDate);
        mFriends = findViewById(R.id.editPersons);
        mComment = findViewById(R.id.editComment);

        initInteractionTypeSpinner();
    }

    private void initInteractionTypeSpinner() {
        if(mType != null){
            mType.setOnItemSelectedListener(this);
        }

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.interaction_types, android.R.layout.simple_spinner_item);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        if(mType != null)
            mType.setAdapter(adapter);
    }

    public void saveInteraction(View view) {
        String result =
                        mDate.getText().toString() + " • " +
                        mType.getSelectedItem().toString() + "\n" +
                        mFriends.getText().toString() + "\n" +
                        mComment.getText().toString() + "\n";

        Intent replyIntent = new Intent();
        replyIntent.putExtra(EXTRA_NEW_INTERACTION, result);
        setResult(RESULT_OK, replyIntent);
        finish();
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}
