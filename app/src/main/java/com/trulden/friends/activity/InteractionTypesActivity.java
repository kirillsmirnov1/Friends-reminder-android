package com.trulden.friends.activity;

import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProviders;

import android.view.View;

import com.trulden.friends.R;
import com.trulden.friends.database.FriendsViewModel;

public class InteractionTypesActivity extends AppCompatActivity {

    private FriendsViewModel mFriendsViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_interaction_types);

        mFriendsViewModel = ViewModelProviders.of(this).get(FriendsViewModel.class);
    }

}
