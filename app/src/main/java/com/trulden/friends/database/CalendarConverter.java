package com.trulden.friends.database;

import androidx.room.TypeConverter;

import java.util.Calendar;

// https://stackoverflow.com/questions/50313525/room-using-date-field

public class CalendarConverter {

    @TypeConverter
    public static Calendar toCalendar(Long l) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(l);
        return c;
    }

    @TypeConverter
    public static Long fromCalendar(Calendar c){
        return c == null ? null : c.getTime().getTime();
    }
}
