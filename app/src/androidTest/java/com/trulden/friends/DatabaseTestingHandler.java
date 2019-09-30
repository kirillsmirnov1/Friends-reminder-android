package com.trulden.friends;

import android.content.Context;

import androidx.room.Room;

import com.trulden.friends.database.FriendsDao;
import com.trulden.friends.database.FriendsDatabase;
import com.trulden.friends.database.entity.BindFriendInteraction;
import com.trulden.friends.database.entity.Friend;
import com.trulden.friends.database.entity.Interaction;
import com.trulden.friends.database.entity.InteractionType;

import java.util.Calendar;
import java.util.List;

public class DatabaseTestingHandler {
    public static void initAndFillDatabase(Context context){
        FriendsDatabase db = Room.inMemoryDatabaseBuilder(context, FriendsDatabase.class).build();
        FriendsDao dao = db.friendsDao();

        InteractionType[] types = {
                new InteractionType("Meeting", 30),
                new InteractionType("Texting", 7)
        };

        Friend[] friends = {
                new Friend("Aaron", "A prophet, high priest, and the brother of Moses"),
                new Friend("Balaam", "Told King Balak how to get the Israelites to commit sin by enticing them with sexual immorality and food sacrificed to idols"),
                new Friend("Caleb", "One of the twelve spies sent by Moses into Canaan")
        };

        Calendar dates[] = { Calendar.getInstance(), Calendar.getInstance(), Calendar.getInstance() };

        dates[0].add(Calendar.DATE, -29);
        dates[1].add(Calendar.DATE, -30);
        dates[2].add(Calendar.DATE, -31);

        Interaction[] interactions = {
                new Interaction( 1, dates[0].getTimeInMillis(), "A + B"),
                new Interaction( 1, dates[1].getTimeInMillis(), "B + C"),
                new Interaction( 1, dates[2].getTimeInMillis(), "C + A")
        };

        BindFriendInteraction[] binds = {
                new BindFriendInteraction(1,1),
                new BindFriendInteraction(2,1),
                new BindFriendInteraction(2,2),
                new BindFriendInteraction(3,2),
                new BindFriendInteraction(3,3),
                new BindFriendInteraction(1,3)
        };

        for(InteractionType type : types) { dao.add(type); }

        for(Friend friend : friends) { dao.add(friend); }

        List friendsList = dao.getAllFriendsAsList();

        for(Interaction interaction : interactions) { dao.add(interaction); }

        for(BindFriendInteraction bind : binds) { dao.add(bind); }

    }
}
