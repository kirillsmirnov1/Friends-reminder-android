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
            .setPositiveButton("Save", null)     // Set it later
            .setNegativeButton("Discard", null); // Need only text on that one

        final AlertDialog dialog = builder.create();

        // To check values in dialog, I need to set listener like that

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                        .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String name = mName.getText().toString();
                        int frequency = -1;

                        try {
                            frequency = Integer.parseInt(mFrequency.getText().toString());
                        } catch (NumberFormatException e){
                            e.printStackTrace();
                        }

                        if(name.isEmpty()){
                            makeToast(getActivity(), "Empty name!");
                            return;
                        }

                        if(frequency < 0){
                            makeToast(getActivity(), "Empty frequency!");
                            return;
                        }

                        // If everything is fine — pass type information back to activity
                        ((EditInteractionActivity)getActivity())
                                .createInteractionType(name, frequency);

                        dialog.dismiss();
                    }
                });
            }
        });

        mName = dialogView.findViewById(R.id.edit_interaction_type_name);
        mFrequency = dialogView.findViewById(R.id.edit_interaction_type_frequency);

        return dialog;
    }
}
