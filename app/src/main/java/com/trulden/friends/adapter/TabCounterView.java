package com.trulden.friends.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.trulden.friends.R;

@SuppressLint("ViewConstructor")
/**
 * View which holds tab name and counter
 */
public class TabCounterView extends LinearLayout {

    private TextView mCounterView;
    private TextView mLabelView;

    public TabCounterView(Context context, String label, int counter) {
        super(context);

        inflate(context, R.layout.tab_counter_view, this);

        mCounterView = findViewById(R.id.tab_content_count);
        mLabelView = findViewById(R.id.tab_label);

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
