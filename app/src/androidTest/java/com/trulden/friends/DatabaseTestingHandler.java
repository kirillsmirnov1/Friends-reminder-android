package com.trulden.friends;

import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProviders;

import com.trulden.friends.database.FriendsDao;
import com.trulden.friends.database.FriendsViewModel;
import com.trulden.friends.database.entity.BindFriendInteraction;
import com.trulden.friends.database.entity.Friend;
import com.trulden.friends.database.entity.Interaction;
import com.trulden.friends.database.entity.InteractionType;
import com.trulden.friends.database.entity.LastInteraction;

import java.util.Calendar;

public class DatabaseTestingHandler {

    static public InteractionType[] types;
    static public Friend[] friends;
    static public Interaction[] interactions;
    static public BindFriendInteraction[] binds;
    static public LastInteraction[] lastInteractions;

    public static void initAndFillDatabase(FragmentActivity activity){

        FriendsViewModel mFriendsViewModel = ViewModelProviders.of(activity).get(FriendsViewModel.class);
        FriendsDao dao = mFriendsViewModel.getDao();

        dao.wipeTypes();
        dao.wipeFriends();
        dao.wipeInteractions();
        dao.wipeBinds();

        types = new InteractionType[]{
                new InteractionType(1, "Meeting", 30),
                new InteractionType(2, "Texting", 7)
        };

        friends = new Friend[]{
                new Friend(1, "Aaron", "A prophet, high priest, and the brother of Moses"),
                new Friend(2, "Balaam", "Told King Balak how to get the Israelites to commit sin by enticing them with sexual immorality and food sacrificed to idols"),
                new Friend(3, "Caleb", "One of the twelve spies sent by Moses into Canaan")
        };

        Calendar[] dates = {Calendar.getInstance(), Calendar.getInstance(), Calendar.getInstance()};

        dates[0].add(Calendar.DATE, -29);
        dates[1].add(Calendar.DATE, -30);
        dates[2].add(Calendar.DATE, -31);

        interactions = new Interaction[]{
                new Interaction( 1, 1, dates[0].getTimeInMillis(), "A + B"),
                new Interaction( 2, 1, dates[1].getTimeInMillis(), "B + C"),
                new Interaction( 3, 1, dates[2].getTimeInMillis(), "C + A")
        };

        binds = new BindFriendInteraction[]{
                new BindFriendInteraction(1,1),
                new BindFriendInteraction(2,1),
                new BindFriendInteraction(2,2),
                new BindFriendInteraction(3,2),
                new BindFriendInteraction(3,3),
                new BindFriendInteraction(1,3)
        };

        lastInteractions = new LastInteraction[]{
                new LastInteraction(1, 1, 1, dates[0].getTimeInMillis(), 0),
                new LastInteraction(2, 1, 1, dates[0].getTimeInMillis(), 0),
                new LastInteraction(3, 1, 2, dates[1].getTimeInMillis(), 0)
        };

        for(InteractionType type : types) { dao.add(type); }

        for(Friend friend : friends) { dao.add(friend); }

        for(Interaction interaction : interactions) { dao.add(interaction); }

        for(BindFriendInteraction bind : binds) { dao.add(bind); }

        for(LastInteraction lastInteraction : lastInteractions) {dao.add(lastInteraction);}

    }
}
