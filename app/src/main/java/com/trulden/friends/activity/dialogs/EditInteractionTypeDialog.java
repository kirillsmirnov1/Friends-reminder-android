package com.trulden.friends.activity.dialogs;

import android.annotation.SuppressLint;
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
import com.trulden.friends.activity.interfaces.EditInteractionType;
import com.trulden.friends.database.entity.InteractionType;

import java.util.Objects;

import static com.trulden.friends.util.Util.makeToast;

/**
 * Dialog in which user can create or change InteractionType
 */
public class EditInteractionTypeDialog extends DialogFragment {

    private static final String KEY_INTERACTION_TYPE = "Interaction type";

    private EditText mName;
    private EditText mFrequency;

    private InteractionType mType;

    public EditInteractionTypeDialog(){
        // Required constructor
    }

    public EditInteractionTypeDialog(InteractionType type){
        mType = type;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder =
                new AlertDialog.Builder(Objects.requireNonNull(getActivity()));

        LayoutInflater inflater = requireActivity().getLayoutInflater();

        @SuppressLint("InflateParams")
        View dialogView = inflater.inflate(R.layout.dialog_edit_interaction_type, null);

        if(savedInstanceState != null && savedInstanceState.containsKey(KEY_INTERACTION_TYPE)){
            mType = (InteractionType) savedInstanceState.getSerializable(KEY_INTERACTION_TYPE);
        }

        builder     // If there is no mType, it means we are creating new one
            .setTitle(mType == null ? getString(R.string.new_interaction_type) : getString(R.string.edit_interaction_type))
            .setView(dialogView)
            .setPositiveButton(getString(R.string.save), null)     // Set it later
            .setNegativeButton(getString(R.string.discard), null); // Need only text on that one

        final AlertDialog dialog = builder.create();

        mName = dialogView.findViewById(R.id.deit_name);
        mFrequency = dialogView.findViewById(R.id.deit_frequency);

        if(mType != null){
            mName.setText(mType.getInteractionTypeName());
            mFrequency.setText(String.valueOf(mType.getFrequency()));
        }

        // To check values in dialog, I need to set listener like that
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {

                dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                        .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String name = mName.getText().toString();

                        int newFrequency = -1;
                        String enteredFrequencyString = mFrequency.getText().toString();

                        EditInteractionType parentActivity = (EditInteractionType) getActivity();

                        if(name.isEmpty()){
                            makeToast(getActivity(), getString(R.string.toast_warning_empty_name));
                            return;
                        }

                        assert parentActivity != null;
                        if(mType == null && parentActivity.typeExists(name)){
                            makeToast(getActivity(), getString(R.string.toast_warning_type_exists));
                            return;
                        }

                        if(enteredFrequencyString.isEmpty()){
                            makeToast(getActivity(), getString(R.string.toast_warning_empty_frequency));
                            return;
                        }

                        if(enteredFrequencyString.length() > 9){
                            makeToast(getActivity(), getString(R.string.frequency_cant_be_that_long));
                            return;
                        }

                        try {
                            newFrequency = Integer.parseInt(enteredFrequencyString);
                        } catch (NumberFormatException e){
                            e.printStackTrace();
                        }

                        if(newFrequency < 0){
                            makeToast(getActivity(), getString(R.string.unknown_error_in_frequecny));
                            return;
                        }

                        if(newFrequency == 0){
                            makeToast(getActivity(), getString(R.string.frequency_cant_be_zero));
                            return;
                        }

                        if(mType == null){
                            mType = new InteractionType(name, newFrequency);

                            makeToast(getActivity(), getString(R.string.toast_notice_type_created));
                        } else {
                            mType.setInteractionTypeName(name);
                            mType.setFrequency(newFrequency);

                            makeToast(getActivity(), getString(R.string.type_updated));
                        }

                        // If everything is fine — pass type information back to activity
                        parentActivity.saveType(mType);

                        dialog.dismiss();
                    }
                });
            }
        });

        return dialog;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(KEY_INTERACTION_TYPE, mType);
    }
}
