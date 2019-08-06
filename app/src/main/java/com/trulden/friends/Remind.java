package com.trulden.friends;

public class Remind {
    private final String name;
    private final int daysAgo;

    Remind(String name, int daysAgo){
        this.name = name;
        this.daysAgo = daysAgo;
    }

    public String getName() {
        return name;
    }

    public int getDaysAgo() {
        return daysAgo;
    }
}
