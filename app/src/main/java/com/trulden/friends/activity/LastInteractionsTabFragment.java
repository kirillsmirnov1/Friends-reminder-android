package com.trulden.friends.activity;


import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.trulden.friends.R;
import com.trulden.friends.adapter.LastInteractionsAdapter;

import java.util.ArrayList;
import java.util.Arrays;


/**
 * A simple {@link Fragment} subclass.
 */
public class LastInteractionsTabFragment extends Fragment {
    private String type;

    private final static String TYPE_ARGUMENT = "TYPE_ARGUMENT";

    public final static String MEETINGS_TAG = "MEETINGS_TAG"; // TODO тэги должны генериться на лету и точно не здесь
    public final static String TEXTING_TAG = "TEXTING_TAG";
    public final static String CALLS_TAG = "CALLS_TAG";

    private ArrayList<String> mReminderData = new ArrayList<>();

    public LastInteractionsTabFragment() {
        // Required empty public constructor
    }

    public static LastInteractionsTabFragment newInstance(String type){
        LastInteractionsTabFragment tr = new LastInteractionsTabFragment();

        Bundle args = new Bundle();
        args.putString(TYPE_ARGUMENT, type);
        tr.setArguments(args);

        return tr;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        type = getArguments().getString(TYPE_ARGUMENT);
        switch (type){ // TODO эти данные не должны быть зашиты в R
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
        return inflater.inflate(R.layout.tab_reminder_content, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView recyclerView = view.findViewById(R.id.tab_reminder_recyclerview);
        RecyclerView.LayoutManager mLayout = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mLayout);

        LastInteractionsAdapter mAdapter = new LastInteractionsAdapter(getContext(), mReminderData);
        recyclerView.setAdapter(mAdapter);
    }
}
