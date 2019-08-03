package com.trulden.friends;


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
public class TabFriends extends Fragment {
    private LinearLayout mLayout;

    public TabFriends() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.tab_friends, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mLayout = getView().findViewById(R.id.tab_friends_layout);

        for(String person : getResources().getStringArray(R.array.persons_list)) {
            addPerson(person);
        }
    }

    private void addPerson(String person) {
        TextView personView = new TextView(getContext());
        personView.setText(person + "\n");
        mLayout.addView(personView);
    }
}
