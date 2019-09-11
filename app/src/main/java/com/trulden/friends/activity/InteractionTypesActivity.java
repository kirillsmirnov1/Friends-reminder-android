package com.trulden.friends.activity;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;

import com.trulden.friends.R;
import com.trulden.friends.adapter.InteractionTypeAdapter;
import com.trulden.friends.database.FriendsViewModel;

public class InteractionTypesActivity extends AppCompatActivity {

    private FriendsViewModel mFriendsViewModel;

    private InteractionTypeAdapter mInteractionTypeAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_interaction_types);

        mFriendsViewModel = ViewModelProviders.of(this).get(FriendsViewModel.class);
    }

}
