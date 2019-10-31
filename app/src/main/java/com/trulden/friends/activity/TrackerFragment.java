package com.trulden.friends.activity;

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

import static com.trulden.friends.util.Util.daysPassed;
import static com.trulden.friends.util.Util.makeToast;

/**
 */
public class TrackerFragment extends Fragment  implements View.OnClickListener {

    private LastInteractionWrapper mLastInteractionWrapper;

    private TextView mComment;
    private TextView mWithWhom;

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

        mComment = view.findViewById(R.id.ft_comment);
        mWithWhom = view.findViewById(R.id.ft_with_whom);
        mStatusIcon = view.findViewById(R.id.ft_status_icon);

        long interactionId = mLastInteractionWrapper.getLastInteraction().getInteractionId();
        String friendsName = mLastInteractionWrapper.getFriendName();

        FriendsViewModel viewModel = ViewModelProviders.of(getActivity()).get(FriendsViewModel.class);

        ((TextView) view.findViewById(R.id.ft_friends_name))
                .setText(friendsName);

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

        //TODO consider using refresh or other icon instead of plus
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
        // TODO
    }
}
