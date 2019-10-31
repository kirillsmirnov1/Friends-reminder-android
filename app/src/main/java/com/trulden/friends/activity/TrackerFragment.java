package com.trulden.friends.activity;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.trulden.friends.R;
import com.trulden.friends.database.FriendsViewModel;
import com.trulden.friends.database.wrappers.LastInteractionWrapper;

import static com.trulden.friends.util.Util.EXTRA_FRIEND_NAME;
import static com.trulden.friends.util.Util.EXTRA_INTERACTION_FRIEND_NAMES;
import static com.trulden.friends.util.Util.EXTRA_INTERACTION_TYPE_ID;
import static com.trulden.friends.util.Util.EXTRA_INTERACTION_TYPE_NAME;
import static com.trulden.friends.util.Util.NEW_INTERACTION_REQUEST;
import static com.trulden.friends.util.Util.daysPassed;
import static com.trulden.friends.util.Util.makeToast;
import static com.trulden.friends.util.Util.openFriendsPage;

/**
 */
public class TrackerFragment extends Fragment  implements View.OnClickListener {

    private LastInteractionWrapper mLastInteractionWrapper;

    private TextView mWithWhom;
    private TextView mComment;

    private ImageView mStatusIcon;

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

        view.setOnClickListener(this);

        TextView friendsNameView = view.findViewById(R.id.ft_friends_name);
        mStatusIcon = view.findViewById(R.id.ft_status_icon);
        ImageView updateIcon = view.findViewById(R.id.ft_update_icon);
        mWithWhom = view.findViewById(R.id.ft_with_whom);
        mComment = view.findViewById(R.id.ft_comment);

        long interactionId = mLastInteractionWrapper.getLastInteraction().getInteractionId();
        String friendsName = mLastInteractionWrapper.getFriendName();

        FriendsViewModel viewModel = ViewModelProviders.of(getActivity()).get(FriendsViewModel.class);

        friendsNameView.setText(friendsName);

        ((TextView) view.findViewById(R.id.ft_type))
                .setText(mLastInteractionWrapper.getType().getInteractionTypeName());

        ((TextView) view.findViewById(R.id.ft_time_passed))
                .setText(daysPassed(mLastInteractionWrapper.getLastInteraction()) + getString(R.string.days_ago));

        ((TextView) view.findViewById(R.id.ft_frequency))
                .setText(String.format(getString(R.string.LI_every_x_days), mLastInteractionWrapper.getType().getFrequency()));

        setStatusIcon();

        viewModel.getInteraction(interactionId)
            .observe(
                getViewLifecycleOwner(),
                interactions ->{
                    String comment = interactions.get(0).getComment();
                    if(comment.isEmpty()){
                        mComment.setHint(R.string.no_description);
                    } else {
                        mComment.setText(comment);
                    }
                });

        viewModel.getCoParticipantNames(interactionId, friendsName).observe(getViewLifecycleOwner(),
            namesList -> {
                if(namesList.size() == 0){
                    mWithWhom.setVisibility(View.GONE);
                } else {
                    String names = getString(R.string.with) + TextUtils.join(", ", namesList);
                    mWithWhom.setVisibility(View.VISIBLE);
                    mWithWhom.setText(names);
                }
            });

        friendsNameView.setOnClickListener(v -> {
            ((MainActivity) getActivity()).closeTrackerOver();
            openFriendsPage(getActivity(), mLastInteractionWrapper.getFriend());
        });

        mStatusIcon.setOnClickListener(v -> {
            mLastInteractionWrapper.getLastInteraction().setStatus(
                mLastInteractionWrapper.getLastInteraction().getStatus() == 0
                ? 1
                : 0);
            setStatusIcon();
            viewModel.update(mLastInteractionWrapper.getLastInteraction());
        });

        updateIcon.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), EditInteractionActivity.class);
            intent.putExtra(EXTRA_INTERACTION_FRIEND_NAMES, mLastInteractionWrapper.getFriendName());
            intent.putExtra(EXTRA_INTERACTION_TYPE_NAME, mLastInteractionWrapper.getType().getInteractionTypeName());
            intent.putExtra(EXTRA_INTERACTION_TYPE_ID, mLastInteractionWrapper.getType().getId());

            ((MainActivity) getActivity()).closeTrackerOver();

            getActivity().startActivityForResult(intent, NEW_INTERACTION_REQUEST);
        });
    }

    private void setStatusIcon() {
        int drawableId;
        if(mLastInteractionWrapper.getLastInteraction().getStatus() == 0){
            drawableId = R.drawable.ic_visibility_24dp;
        } else {
            drawableId = R.drawable.ic_visibility_off_24dp;
        }
        mStatusIcon.setImageDrawable(ContextCompat.getDrawable(getContext(), drawableId));
    }

    @Override
    public void onClick(View v) {

    }
}
