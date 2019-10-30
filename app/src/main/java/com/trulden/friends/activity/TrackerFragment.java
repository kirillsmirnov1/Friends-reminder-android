package com.trulden.friends.activity;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.trulden.friends.R;
import com.trulden.friends.database.FriendsViewModel;
import com.trulden.friends.database.wrappers.LastInteractionWrapper;

import static com.trulden.friends.util.Util.daysPassed;

/**
 */
public class TrackerFragment extends Fragment {

    private LastInteractionWrapper mLastInteractionWrapper;

    public TrackerFragment() {
        // Required empty public constructor
    }

    public static TrackerFragment newInstance(LastInteractionWrapper lastInteractionWrapper) {
        TrackerFragment fragment = new TrackerFragment();
        fragment.mLastInteractionWrapper = lastInteractionWrapper;
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_tracker, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        FriendsViewModel viewModel = ViewModelProviders.of(getActivity()).get(FriendsViewModel.class);

        ((TextView) view.findViewById(R.id.ft_friends_name))
                .setText(mLastInteractionWrapper.getFriendName());

        ((TextView) view.findViewById(R.id.ft_type))
                .setText(mLastInteractionWrapper.getType().getInteractionTypeName());

        ((TextView) view.findViewById(R.id.ft_time_passed))
                .setText(daysPassed(mLastInteractionWrapper.getLastInteraction()) + getString(R.string.days_ago));

        ((TextView) view.findViewById(R.id.ft_frequency))
                .setText(String.format(getString(R.string.LI_every_x_days), mLastInteractionWrapper.getType().getFrequency()));

        long interactionId = mLastInteractionWrapper.getLastInteraction().getInteractionId();

        ((TextView) view.findViewById(R.id.ft_comment))
                .setText(viewModel.getInteraction(interactionId).getComment());

        //TODO
        //viewModel.getFriendNamesByInteractionId(interactionId);

        //TODO set visible icon status

        //TODO consider using refresh or other icon instead of plus
    }
}
