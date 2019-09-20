package com.trulden.friends.database.entity;

import androidx.annotation.NonNull;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@androidx.room.Entity(tableName = "friend_table")
public class Friend implements Entity {

    @PrimaryKey(autoGenerate = true)
    private long id;

    @NonNull
    private String name;

    private String info;

    public Friend(@NonNull String name, String info){
        this.name = name;
        this.info = info;
    }

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

    public void setInfo(String info) {
        this.info = info;
    }
}
