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
import java.util.Objects;

import static com.trulden.friends.util.Util.EXTRA_INTERACTION_COMMENT;
import static com.trulden.friends.util.Util.EXTRA_INTERACTION_DATE;
import static com.trulden.friends.util.Util.EXTRA_INTERACTION_FRIEND_IDS;
import static com.trulden.friends.util.Util.EXTRA_INTERACTION_FRIEND_NAMES;
import static com.trulden.friends.util.Util.EXTRA_INTERACTION_ID;
import static com.trulden.friends.util.Util.EXTRA_INTERACTION_TYPE_ID;
import static com.trulden.friends.util.Util.EXTRA_INTERACTION_TYPE_NAME;
import static com.trulden.friends.util.Util.dateFormat;
import static com.trulden.friends.util.Util.makeToast;

/**
 * Activity in which user can edit existing or create new Interaction
 */
public class EditInteractionActivity
        extends AppCompatActivity
        implements
            AdapterView.OnItemSelectedListener,
            EditInteractionType {

    private static final String LOG_TAG = EditInteractionActivity.class.getSimpleName();

    FriendsViewModel mFriendsViewModel;

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

        mFriendsViewModel = ViewModelProviders.of(this).get(FriendsViewModel.class);

        // Set friend names from database to corresponding dropdown list
        mFriendsViewModel.getAllFriends().observe(this, new Observer<List<Friend>>() {
            @Override
            public void onChanged(List<Friend> friends) {
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
            }
        });

        // Set interaction types from db
        mFriendsViewModel.getAllInteractionTypes().observe(this, new Observer<List<InteractionType>>() {
            @Override
            public void onChanged(List<InteractionType> interactionTypes) {
                for(InteractionType interactionType : interactionTypes){
                    if(!mTypesMap.containsKey(interactionType.getInteractionTypeName())) { // putIfAbsent requires API 24
                        mTypesMap.put(interactionType.getInteractionTypeName(), interactionType.getId());
                    }
                }

                String[] spinnerOptions = new String[mTypesMap.size() + 1];

                System.arraycopy(mTypesMap.keySet().toArray(new String[0]), 0, spinnerOptions, 0, mTypesMap.size());

                spinnerOptions[spinnerOptions.length-1] = getString(R.string.add_new_interaction_type);

                mTypeSpinnerAdapter = new ArrayAdapter<>(getBaseContext(),
                        android.R.layout.simple_spinner_dropdown_item, spinnerOptions);

                if(mType != null) {
                    mType.setAdapter(mTypeSpinnerAdapter);

                    mType.setSelection(mTypeSpinnerAdapter.getPosition(
                            mTypeToSelect == null
                                    ? getIntent().getStringExtra(EXTRA_INTERACTION_TYPE_NAME)
                                    : mTypeToSelect
                    ));
                }
            }
        });

        mType = findViewById(R.id.interaction_type_spinner);

        mDateText = findViewById(R.id.editDate);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // I guess, 15% which still uses android 4 will have to suffer
            mDateText.setShowSoftInputOnFocus(false);
        }

        mFriendsText = findViewById(R.id.editFriends);
        mCommentText = findViewById(R.id.editComment);

        mFriendsText.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());

        initInteractionTypeSpinner();

        // Get info from intent and set it to views

        Intent intent = getIntent();

        mInteractionId = intent.getLongExtra(EXTRA_INTERACTION_ID, -1);

        if(mInteractionId == -1){
            Objects.requireNonNull(getSupportActionBar()).setTitle(getString(R.string.add_interaction));
        } else {
            Objects.requireNonNull(getSupportActionBar()).setTitle(getString(R.string.edit_interaction));

            mCommentText.setText(intent.getStringExtra(EXTRA_INTERACTION_COMMENT));

            mPickedDate = Calendar.getInstance();
            mPickedDate.setTimeInMillis(intent.getLongExtra(EXTRA_INTERACTION_DATE, -1));

            mDateText.setText(dateFormat.format(mPickedDate.getTime()));

            mFriendsText.setText(intent.getStringExtra(EXTRA_INTERACTION_FRIEND_NAMES));
        }
    }

    private void initInteractionTypeSpinner() {
        if(mType != null){
            mType.setOnItemSelectedListener(this);
        }
    }

    public void processDatePickerResult(int year, int month, int date){
        mPickedDate = Calendar.getInstance();

        mPickedDate.set(year, month-1, date);

        Calendar tomorrow = Calendar.getInstance();
        tomorrow.add(Calendar.DATE, 1);

        if(mPickedDate.before(tomorrow)) {
            mDateText.setText(dateFormat.format(mPickedDate.getTime()));
        } else {
            makeToast(this, getString(R.string.set_date_future_warning));
            mPickedDate = null;
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {

        if(adapterView.getItemAtPosition(position).toString().equals(getString(R.string.add_new_interaction_type))){
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

        if (item.getItemId() == R.id.icon_save) {
            mSaveHandler.startCheckingFriends();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean typeExists(String name) {
        return mTypesMap.containsKey(name);
    }

    @Override
    public void saveType(InteractionType interactionType) {
        mFriendsViewModel.add(interactionType);
        mTypeToSelect = interactionType.getInteractionTypeName();
    }

    public void createFriendByName(String name)   { mSaveHandler.createFriendByName(name);   }
    public void removeFriendName(String name)     { mSaveHandler.removeFriendName(name);     }
    public void updateAndCheckFriend(String name) { mSaveHandler.updateAndCheckFriend(name); }

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
            mFriendsViewModel.add(new Friend(name, ""));

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

        boolean canSaveNow(){
            return timeToSaveInteraction                // checked all friends
                && mSaveHandler.checkFriendsList != null // there are some actual friends
                && mSaveHandler.newbies.isEmpty();       // all of them saved to db
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
                friendsIds.add(mFriendsMap.get(friendName));
            }
            replyIntent.putExtra(EXTRA_INTERACTION_FRIEND_IDS, friendsIds);

            // And all of the others

            replyIntent.putExtra(EXTRA_INTERACTION_TYPE_ID, mTypesMap.get(mType.getSelectedItem().toString()));
            replyIntent.putExtra(EXTRA_INTERACTION_DATE, mPickedDate.getTimeInMillis());
            replyIntent.putExtra(EXTRA_INTERACTION_COMMENT, mCommentText.getText().toString());

            setResult(RESULT_OK, replyIntent);

            makeToast(context, getString(R.string.toast_notice_interaction_saved));

            finish();
        }
    }
}
