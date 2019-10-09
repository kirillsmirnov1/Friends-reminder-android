package com.trulden.friends.util;

import com.trulden.friends.database.entity.LastInteraction;

import org.junit.Test;

import java.util.Calendar;

import static com.trulden.friends.util.Util.daysPassed;
import static org.junit.Assert.assertEquals;

public class UtilTest {

    @Test
    public void testTodayDayCounter(){
        LastInteraction lastInteraction = new LastInteraction(0, 0, 0, Calendar.getInstance().getTimeInMillis(), 0);

        assertEquals(daysPassed(lastInteraction), 0);
    }

    @Test
    public void testYesterdayCounter(){
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, -1);
        LastInteraction lastInteraction = new LastInteraction(0, 0, 0, calendar.getTimeInMillis(), 0);

        assertEquals(daysPassed(lastInteraction), 1);
    }
}