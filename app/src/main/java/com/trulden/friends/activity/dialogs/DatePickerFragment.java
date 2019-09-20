package com.trulden.friends.activity.dialogs;


import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.widget.DatePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.trulden.friends.activity.EditInteractionActivity;

import java.util.Calendar;
import java.util.Objects;

/**
 * Proposition to choose a date
 */
public class DatePickerFragment extends DialogFragment
        implements DatePickerDialog.OnDateSetListener {

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        final Calendar c = Calendar.getInstance();

        return new DatePickerDialog(Objects.requireNonNull(getActivity()), this,
                c.get(Calendar.YEAR),
                c.get(Calendar.MONTH),
                c.get(Calendar.DAY_OF_MONTH));
    }

    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
        ((EditInteractionActivity) Objects.requireNonNull(getActivity()))
                .processDatePickerResult(year, month + 1, day);
    }
}
