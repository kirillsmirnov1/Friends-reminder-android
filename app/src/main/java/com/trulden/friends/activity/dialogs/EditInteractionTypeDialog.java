package com.trulden.friends.activity.dialogs;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.trulden.friends.R;
import com.trulden.friends.activity.EditInteractionActivity;

import static com.trulden.friends.util.Util.*;

public class EditInteractionTypeDialog extends DialogFragment {

    private EditText mName;
    private EditText mFrequency;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_edit_interaction_type, null);

        builder
            .setTitle("Edit interaction type")
            .setView(dialogView)
            .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    ((EditInteractionActivity)getActivity()) // FIXME mName is null
                            .createInteractionType(mName.getText().toString(), Integer.parseInt(mFrequency.getText().toString()));
                }
            })
            .setNegativeButton("Discard", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                }
            });

        Dialog dialog = builder.create();

        mName = dialogView.findViewById(R.id.edit_interaction_type_name);
        mFrequency = dialogView.findViewById(R.id.edit_interaction_type_frequency);

        return dialog;
    }
}
