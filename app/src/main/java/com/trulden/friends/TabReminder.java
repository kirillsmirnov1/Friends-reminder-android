package com.trulden.friends;


import android.graphics.Typeface;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


/**
 * A simple {@link Fragment} subclass.
 */
public class TabReminder extends Fragment {
    private LinearLayout mLayout;

    public TabReminder() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.tab_reminder, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mLayout = getView().findViewById(R.id.tab_reminder_layout);

        addBlock("Meetings", getResources().getStringArray(R.array.meetings_a_while_ago));
        addBlock("\nTextings", getResources().getStringArray(R.array.texting_a_while_ago));
        addBlock("\nCalls", getResources().getStringArray(R.array.call_a_while_ago));
    }

    private void addBlock(String name, String[] entries){

        if(entries.length > 0){

            TextView meetingsLabel = new TextView(getContext());
            meetingsLabel.setText(name);
            meetingsLabel.setTypeface(null, Typeface.BOLD);
            mLayout.addView(meetingsLabel);

            for(String entry : entries){
                TextView m = new TextView(getContext());
                m.setText(entry);
                mLayout.addView(m);
            }
        }

    }
}