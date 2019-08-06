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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Arrays;


/**
 * A simple {@link Fragment} subclass.
 */
public class TabReminder extends Fragment {
    private RecyclerView.LayoutManager mLayout;
    private String type;

    private final static String TYPE_ARGUMENT = "TYPE_ARGUMENT";

    public final static String MEETINGS_TAG = "MEETINGS_TAG";
    public final static String TEXTING_TAG = "TEXTING_TAG";
    public final static String CALLS_TAG = "CALLS_TAG";

    private ArrayList<String> mReminderData = new ArrayList<>();

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
        switch (type){
            case MEETINGS_TAG:
                mReminderData.addAll(Arrays.asList(getResources().getStringArray(R.array.meetings_a_while_ago)));
                break;
            case TEXTING_TAG:
                mReminderData.addAll(Arrays.asList(getResources().getStringArray(R.array.texting_a_while_ago)));
                break;
            case CALLS_TAG:
                mReminderData.addAll(Arrays.asList(getResources().getStringArray(R.array.call_a_while_ago)));
                break;
        }

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

        RecyclerView recyclerView = view.findViewById(R.id.tab_reminder_recyclerview);
        mLayout = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mLayout);

        ReminderAdapter mAdapter = new ReminderAdapter(getContext(), mReminderData);
        recyclerView.setAdapter(mAdapter);
    }
}
