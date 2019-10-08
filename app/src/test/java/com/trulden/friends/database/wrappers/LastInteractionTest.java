package com.trulden.friends.database.wrappers;

import com.trulden.friends.database.entity.InteractionType;
import com.trulden.friends.database.entity.LastInteraction;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Calendar;

import static org.junit.Assert.assertFalse;

public class LastInteractionTest {

    @Test
    public void itsTimeFalse() {

        Calendar weekAgo = Calendar.getInstance();
        weekAgo.add(Calendar.DATE, -7);

        LastInteraction lastInteraction = new LastInteraction(0, 0, 0, weekAgo.getTimeInMillis(), 0);
        LastInteractionWrapper lastInteractionWrapper = new LastInteractionWrapper();
        lastInteractionWrapper.setLastInteraction(lastInteraction);

        InteractionType type = new InteractionType("8 days", 8);
        ArrayList<InteractionType> typeArrayList = new ArrayList<>();
        typeArrayList.add(type);

        lastInteractionWrapper.setTypes(typeArrayList);

        assertFalse(lastInteractionWrapper.itsTime());
    }
}