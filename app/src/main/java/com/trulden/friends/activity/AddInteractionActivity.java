package com.trulden.friends.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.MultiAutoCompleteTextView;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import androidx.appcompat.widget.AppCompatMultiAutoCompleteTextView;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.trulden.friends.activity.dialogs.DatePickerFragment;
import com.trulden.friends.R;
import com.trulden.friends.activity.dialogs.FriendNotFoundDialog;
import com.trulden.friends.database.entity.Friend;
import com.trulden.friends.database.FriendsViewModel;
import com.trulden.friends.database.entity.InteractionType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static com.trulden.friends.util.Util.*;

public class AddInteractionActivity extends AppCompatActivity implements
        AdapterView.OnItemSelectedListener{

    FriendsViewModel mFriendsViewModel;

    private HashMap<String, Integer> friendsMap = new HashMap<>();
    private HashMap<String, Integer> typesMap = new HashMap<>();

    private Spinner  mType;
    private EditText mDate;
    private AppCompatMultiAutoCompleteTextView mFriends;
    private EditText mComment;

    @SuppressLint("ClickableViewAccessibility")
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

        // Set interaction types from db
        mFriendsViewModel.getAllInteractionTypes().observe(this, new Observer<List<InteractionType>>() {
            @Override
            public void onChanged(List<InteractionType> interactionTypes) {
                for(InteractionType interactionType : interactionTypes){
                    if(!typesMap.containsKey(interactionType.getInteractionTypeName())) { // putIfAbsent requires API 24
                        typesMap.put(interactionType.getInteractionTypeName(), interactionType.getId());
                    }
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<>(getBaseContext(),
                        android.R.layout.simple_spinner_dropdown_item, typesMap.keySet().toArray(new String[0]));

                if(mType != null)
                    mType.setAdapter(adapter);
            }
        });

        mType = findViewById(R.id.interaction_type_spinner);

        mDate = findViewById(R.id.editDate);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // I guess, 15% which still uses android 4 will have to suffer
            mDate.setShowSoftInputOnFocus(false);
        }

        mFriends = findViewById(R.id.editFriends);
        mComment = findViewById(R.id.editComment);

        mFriends.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());
        mFriends.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                mFriends.showDropDown();
                return false;
            }
        });

        initInteractionTypeSpinner();
    }

    private void initInteractionTypeSpinner() {
        if(mType != null){
            mType.setOnItemSelectedListener(this);
        }
    }

    public void saveInteraction() {
        String[] friends = mFriends.getText().toString().split("\\s*,\\s*");
        String result = // TODO обернуть в LogEntry класс
                        mDate.getText().toString() + " • " +
                        mType.getSelectedItem().toString() + "\n" +
                        mFriends.getText().toString() + "\n" +
                        mComment.getText().toString() + "\n";

        Intent replyIntent = new Intent();
        replyIntent.putExtra(EXTRA_NEW_INTERACTION, result);
        setResult(RESULT_OK, replyIntent);
        makeToast(this, "Interaction saved");
        finish();
    }

    private boolean allFriendsExist() {
        String[] friends = mFriends.getText().toString().split("\\s*,\\s*");

        for(String friendName : friends){
            if(!friendsMap.containsKey(friendName)){

                FriendNotFoundDialog dialog = new FriendNotFoundDialog(friendName);
                dialog.show(getSupportFragmentManager(), "friendNotFoundDialog");

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

    public void createFriendByName(String name){
        mFriendsViewModel.add(new Friend(name, ""));
        makeToast(this, "«" + name + "» is created");
    }

    public void removeFriendName(String name) {
        ArrayList<String> friendNames = new ArrayList<>(Arrays.asList(
                mFriends.getText().toString().split("\\s*,\\s*")));
        friendNames.remove(name);
        mFriends.setText(TextUtils.join(", ", friendNames.toArray(new String[0])));

        makeToast(this, "«" + name + "» is forgotten");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_save, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){
            case R.id.icon_save: {
                if(allFriendsExist()) {
                    saveInteraction();
                }
                return true;
            }

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
