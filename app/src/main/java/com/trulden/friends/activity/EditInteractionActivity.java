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
import androidx.lifecycle.ViewModelProviders;

import com.trulden.friends.R;
import com.trulden.friends.activity.dialogs.DatePickerFragment;
import com.trulden.friends.activity.dialogs.EditInteractionTypeDialog;
import com.trulden.friends.activity.dialogs.FriendNotFoundDialog;
import com.trulden.friends.activity.interfaces.EditInteractionType;
import com.trulden.friends.database.MainViewModel;
import com.trulden.friends.database.entity.Friend;
import com.trulden.friends.database.entity.InteractionType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.ListIterator;
import java.util.Objects;

import static com.trulden.friends.util.Util.INTERACTION_COMMENT;
import static com.trulden.friends.util.Util.INTERACTION_DATE;
import static com.trulden.friends.util.Util.INTERACTION_FRIEND_IDS;
import static com.trulden.friends.util.Util.INTERACTION_FRIEND_NAMES;
import static com.trulden.friends.util.Util.INTERACTION_ID;
import static com.trulden.friends.util.Util.INTERACTION_TYPE_ID;
import static com.trulden.friends.util.Util.INTERACTION_TYPE_NAME;
import static com.trulden.friends.util.Util.INTERACTION_TYPE_POS;
import static com.trulden.friends.util.Util.formatDate;
import static com.trulden.friends.util.Util.makeToast;

/**
 * Activity in which user can edit existing or create new Interaction
 */
