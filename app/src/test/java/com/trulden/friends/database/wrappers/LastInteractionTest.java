package com.trulden.friends.database.wrappers;

import com.trulden.friends.database.entity.InteractionType;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Calendar;

import static org.junit.Assert.assertFalse;

public class LastInteractionTest {

    @Test
    public void itsTimeFalse() {

        Calendar weekAgo = Calendar.getInstance();
        weekAgo.add(Calendar.DATE, -7);

        LastInteraction lastInteraction = new LastInteraction(0, weekAgo.getTimeInMillis(), null);
        InteractionType type = new InteractionType("8 days", 8);
        ArrayList<InteractionType> typeArrayList = new ArrayList<>();
        typeArrayList.add(type);

        lastInteraction.setInteractionTypes(typeArrayList);

        assertFalse(lastInteraction.itsTime());
    }
}