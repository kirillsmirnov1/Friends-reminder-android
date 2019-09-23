package com.trulden.friends.activity;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.trulden.friends.R;
import com.trulden.friends.adapter.LastInteractionsAdapter;
import com.trulden.friends.database.entity.InteractionType;
import com.trulden.friends.database.entity.LastInteraction;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class LastInteractionsTabFragment extends Fragment {

    private ArrayList<LastInteraction> mLastInteractions = new ArrayList<>();

    public LastInteractionsTabFragment() {
        // Required empty public constructor
    }

    public static LastInteractionsTabFragment newInstance(InteractionType type, ArrayList<LastInteraction> lastInteractions){
        LastInteractionsTabFragment tr = new LastInteractionsTabFragment();

        tr.setLastInteractions(lastInteractions);

        return tr;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    public void setLastInteractions(ArrayList<LastInteraction> mLastInteractions) {
        this.mLastInteractions = mLastInteractions;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.tab_last_interaction_content, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView recyclerView = view.findViewById(R.id.tab_last_interaction_recyclerview);
        RecyclerView.LayoutManager mLayout = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mLayout);

        LastInteractionsAdapter mAdapter = new LastInteractionsAdapter(getContext());
        mAdapter.setEntries(mLastInteractions);
        recyclerView.setAdapter(mAdapter);
    }
}
