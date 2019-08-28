package com.trulden.friends.activity;

import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.MultiAutoCompleteTextView;
import android.widget.Spinner;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.widget.AppCompatMultiAutoCompleteTextView;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.trulden.friends.activity.dialogs.DatePickerFragment;
import com.trulden.friends.R;
import com.trulden.friends.database.Friend;
import com.trulden.friends.database.FriendsViewModel;

import java.util.HashMap;
import java.util.List;

import static com.trulden.friends.util.Util.*;

public class AddInteractionActivity extends AppCompatActivity implements
        AdapterView.OnItemSelectedListener{

    FriendsViewModel mFriendsViewModel;
    private HashMap<String, Integer> friendsMap = new HashMap<>();

    private Spinner  mType;
    private EditText mDate;
    private AppCompatMultiAutoCompleteTextView mFriends;
    private EditText mComment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_interaction);

        mFriendsViewModel = ViewModelProviders.of(this).get(FriendsViewModel.class);

        // Set friend names from database to corresponding dropdown list
        mFriendsViewModel.getAllFriends().observe(this, new Observer<List<Friend>>() {
            @Override
            public void onChanged(List<Friend> friends) {
                for(Friend friend : friends){
                    if(!friendsMap.containsKey(friend.getName())) { // putIfAbsent requires API 24
                        friendsMap.put(friend.getName(), friend.getId());
                    }
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<>(getBaseContext(),
                        android.R.layout.simple_dropdown_item_1line, friendsMap.keySet().toArray(new String[0]));

                mFriends.setAdapter(adapter);
            }
        });

        mType = findViewById(R.id.interaction_type_spinner);
        mDate = findViewById(R.id.editDate);
        mFriends = findViewById(R.id.editFriends);
        mComment = findViewById(R.id.editComment);
        mFriends.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());

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
        String[] friends = mFriends.getText().toString().split("\\s*,\\s*");
        String result = // TODO обернуть в LogEntry класс
                        mDate.getText().toString() + " • " +
                        mType.getSelectedItem().toString() + "\n" +
                        mFriends.getText().toString() + "\n" +
                        mComment.getText().toString() + "\n";

        if(allFriendsExist(friends)){
            Intent replyIntent = new Intent();
            replyIntent.putExtra(EXTRA_NEW_INTERACTION, result);
            setResult(RESULT_OK, replyIntent);
            finish();
        }
    }

    private boolean allFriendsExist(String[] friends) {
        for(String friend : friends){
            if(!friendsMap.containsKey(friend)){
                Toast.makeText(this, "You don't have friend named «" + friend + "»", Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        return true;
    }

    public void processDatePickerResult(int year, int month, int date){
        mDate.setText(year + "-" + month + "-" + date);
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    public void pickADate(View view) {
        DialogFragment f = new DatePickerFragment();
        f.show(getSupportFragmentManager(), "datePicker");
    }
}
