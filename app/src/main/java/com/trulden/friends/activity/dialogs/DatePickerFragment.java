package com.trulden.friends.activity.dialogs;


import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.trulden.friends.R;
import com.trulden.friends.activity.EditInteractionActivity;

import java.util.Calendar;
import java.util.Objects;

import static com.trulden.friends.util.Util.makeToast;

/**
 * Proposition to choose a date.
 * Checks if picked date is in the future.
 */
public class DatePickerFragment extends DialogFragment{

    private final Calendar calendarInstance;

    public DatePickerFragment(){
        // Required constructor
        calendarInstance = Calendar.getInstance();
    }

    public DatePickerFragment(Calendar calendarInstance){
        if(calendarInstance != null){
            this.calendarInstance = calendarInstance;
        } else {
            this.calendarInstance = Calendar.getInstance();
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        final DatePickerDialog datePickerDialog =
                new DatePickerDialog(Objects.requireNonNull(getActivity()), null,
                                    calendarInstance.get(Calendar.YEAR),
                                    calendarInstance.get(Calendar.MONTH),
                                    calendarInstance.get(Calendar.DAY_OF_MONTH));

        datePickerDialog.setOnShowListener(dialogInterface ->
            datePickerDialog.getButton(DialogInterface.BUTTON_POSITIVE)
                .setOnClickListener(view -> {
                    // Checked if date is in the future

                    Calendar pickedDate = Calendar.getInstance();

                    int year = datePickerDialog.getDatePicker().getYear();
                    int month = datePickerDialog.getDatePicker().getMonth();
                    int day = datePickerDialog.getDatePicker().getDayOfMonth();

                    pickedDate.set(year,month,day);

                    Calendar tomorrow = Calendar.getInstance();
                    tomorrow.add(Calendar.DATE, 1);

                    if(pickedDate.before(tomorrow)) {
                        ((EditInteractionActivity) Objects.requireNonNull(getActivity()))
                                .processDatePickerResult(pickedDate);
                        datePickerDialog.dismiss();
                    } else {
                        makeToast(getActivity(), getString(R.string.set_date_future_warning));
                    }
            }));

        return datePickerDialog;
    }
}
