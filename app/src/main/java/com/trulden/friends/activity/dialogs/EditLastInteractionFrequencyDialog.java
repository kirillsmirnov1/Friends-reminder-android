package com.trulden.friends.activity.dialogs;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProviders;

import com.trulden.friends.R;
import com.trulden.friends.activity.interfaces.TrackerOverActivity;
import com.trulden.friends.database.FriendsViewModel;
import com.trulden.friends.database.entity.LastInteraction;
import com.trulden.friends.database.wrappers.LastInteractionWrapper;

import java.util.Objects;

import static com.trulden.friends.util.Util.makeToast;

public class EditLastInteractionFrequencyDialog extends DialogFragment {

    private LastInteractionWrapper mLastInteractionWrapper;
    private FriendsViewModel mViewModel;

    public EditLastInteractionFrequencyDialog(){
        // Required constructor
    }

    public EditLastInteractionFrequencyDialog(LastInteractionWrapper lastInteractionWrapper){
        mLastInteractionWrapper = lastInteractionWrapper;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder =
                new AlertDialog.Builder(Objects.requireNonNull(getActivity()));

        LayoutInflater inflater = requireActivity().getLayoutInflater();

        @SuppressLint("InflateParams")
        View dialogView = inflater.inflate(R.layout.dialog_edit_last_interaction_frequency, null);

        mViewModel = ViewModelProviders.of(this).get(FriendsViewModel.class);

        if(mLastInteractionWrapper == null){
            mLastInteractionWrapper = mViewModel.getTrackerInFragment();
        }

        builder
            .setTitle(getString(R.string.edit_frequency))
            .setView(dialogView)
            .setPositiveButton(R.string.save, null)
            .setNegativeButton(R.string.discard, null);

        final AlertDialog dialog = builder.create();

        TextView hintView = dialogView.findViewById(R.id.delif_hint);
        EditText editFrequency = dialogView.findViewById(R.id.delif_frequency);

        dialog.setOnShowListener(dialogInterface -> {

            String type = mLastInteractionWrapper.getType().getInteractionTypeName();
            String friend = mLastInteractionWrapper.getFriendName();
            long oldFrequency = mLastInteractionWrapper.getLastInteraction().getFrequency();
            //TODO завернуть getTypeName и getFrequency в wrapper, чтобы не нужно было образаться к внутренним объектам

            String hintText = String.format(
                    getString(R.string.current_frequency_type_friend), type, friend, oldFrequency);

            hintView.setText(hintText);

            dialog.getButton(DialogInterface.BUTTON_POSITIVE)
                .setOnClickListener(view -> {
                    LastInteraction lastInteraction = mLastInteractionWrapper.getLastInteraction();
                    int newFrequency = -1;
                    String enteredFrequencyString = editFrequency.getText().toString();

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

                    lastInteraction.setFrequency(newFrequency);

                    ((TrackerOverActivity) getActivity()).updateLastInteraction(lastInteraction);
                    dialog.dismiss();
                });
        });

        return dialog;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        mViewModel.setTrackerInFragment(mLastInteractionWrapper);
    }
}
