package com.trulden.friends.activity.dialogs;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.trulden.friends.R;
import com.trulden.friends.activity.EditInteractionActivity;

import java.util.Objects;

/**
 * Created when user enters name of friend who is not in a database yet
 */
public class FriendNotFoundDialog extends DialogFragment {

    private static final String NAME_KEY = "NAME_KEY";
    private String name;

    public FriendNotFoundDialog(){
        // Required constructor
    }

    public FriendNotFoundDialog(String name){
        this.name = name;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        // Activity from which dialog have been created
        final EditInteractionActivity activity =
                Objects.requireNonNull((EditInteractionActivity)getActivity());

        if(name == null && savedInstanceState.containsKey(NAME_KEY)){
            name = savedInstanceState.getString(NAME_KEY);
        }

        AlertDialog.Builder builder =
                new AlertDialog.Builder(Objects.requireNonNull(getActivity()));

        builder
            .setMessage(String.format(getString(R.string.dont_have_such_friend), name ))

            .setPositiveButton(getString(R.string.create),
               (dialogInterface, i) -> activity.createFriendByName(name))

            .setNeutralButton(getString(R.string.edit), (dialogInterface, i) -> {
               EditFriendNameDialog dialog = new EditFriendNameDialog(name);
               dialog.show(activity.getSupportFragmentManager(), "editFriendNameDialog");
               })
            .setNegativeButton(getString(R.string.forget), (dialogInterface, i) -> activity.removeFriendName(name));

        return builder.create();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(NAME_KEY, name);
    }
}
