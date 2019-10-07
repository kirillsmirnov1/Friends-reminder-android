package com.trulden.friends.util;

import com.trulden.friends.database.wrappers.LastInteraction;

import org.junit.Test;

import java.util.Calendar;

import static com.trulden.friends.util.Util.daysPassed;
import static org.junit.Assert.assertEquals;

public class UtilTest {

    @Test
    public void testTodayDayCounter(){
        LastInteraction lastInteraction = new LastInteraction(0, Calendar.getInstance().getTimeInMillis(), null);

        assertEquals(daysPassed(lastInteraction), 0);
    }

    @Test
    public void testYesterdayCounter(){
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, -1);
        LastInteraction lastInteraction = new LastInteraction(0, calendar.getTimeInMillis(), null);

        assertEquals(daysPassed(lastInteraction), 1);
    }
}
