package com.trulden.friends.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.trulden.friends.R;

/**
 * View which holds tab name and counter
 */
@SuppressLint("ViewConstructor")
public class TabLabelWithCounterView extends LinearLayout {

    private TextView mCounterView;
    private TextView mLabelView;

    public TabLabelWithCounterView(Context context, String label, int counter) {
        super(context);

        inflate(context, R.layout.view_tab_label_with_counter, this);

        mCounterView = findViewById(R.id.vtlwc_count);
        mLabelView = findViewById(R.id.vtlwc_label);

        setLabelText(label);
        setCounter(counter);
    }

    public void setLabelText(String text){
        mLabelView.setText(text);
    }

    public void setCounter(int counter){
        mCounterView.setText(String.valueOf(counter));
    }
}

