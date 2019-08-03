package com.trulden.friends;


import android.os.Bundle;
import android.widget.LinearLayout;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * A simple {@link Fragment} subclass.
 */
public class TabLog extends Fragment {
    private LinearLayout mLayout;

    public TabLog() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.tab_log, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mLayout = getView().findViewById(R.id.tab_log_layout);

        for(String entry : getResources().getStringArray(R.array.log_list)){
            addEntry(entry);
        }
    }

    private void addEntry(String entry) {
        TextView entryView = new TextView(getContext());
        entryView.setText(entry + "\n");
        mLayout.addView(entryView);
    }
}
