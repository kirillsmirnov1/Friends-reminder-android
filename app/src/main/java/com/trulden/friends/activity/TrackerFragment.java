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
import com.trulden.friends.activity.dialogs.EditLastInteractionFrequencyDialog;
import com.trulden.friends.activity.interfaces.TrackerOverActivity;
import com.trulden.friends.database.FriendsViewModel;
import com.trulden.friends.database.wrappers.LastInteractionWrapper;

import static com.trulden.friends.util.Util.EXTRA_INTERACTION_FRIEND_NAMES;
import static com.trulden.friends.util.Util.EXTRA_INTERACTION_TYPE_ID;
import static com.trulden.friends.util.Util.EXTRA_INTERACTION_TYPE_NAME;
import static com.trulden.friends.util.Util.NEW_INTERACTION_REQUEST;
import static com.trulden.friends.util.Util.daysPassed;
import static com.trulden.friends.util.Util.openFriendsPage;

/**
 * Shows {@link com.trulden.friends.database.entity.LastInteraction LastInteraction} entry.
 * Shows comment from interaction.
 */
public class TrackerFragment extends Fragment  implements View.OnClickListener {

    private FriendsViewModel mViewModel;

    private long mTypeId;
    private long mFriendId;

    private LastInteractionWrapper mLastInteractionWrapper;

    private TextView mWithWhom;
    private TextView mComment;

    private ImageView mStatusIcon;

    public TrackerFragment() {
        // Required empty public constructor
    }

    public static TrackerFragment newInstance(long typeId, long friendId) {
        TrackerFragment fragment = new TrackerFragment();
        fragment.mTypeId = typeId;
        fragment.mFriendId = friendId;
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
        ImageView createInteractionIcon = view.findViewById(R.id.ft_create_interaction_icon);
        ImageView changeFrequencyIcon = view.findViewById(R.id.ft_change_frequency_icon);
        mWithWhom = view.findViewById(R.id.ft_with_whom);
        mComment = view.findViewById(R.id.ft_comment);

        mViewModel = ViewModelProviders.of(getActivity()).get(FriendsViewModel.class);

        if(mTypeId == 0 || mFriendId == 0){
            mLastInteractionWrapper = mViewModel.getTrackerInFragment();
            mTypeId = mLastInteractionWrapper.getType().getId();
            mFriendId = mLastInteractionWrapper.getFriend().getId();
        }

        mViewModel.getLiveLastInteractionWrapper(mTypeId, mFriendId).observe(getViewLifecycleOwner(), lastInteractionWrapper -> {
            mLastInteractionWrapper = lastInteractionWrapper;

            long interactionId = mLastInteractionWrapper.getLastInteraction().getInteractionId();
            String friendsName = mLastInteractionWrapper.getFriendName();

            friendsNameView.setText(friendsName);

            ((TextView) view.findViewById(R.id.ft_type))
                    .setText(mLastInteractionWrapper.getType().getInteractionTypeName());

            ((TextView) view.findViewById(R.id.ft_time_passed))
                    .setText(daysPassed(mLastInteractionWrapper.getLastInteraction()) + getString(R.string.days_ago));

            ((TextView) view.findViewById(R.id.ft_frequency))
                    .setText(String.format(getString(R.string.LI_every_x_days), mLastInteractionWrapper.getLastInteraction().getFrequency()));

            setStatusIcon();

            mViewModel.getInteraction(interactionId)
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

            mViewModel.getCoParticipantNames(interactionId, friendsName).observe(getViewLifecycleOwner(),
                    namesList -> {
                        if(namesList.size() == 0){
                            mWithWhom.setVisibility(View.GONE);
                        } else {
                            String names = getString(R.string.with) + TextUtils.join(", ", namesList);
                            mWithWhom.setVisibility(View.VISIBLE);
                            mWithWhom.setText(names);
                        }
                    });
        });

        friendsNameView.setOnClickListener(v -> {
            ((TrackerOverActivity) getActivity()).closeTrackerOverActivity();
            if(getActivity() instanceof MainActivity){
                openFriendsPage(getActivity(), mLastInteractionWrapper.getFriend());
            }
        });

        mStatusIcon.setOnClickListener(v -> {
            mLastInteractionWrapper.getLastInteraction().setStatus(
                mLastInteractionWrapper.getLastInteraction().getStatus() == 0
                ? 1
                : 0);
            setStatusIcon();
            mViewModel.update(mLastInteractionWrapper.getLastInteraction());
        });

        createInteractionIcon.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), EditInteractionActivity.class);
            intent.putExtra(EXTRA_INTERACTION_FRIEND_NAMES, mLastInteractionWrapper.getFriendName());
            intent.putExtra(EXTRA_INTERACTION_TYPE_NAME, mLastInteractionWrapper.getType().getInteractionTypeName());
            intent.putExtra(EXTRA_INTERACTION_TYPE_ID, mLastInteractionWrapper.getType().getId());

            ((TrackerOverActivity) getActivity()).closeTrackerOverActivity();

            getActivity().startActivityForResult(intent, NEW_INTERACTION_REQUEST);
        });

        changeFrequencyIcon.setOnClickListener(v ->{
            new EditLastInteractionFrequencyDialog(mLastInteractionWrapper)
                .show(getActivity().getSupportFragmentManager(), "editLIFrequency");
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
        // Class has to extend onClickListener so clicks won't fall through
        // But clicks on the view itself are useless, so this method is empty
        // Clicks on elements are handled in «setOnClickListener» methods in onViewCreated
    }

    @Override
    public void onDetach() {
        super.onDetach();

        mViewModel.setTrackerInFragment(mLastInteractionWrapper);
    }
}