public class EditInteractionActivity
        extends AppCompatActivity
        implements
            AdapterView.OnItemSelectedListener,
            EditInteractionType {

    MainViewModel mViewModel;

    private Spinner  mType;
    private ArrayAdapter<String> mTypeSpinnerAdapter;

    private EditText mDateText;
    private AppCompatMultiAutoCompleteTextView mFriendsText;
    private EditText mCommentText;

    private Calendar mPickedDate;

    private HashMap<String, Long> mFriendsMap = new HashMap<>();
    private HashMap<String, Long> mTypesMap = new HashMap<>();

    private long mInteractionId;
    private String mTypeToSelect = null;

    private SaveInteractionHandler mSaveHandler = new SaveInteractionHandler();

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_interaction);

        mSaveHandler.restoreState(savedInstanceState);

        mViewModel = ViewModelProviders.of(this).get(MainViewModel.class);

        // Set friend names from database to corresponding dropdown list
        mViewModel.getAllFriends().observe(this, friends -> {
            for(Friend friend : friends){
                if(!mFriendsMap.containsKey(friend.getName())) { // putIfAbsent requires API 24
                    mFriendsMap.put(friend.getName(), friend.getId());

                    mSaveHandler.newbies.remove(friend.getName());
                }
            }
            ArrayAdapter<String> adapter = new ArrayAdapter<>(getBaseContext(),
                    android.R.layout.simple_dropdown_item_1line, mFriendsMap.keySet().toArray(new String[0]));

            mFriendsText.setAdapter(adapter);

            // Saving interaction after all friends are checked and added [postponed_save]
            if(mSaveHandler.canSaveNow()){
                mSaveHandler.saveInteraction();
            }
        });

        // Set interaction types from db
        mViewModel.getAllInteractionTypes().observe(this, interactionTypes -> {
            for(InteractionType interactionType : interactionTypes){
                if(!mTypesMap.containsKey(interactionType.getInteractionTypeName())) { // putIfAbsent requires API 24
                    mTypesMap.put(interactionType.getInteractionTypeName(), interactionType.getId());
                }
            }

            String[] spinnerOptions = new String[mTypesMap.size() + 1];

            String[] typeNames = new String[interactionTypes.size()];
            for(int i = 0; i < interactionTypes.size(); ++i){
                typeNames[i] = interactionTypes.get(i).getInteractionTypeName();
            }

            System.arraycopy(typeNames, 0, spinnerOptions, 0, mTypesMap.size());

            spinnerOptions[spinnerOptions.length-1] = getString(R.string.add_new_interaction_type);

            mTypeSpinnerAdapter = new ArrayAdapter<>(getBaseContext(),
                    R.layout.spinner_item, spinnerOptions);

            if(mType != null) {
                mType.setAdapter(mTypeSpinnerAdapter);

                if(mTypeToSelect == null){

                    if(getIntent().hasExtra(INTERACTION_TYPE_POS)) {
                        mTypeToSelect = mTypeSpinnerAdapter.getItem(
                            getIntent().getIntExtra(INTERACTION_TYPE_POS, 0));
                    }

                    if(getIntent().hasExtra(INTERACTION_TYPE_NAME)) {
                        mTypeToSelect = getIntent().getStringExtra(INTERACTION_TYPE_NAME);
                    }
                }

                mType.setSelection(mTypeSpinnerAdapter.getPosition(mTypeToSelect));
            }
        });

        mType = findViewById(R.id.aei_type_spinner);

        mDateText = findViewById(R.id.aei_edit_date);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // I guess, 15% which still uses android 4 will have to suffer
            mDateText.setShowSoftInputOnFocus(false);
        }

        mFriendsText = findViewById(R.id.aei_edit_friends);
        mCommentText = findViewById(R.id.aei_edit_comment);

        mFriendsText.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());

        initInteractionTypeSpinner();

        mPickedDate = Calendar.getInstance();

        // Get info from intent and set it to views

        Intent intent = getIntent();

        mInteractionId = intent.getLongExtra(INTERACTION_ID, -1);

        if(mInteractionId == -1){
            Objects.requireNonNull(getSupportActionBar()).setTitle(getString(R.string.add_interaction));

            mPickedDate.setTimeInMillis(intent.getLongExtra(INTERACTION_DATE, mPickedDate.getTimeInMillis()));
            mDateText.setText(formatDate(mPickedDate.getTime()));
            mFriendsText.setText(intent.getStringExtra(INTERACTION_FRIEND_NAMES));

        } else {

            Objects.requireNonNull(getSupportActionBar()).setTitle(getString(R.string.edit_interaction));

            mViewModel.getInteraction(mInteractionId).observe(this, interaction -> {

                mViewModel.getType(interaction.getInteractionTypeId()).observe(this, type ->
                        mType.setSelection(mTypeSpinnerAdapter.getPosition(type.getInteractionTypeName())));

                mPickedDate.setTimeInMillis(interaction.getDate());
                mDateText.setText(formatDate(mPickedDate.getTime()));

                mViewModel.getFriendNamesOfInteraction(mInteractionId).observe(this, names -> {
                    mFriendsText.setText(TextUtils.join(", ", names));
                });

                mCommentText.setText(interaction.getComment());
            });
        }
    }

    private void initInteractionTypeSpinner() {
        if(mType != null){
            mType.setOnItemSelectedListener(this);
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        mSaveHandler.saveState(outState);
    }

    /**
     * Set the date
     */
    public void processDatePickerResult(Calendar pickedDate){
        mPickedDate = pickedDate;
        mDateText.setText(formatDate(mPickedDate.getTime()));
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {

        // If user chooses «Add new..» option in type selector, show him a dialog to create new type
        if(adapterView.getItemAtPosition(position).toString().equals(getString(R.string.add_new_interaction_type))){
            new EditInteractionTypeDialog(null).show(getSupportFragmentManager(), "editInteractionType");
            mType.setSelection(0);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    /**
     * Open date-picker
     */
    public void pickADate(View view) {
        DialogFragment f = new DatePickerFragment(mPickedDate);
        f.show(getSupportFragmentManager(), "datePicker");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_save, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.ms_save: {
                mSaveHandler.startCheckingFriends();
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

    @Override
    public boolean typeExists(String name) {
        return mTypesMap.containsKey(name);
    }

    @Override
    public void saveType(InteractionType interactionType) {
        mViewModel.add(interactionType);
        mTypeToSelect = interactionType.getInteractionTypeName();
    }


    /**
     * Create new friend with given name and empty info
     */
    public void createFriendByName(String name)   { mSaveHandler.createFriendByName(name);   }

    /**
     * Remove name from interaction.
     * Used when friend with name like this doesn't exist and user doesn't want to create a new one.
     */
    public void removeFriendName(String name)     { mSaveHandler.removeFriendName(name);     }

    /**
     * Used after editing name in check-dialog. Sets current name and checks it for existence.
     * @param name name to check
     */
    public void updateAndCheckFriend(String name) { mSaveHandler.updateAndCheckFriend(name); }

    /**
     * Handles saving interaction after save icon press.
     * Checks friends for existence, adds and edits them through dialogs, if needed.
     */
    private class SaveInteractionHandler{

        private static final String KEY_CHECK_FRIENDS_LIST = "Check friend's list";
        private static final String KEY_CHECK_ITERATOR_POS = "Iterator pos";
        private static final String KEY_NEWBIES = "Newbies";
        private static final String KEY_TIME_TO_SAVE_INTERACTION = "Time to save interaction";

        private ArrayList<String> checkFriendsList = null;
        private ListIterator<String> checkFriendsIter = null;

        /**
         * New friends, who need to be created, before creating/updating an Interaction entry in database
         */
        private HashSet<String> newbies = new HashSet<>();

        /**
         * Flag saying if it is ok to save Interaction entry right now
         */
        private boolean timeToSaveInteraction = false;

        private Context context = EditInteractionActivity.this;

        void saveState(Bundle outState) {
            if(checkFriendsList != null) {
                outState.putStringArrayList(KEY_CHECK_FRIENDS_LIST, checkFriendsList);
                outState.putInt(KEY_CHECK_ITERATOR_POS, checkFriendsIter.previousIndex());
                outState.putSerializable(KEY_NEWBIES, newbies);
                outState.putBoolean(KEY_TIME_TO_SAVE_INTERACTION, timeToSaveInteraction);
            }
        }

        void restoreState(Bundle savedInstanceState) {
            if(savedInstanceState != null && savedInstanceState.containsKey(KEY_CHECK_FRIENDS_LIST)) {
                checkFriendsList = savedInstanceState.getStringArrayList(KEY_CHECK_FRIENDS_LIST);
                checkFriendsIter = checkFriendsList.listIterator(savedInstanceState.getInt(KEY_CHECK_ITERATOR_POS));
                checkFriendsIter.next();
                newbies = (HashSet<String>) savedInstanceState.getSerializable(KEY_NEWBIES);
                timeToSaveInteraction = savedInstanceState.getBoolean(KEY_TIME_TO_SAVE_INTERACTION);
            }
        }

        private void startCheckingFriends() {

            if(argsFilled()) {
                checkFriendsList = new ArrayList<>(Arrays.asList(
                        mFriendsText.getText().toString().split("\\s*,\\s*")));

                checkFriendsIter = checkFriendsList.listIterator();

                checkNextFriend();
            }
        }

        private boolean argsFilled() {

            if(mDateText.getText().toString().isEmpty()){
                makeToast(context, getString(R.string.toast_warning_fill_date));
                return false;
            }

            if(mFriendsText.getText().toString().isEmpty()){
                makeToast(context, getString(R.string.toast_warning_fill_friends));
                return false;
            }

            return true;
        }

        /**
         * Checks existence of next friend.
         * If all friends are checked, starts saving.
         */
        private void checkNextFriend() {
            if(checkFriendsIter.hasNext()){
                String friendName = checkFriendsIter.next();
                checkFriend(friendName);
            } else {
                timeToSaveInteraction = true;

                // When all friends are checked, we might have to wait, while new ones are added to database
                // In that case, we can't save interaction from here, because it will break flow and cause NPE
                // I'm saving it from friends observer, look it up by [postponed_save]

                if (canSaveNow()) {
                    saveInteraction();
                }
            }
        }

        /**
         * Checks existence of friend by name.
         * If friend doesn't exist, shows {@link FriendNotFoundDialog}.
         */
        private void checkFriend(String name) {
            if(mFriendsMap.containsKey(name)) {
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
            mViewModel.add(new Friend(name, ""));

            newbies.add(name);

            makeToast(context, "«" + name + "»"
                    + getString(R.string.toast_notice_friend_created)); // Not actually, lol
            checkNextFriend();
        }

        void removeFriendName(String name) {
            checkFriendsIter.remove();
            makeToast(context, "«" + name + "»" +
                    getString(R.string.toast_notice_friend_forgotten));
            checkNextFriend();
        }

        /**
         * @return true if there is something to save and all new friends are in database
         */
        boolean canSaveNow(){
            return timeToSaveInteraction                // checked all friends
                && mSaveHandler.checkFriendsList != null // there are some actual friends
                && mSaveHandler.newbies.isEmpty();       // all of them saved to db
        }

        /**
         * Create an intent with given Interaction. {@link MainActivity} handles it.
         */
        void saveInteraction() {

            Intent replyIntent = new Intent();
            HashSet<String> friendNamesSet = new HashSet<>(checkFriendsList);

            // User can remove all non-created friends in dialog, so we need to check again
            if(friendNamesSet.size() == 0){
                makeToast(context, getString(R.string.toast_warning_fill_friends));
                mFriendsText.setText("");
                return;
            }

            replyIntent.putExtra(INTERACTION_ID, mInteractionId);

            // Put friend names to intent

            String friendNamesString = TextUtils.join(", ", checkFriendsList);

            replyIntent.putExtra(INTERACTION_FRIEND_NAMES, friendNamesString);

            // Put friend id's to intent

            HashSet<Long> friendsIds = new HashSet<>();
            for(String friendName : friendNamesSet){
                friendsIds.add(mFriendsMap.get(friendName));
            }
            replyIntent.putExtra(INTERACTION_FRIEND_IDS, friendsIds);

            // Put type, date and comment to intent

            replyIntent.putExtra(INTERACTION_TYPE_ID, mTypesMap.get(mType.getSelectedItem().toString()));
            replyIntent.putExtra(INTERACTION_DATE, mPickedDate.getTimeInMillis());
            replyIntent.putExtra(INTERACTION_COMMENT, mCommentText.getText().toString());

            setResult(RESULT_OK, replyIntent);

            makeToast(context, getString(R.string.toast_notice_interaction_saved));

            finish();
        }
    }
}
