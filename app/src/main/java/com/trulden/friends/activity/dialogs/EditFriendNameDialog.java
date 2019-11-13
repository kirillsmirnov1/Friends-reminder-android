package com.trulden.friends.activity.dialogs;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.trulden.friends.R;
import com.trulden.friends.activity.EditInteractionActivity;

import java.util.Objects;

/**
 * Dialog in which user can change name of friend if there was some error.
 * Used in {@link FriendNotFoundDialog}
 */
public class EditFriendNameDialog extends DialogFragment {

    private String name;
    private EditText editName;

    public EditFriendNameDialog(){
        // Required constructor
    }

    EditFriendNameDialog(String name){
        this.name = name;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder =
                new AlertDialog.Builder(Objects.requireNonNull(getActivity()));

        @SuppressLint("InflateParams")
        View dialogView = requireActivity().getLayoutInflater()
                .inflate(R.layout.dialog_edit_friend_name, null);

        editName = dialogView.findViewById(R.id.defn_name);
        editName.setText(name);

        builder
            .setTitle(getString(R.string.edit_name))
            .setView(dialogView)
            .setPositiveButton(getString(R.string.save),
                (dialogInterface, i) -> ((EditInteractionActivity) Objects.requireNonNull(getActivity()))
                    .updateAndCheckFriend(editName.getText().toString()));

        return builder.create();
    }
}
