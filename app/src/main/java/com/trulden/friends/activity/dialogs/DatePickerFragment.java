package com.trulden.friends.activity.dialogs;


import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.widget.DatePicker;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import com.trulden.friends.activity.AddInteractionActivity;

import java.util.Calendar;

/**
 * A simple {@link Fragment} subclass.
 */
public class DatePickerFragment extends DialogFragment
        implements DatePickerDialog.OnDateSetListener {

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        final Calendar c = Calendar.getInstance();

        return new DatePickerDialog(getActivity(), this,
                c.get(Calendar.YEAR),
                c.get(Calendar.MONTH),
                c.get(Calendar.DAY_OF_MONTH));
    }

    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
        ((AddInteractionActivity)getActivity()).processDatePickerResult(year, month + 1, day);
    }
}
