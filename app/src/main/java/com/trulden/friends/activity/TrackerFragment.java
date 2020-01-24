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
import com.trulden.friends.database.wrappers.LastInteractionWrapper;
import com.trulden.friends.database.TrackerViewModel;
import com.trulden.friends.database.TrackerViewModelFactory;

import static com.trulden.friends.util.Util.FRIEND_ID;
import static com.trulden.friends.util.Util.FRIEND_NAME;
import static com.trulden.friends.util.Util.INTERACTION_FRIEND_NAMES;
import static com.trulden.friends.util.Util.INTERACTION_ID;
import static com.trulden.friends.util.Util.INTERACTION_TYPE_ID;
import static com.trulden.friends.util.Util.INTERACTION_TYPE_NAME;
import static com.trulden.friends.util.Util.LAST_INTERACTION;
import static com.trulden.friends.util.Util.NEW_INTERACTION_REQUEST;
import static com.trulden.friends.util.Util.UPDATE_INTERACTION_REQUEST;
import static com.trulden.friends.util.Util.daysPassed;
import static com.trulden.friends.util.Util.openFriendsPage;

/**
 * Shows {@link com.trulden.friends.database.entity.LastInteraction LastInteraction} entry.
 * Shows comment from interaction.
 */
public class TrackerFragment extends Fragment  implements View.OnClickListener {

    private TrackerViewModel mTrackerViewModel;

    private LastInteractionWrapper mLastInteractionWrapper;

    private TextView mWithWhom;
    private TextView mComment;

    private ImageView mStatusIcon;

    public TrackerFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        long typeId = getArguments().getLong(INTERACTION_TYPE_ID);
        long friendId = getArguments().getLong(FRIEND_ID);

        // Some resources say, creating new factory will cause recreation of VM
        // Checked that with log and it seems that VM itself survives configuration change
        mTrackerViewModel = ViewModelProviders
            .of(this,
                new TrackerViewModelFactory(this.getActivity().getApplication(), friendId, typeId))
            .get(TrackerViewModel.class);

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_tracker, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        view.setOnClickListener(this);

        // FIXME move to onCreateView
        TextView friendsNameView = view.findViewById(R.id.ft_friends_name);
        mStatusIcon = view.findViewById(R.id.ft_status_icon);
        ImageView createInteractionIcon = view.findViewById(R.id.ft_create_interaction_icon);
        ImageView changeFrequencyIcon = view.findViewById(R.id.ft_change_frequency_icon);
        mWithWhom = view.findViewById(R.id.ft_with_whom);
        mComment = view.findViewById(R.id.ft_comment);

        mTrackerViewModel.getLastInteractionWrapper().observe(getViewLifecycleOwner(), lastInteractionWrapper -> {
            mLastInteractionWrapper = lastInteractionWrapper;

            String friendName = mLastInteractionWrapper.getFriendName();

            friendsNameView.setText(friendName);

            ((TextView) view.findViewById(R.id.ft_type))
                    .setText(mLastInteractionWrapper.getTypeName());

            ((TextView) view.findViewById(R.id.ft_time_passed))
                    .setText(daysPassed(mLastInteractionWrapper.getLastInteraction()) + getString(R.string.days_ago));

            ((TextView) view.findViewById(R.id.ft_frequency))
                    .setText(String.format(getString(R.string.LI_every_x_days), mLastInteractionWrapper.getFrequency()));

            setStatusIcon();

            mTrackerViewModel.getInteraction()
                    .observe(
                            getViewLifecycleOwner(),
                            interaction -> {
                                String comment = interaction.getComment(); // FIXME get only string
                                if(comment.isEmpty()){
                                    mComment.setHint(R.string.no_description);
                                } else {
                                    mComment.setText(comment);
                                }
                            });

            mTrackerViewModel.getCoParticipantNames().observe(getViewLifecycleOwner(),
                    namesList -> {
                        if(namesList.size() == 0){ // FIXME transform in VM?
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
            mTrackerViewModel.update(mLastInteractionWrapper.getLastInteraction());
        });

        createInteractionIcon.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), EditInteractionActivity.class);
            intent.putExtra(INTERACTION_FRIEND_NAMES, mLastInteractionWrapper.getFriendName());
            intent.putExtra(INTERACTION_TYPE_NAME, mLastInteractionWrapper.getTypeName());
            intent.putExtra(INTERACTION_TYPE_ID, mLastInteractionWrapper.getType().getId());

            ((TrackerOverActivity) getActivity()).closeTrackerOverActivity();

            getActivity().startActivityForResult(intent, NEW_INTERACTION_REQUEST);
        });

        changeFrequencyIcon.setOnClickListener(v -> {
            EditLastInteractionFrequencyDialog dialog = new EditLastInteractionFrequencyDialog();

            Bundle bundle = new Bundle();

            bundle.putString(INTERACTION_TYPE_NAME, mLastInteractionWrapper.getTypeName());
            bundle.putString(FRIEND_NAME, mLastInteractionWrapper.getFriendName());
            bundle.putSerializable(LAST_INTERACTION, mLastInteractionWrapper.getLastInteraction());

            dialog.setArguments(bundle);

            dialog.show(getActivity().getSupportFragmentManager(), "editLIFrequency");
        });

        mComment.setOnLongClickListener(v -> {
            Intent intent = new Intent(getContext(), EditInteractionActivity.class);

            intent.putExtra(INTERACTION_ID, mLastInteractionWrapper.getLastInteraction().getInteractionId());

            getActivity().startActivityForResult(intent, UPDATE_INTERACTION_REQUEST);

            return true;
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
    }
}
