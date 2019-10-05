package com.trulden.friends.activity;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.trulden.friends.R;
import com.trulden.friends.adapter.LastInteractionsAdapter;
import com.trulden.friends.database.wrappers.LastInteraction;

import java.util.ArrayList;


/**
 * Holds {@link LastInteraction} entries of specific type
 */
public class LastInteractionsTabFragment extends Fragment {

    private ArrayList<LastInteraction> mLastInteractions = new ArrayList<>();

    private TextView noDataTextView;

    private String noDataText;

    public LastInteractionsTabFragment() {
        // Required empty public constructor
    }

    public static LastInteractionsTabFragment newInstance(ArrayList<LastInteraction> lastInteractions,String name){
        LastInteractionsTabFragment tr = new LastInteractionsTabFragment();
        tr.noDataText = "You have no "+name.toLowerCase()+"s";
        tr.setLastInteractions(lastInteractions);

        return tr;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    private void setLastInteractions(ArrayList<LastInteraction> lastInteractions) {
        mLastInteractions = lastInteractions;
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

        noDataTextView = view.findViewById(R.id.last_interaction_no_data);
        noDataTextView.setText(noDataText);

        RecyclerView recyclerView = view.findViewById(R.id.tab_last_interaction_recyclerview);
        RecyclerView.LayoutManager mLayout = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mLayout);

        LastInteractionsAdapter mAdapter = new LastInteractionsAdapter(getContext());
        mAdapter.setEntries(mLastInteractions);
        recyclerView.setAdapter(mAdapter);

        // Might be unnecessary, might fix issue with empty LI tab
        mAdapter.notifyDataSetChanged();
        changeNoDataTextVisibility();
    }

    private void changeNoDataTextVisibility(){
        if(mLastInteractions == null || mLastInteractions.size() == 0){
            noDataTextView.setVisibility(View.VISIBLE);
        }else{
            noDataTextView.setVisibility(View.GONE);
        }
    }
}
