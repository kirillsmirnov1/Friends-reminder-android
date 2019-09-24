package com.trulden.friends.database.entity;

import androidx.annotation.NonNull;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

/**
 * Friend class. Stores person information — id in database, name, notes
 */
@androidx.room.Entity(tableName = "friend_table")
public class Friend implements Entity {

    @PrimaryKey(autoGenerate = true)
    private long id;

    @NonNull
    private String name;

    private String info;

    /**
     * Constructor for creating new friend
     * @param name friend's name
     * @param info notes about friend
     */
    public Friend(@NonNull String name, String info){
        this.name = name;
        this.info = info;
    }

    /**
     * Constructor for updating existing Friend
     * @param id database identification of Friend to update
     * @param name friend's name
     * @param info notes about friend
     */
    @Ignore
    public Friend(long id, @NonNull String name, String info){
        this.id = id;
        this.name = name;
        this.info = info;
    }

    // -----------------------------------------
    // Getters and setters
    // -----------------------------------------

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @NonNull
    public String getName() {
        return name;
    }

    public void setName(@NonNull String name) {
        this.name = name;
    }

    public String getInfo() {
        return info;
    }
}
