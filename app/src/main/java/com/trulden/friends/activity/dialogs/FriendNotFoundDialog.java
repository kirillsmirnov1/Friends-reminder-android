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

/**
 * Created when user enters name of friend who is not in a database yet
 */
public class FriendNotFoundDialog extends DialogFragment {

    private String name;

    public FriendNotFoundDialog(String name){
        this.name = name;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setMessage(String.format(getString(R.string.dont_have_such_friend), name ))
               .setPositiveButton(getString(R.string.create), new DialogInterface.OnClickListener() {
                   @Override
                   public void onClick(DialogInterface dialogInterface, int i) {
                       ((EditInteractionActivity)getActivity()).createFriendByName(name);
                   }
               })
               .setNeutralButton(getString(R.string.edit), new DialogInterface.OnClickListener() {
                   @Override
                   public void onClick(DialogInterface dialogInterface, int i) {
                       EditFriendNameDialog dialog = new EditFriendNameDialog(name);
                       dialog.show(getActivity().getSupportFragmentManager(), "editFriendNameDialog");
                   }
               })
               .setNegativeButton(getString(R.string.forget), new DialogInterface.OnClickListener() {
                   @Override
                   public void onClick(DialogInterface dialogInterface, int i) {
                       ((EditInteractionActivity)getActivity()).removeFriendName(name);
                   }
               });

        return builder.create();
    }
}
