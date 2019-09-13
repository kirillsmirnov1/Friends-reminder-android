package com.trulden.friends.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.MultiAutoCompleteTextView;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatMultiAutoCompleteTextView;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.trulden.friends.R;
import com.trulden.friends.activity.dialogs.DatePickerFragment;
import com.trulden.friends.activity.dialogs.EditInteractionTypeDialog;
import com.trulden.friends.activity.dialogs.FriendNotFoundDialog;
import com.trulden.friends.activity.interfaces.EditInteractionType;
import com.trulden.friends.database.FriendsViewModel;
import com.trulden.friends.database.entity.Friend;
import com.trulden.friends.database.entity.InteractionType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;

import static com.trulden.friends.util.Util.*;

public class EditInteractionActivity
        extends AppCompatActivity
        implements
            AdapterView.OnItemSelectedListener,
            EditInteractionType {

    private static final String LOG_TAG = EditInteractionActivity.class.getSimpleName();

    FriendsViewModel mFriendsViewModel;

    private Spinner  mType;
    private ArrayAdapter<String> mSpinnerAdapter;

    private EditText mDate;
    private AppCompatMultiAutoCompleteTextView mFriends;
    private EditText mComment;

    private Calendar pickedDate;

    private HashMap<String, Long> friendsMap = new HashMap<>();
    private HashMap<String, Long> typesMap = new HashMap<>();

    private long mInteractionId;
    private String mTypeToSelect = null;

    private SaveInteractionHandler saveHandler = new SaveInteractionHandler();

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

                        if(saveHandler.newbies.contains(friend.getName())){
                            saveHandler.newbies.remove(friend.getName());
                        }
                    }
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<>(getBaseContext(),
                        android.R.layout.simple_dropdown_item_1line, friendsMap.keySet().toArray(new String[0]));

                mFriends.setAdapter(adapter);

                // Saving interaction after all friends are checked and added #postponed_save
                if(saveHandler.canSaveNow()){
                    saveHandler.saveInteraction();
                }
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_save, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){
            case R.id.icon_save: {
                saveHandler.startCheckingFriends();
                return true;
            }

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean typeExists(String name) {
        return typesMap.containsKey(name);
    }

    @Override
    public void saveType(InteractionType interactionType) {
        mFriendsViewModel.add(interactionType);
        mTypeToSelect = interactionType.getInteractionTypeName();

        makeToast(this, "Type created!");
    }

    public void createFriendByName(String name)   { saveHandler.createFriendByName(name);   }
    public void removeFriendName(String name)     { saveHandler.removeFriendName(name);     }
    public void updateAndCheckFriend(String name) { saveHandler.updateAndCheckFriend(name); }

    // Handles saving interaction after save icon press
    // Checks friends for existence, adds and edits them through dialogs, if needed
    private class SaveInteractionHandler{

        private ListIterator<String> checkFriendsIter = null;
        private ArrayList<String> checkFriendsList = null;
        private HashSet<String> newbies = new HashSet<>();
        private boolean timeToSaveInteraction = false;

        private Context context = EditInteractionActivity.this;

        private void startCheckingFriends() {

            if(argsFilled()) {
                checkFriendsList = new ArrayList<>(Arrays.asList(
                        mFriends.getText().toString().split("\\s*,\\s*")));

                checkFriendsIter = checkFriendsList.listIterator();

                checkNextFriend();
            }
        }

        private boolean argsFilled() {

            if(mDate.getText().toString().isEmpty()){
                makeToast(context, "Fill date");
                return false;
            }

            if(mFriends.getText().toString().isEmpty()){
                makeToast(context, "Fill friends");
                return false;
            }

            return true;
        }

        private void checkNextFriend() {
            if(checkFriendsIter.hasNext()){
                String friendName = checkFriendsIter.next();
                checkFriend(friendName);
            } else {
                timeToSaveInteraction = true;

                // When all friends are checked, we might have to wait, while new ones are added to database
                // In that case, we can't save interaction from here, because it will break flow and cause NPE
                // I'm saving it from friends observer, look it up by #postponed_save

                if (canSaveNow()) {
                    saveInteraction();
                }
            }
        }

        private void checkFriend(String name) {
            if(friendsMap.containsKey(name)) {
                checkNextFriend();
            } else {
                FriendNotFoundDialog dialog = new FriendNotFoundDialog(name);
                dialog.show(getSupportFragmentManager(), "friendNotFoundDialog");
            }
        }

        void updateAndCheckFriend(String name) {
            checkFriendsIter.set(name);
            checkFriend(name);
        }

        void createFriendByName(String name){
            mFriendsViewModel.add(new Friend(name, ""));

            newbies.add(name);

            makeToast(context, "«" + name + "» is created"); // Not actually, lol
            checkNextFriend();
        }

        void removeFriendName(String name) {
            checkFriendsIter.remove();
            makeToast(context, "«" + name + "» is forgotten");
            checkNextFriend();
        }

        boolean canSaveNow(){
            return timeToSaveInteraction                // checked all friends
                && saveHandler.checkFriendsList != null // there are some actual friends
                && saveHandler.newbies.isEmpty();       // all of them saved to db
        }

        void saveInteraction() {

            Intent replyIntent = new Intent();
            HashSet<String> friendNamesSet = new HashSet<>(checkFriendsList);

            replyIntent.putExtra(EXTRA_INTERACTION_ID, mInteractionId);

            // Get friend names, get ids and put them into intent

            String friendNamesString = TextUtils.join(", ", checkFriendsList);

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

            makeToast(context, "Interaction saved");

            finish();
        }
    }
}
