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
import com.trulden.friends.activity.dialogs.EditInteractionTypeDialog;
import com.trulden.friends.activity.dialogs.FriendNotFoundDialog;
import com.trulden.friends.activity.interfaces.EditInteractionType;
import com.trulden.friends.database.entity.Friend;
import com.trulden.friends.database.FriendsViewModel;
import com.trulden.friends.database.entity.InteractionType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import static com.trulden.friends.util.Util.*;

public class EditInteractionActivity
        extends AppCompatActivity
        implements
            AdapterView.OnItemSelectedListener,
            EditInteractionType {

    private static final String LOG_TAG = EditInteractionActivity.class.getSimpleName();
    FriendsViewModel mFriendsViewModel;

    private HashMap<String, Long> friendsMap = new HashMap<>();
    private HashMap<String, Long> typesMap = new HashMap<>();

    private Spinner  mType;
    private ArrayAdapter<String> mSpinnerAdapter;

    private EditText mDate;
    private AppCompatMultiAutoCompleteTextView mFriends;
    private EditText mComment;

    private Calendar pickedDate;

    private long mInteractionId;
    private String mTypeToSelect = null;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_interaction);

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

                String[] spinnerOptions = new String[typesMap.size() + 1];

                System.arraycopy(typesMap.keySet().toArray(new String[0]), 0, spinnerOptions, 0, typesMap.size());

                spinnerOptions[spinnerOptions.length-1] = getResources().getString(R.string.add_new);

                mSpinnerAdapter = new ArrayAdapter<>(getBaseContext(),
                        android.R.layout.simple_spinner_dropdown_item, spinnerOptions);

                if(mType != null)
                    mType.setAdapter(mSpinnerAdapter);

                mType.setSelection(mSpinnerAdapter.getPosition(
                        mTypeToSelect == null
                        ? getIntent().getStringExtra(EXTRA_INTERACTION_TYPE_NAME)
                        : mTypeToSelect
                ));
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

        initInteractionTypeSpinner();

        // Get info from intent and set it to views

        Intent intent = getIntent();

        mInteractionId = intent.getLongExtra(EXTRA_INTERACTION_ID, -1);

        if(mInteractionId == -1){
            getSupportActionBar().setTitle("Add interaction");
        } else {
            getSupportActionBar().setTitle("Edit interaction");

            mComment.setText(intent.getStringExtra(EXTRA_INTERACTION_COMMENT));

            pickedDate = Calendar.getInstance();
            pickedDate.setTimeInMillis(intent.getLongExtra(EXTRA_INTERACTION_DATE, -1));

            mDate.setText(dateFormat.format(pickedDate.getTime()));

            mFriends.setText(intent.getStringExtra(EXTRA_INTERACTION_FRIEND_NAMES));
        }
    }

    private void initInteractionTypeSpinner() {
        if(mType != null){
            mType.setOnItemSelectedListener(this);
        }
    }

    public void saveInteraction() {
        Intent replyIntent = new Intent();

        replyIntent.putExtra(EXTRA_INTERACTION_ID, mInteractionId);

        // Get friend names, get ids and put them into intent

        String friendNamesString = mFriends.getText().toString();

        HashSet<String> friendNamesSet = new HashSet<>(Arrays.asList(
                friendNamesString.split("\\s*,\\s*")));

        replyIntent.putExtra(EXTRA_INTERACTION_FRIEND_NAMES, friendNamesString);

        HashSet<Long> friendsIds = new HashSet<>();
        for(String friendName : friendNamesSet){
            friendsIds.add(friendsMap.get(friendName));
        }
        replyIntent.putExtra(EXTRA_INTERACTION_FRIEND_IDS, friendsIds);

        // And all of the others

        replyIntent.putExtra(EXTRA_INTERACTION_TYPE_ID, typesMap.get(mType.getSelectedItem().toString()));
        replyIntent.putExtra(EXTRA_INTERACTION_DATE, pickedDate.getTimeInMillis());
        replyIntent.putExtra(EXTRA_INTERACTION_COMMENT, mComment.getText().toString());

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
        pickedDate = Calendar.getInstance();
        //                   Sincerely, fuck you, developers of Calendar class
        pickedDate.set(year, month-1, date);

        Calendar tomorrow = Calendar.getInstance();
        tomorrow.add(Calendar.DATE, 1);

        if(pickedDate.before(tomorrow)) {
            mDate.setText(dateFormat.format(pickedDate.getTime()));
        } else {
            makeToast(this, "Can't interact in the future");
            pickedDate = null;
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {

        if(adapterView.getItemAtPosition(position).toString().equals(getResources().getString(R.string.add_new))){
            new EditInteractionTypeDialog(null).show(getSupportFragmentManager(), "editInteractionType");
            mType.setSelection(0);
        }
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
                if(argsFilled() && allFriendsExist()) {
                    saveInteraction();
                }
                return true;
            }

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private boolean argsFilled() {

        if(mDate.getText().toString().isEmpty()){
            makeToast(this, "Fill date");
            return false;
        }

        if(mFriends.getText().toString().isEmpty()){
            makeToast(this, "Fill friends");
            return false;
        }

        return true;
    }

    @Override
    public boolean typeExists(String name) {
        return typesMap.containsKey(name);
    }

    @Override
    public void saveType(InteractionType interactionType) {
        mFriendsViewModel.add(interactionType);
        mTypeToSelect = interactionType.getInteractionTypeName();
    }
}
