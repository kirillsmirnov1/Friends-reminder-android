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
    private String type;

    private final static String TYPE_ARGUMENT = "TYPE_ARGUMENT";

    public final static String MEETINGS_TAG = "MEETINGS_TAG";
    public final static String TEXTING_TAG = "TEXTING_TAG";
    public final static String CALLS_TAG = "CALLS_TAG";

    public TabReminder() {
        // Required empty public constructor
    }

    public static TabReminder newInstance(String type){
        TabReminder tr = new TabReminder();

        Bundle args = new Bundle();
        args.putString(TYPE_ARGUMENT, type);
        tr.setArguments(args);

        return tr;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        type = getArguments().getString(TYPE_ARGUMENT);
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

        switch(type) {
            case MEETINGS_TAG:
                addBlock(getResources().getStringArray(R.array.meetings_a_while_ago));
                break;
            case TEXTING_TAG:
                addBlock(getResources().getStringArray(R.array.texting_a_while_ago));
                break;
            case CALLS_TAG:
                addBlock(getResources().getStringArray(R.array.call_a_while_ago));
                break;
        }
    }

    private void addBlock(String[] entries){

        if(entries.length > 0){
            for(String entry : entries){
                TextView m = new TextView(getContext());
                m.setText(entry);
                mLayout.addView(m);
            }
        }

    }
}
