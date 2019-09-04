package com.trulden.friends.database;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "friend_table")
public class Friend {

    @PrimaryKey(autoGenerate = true)
    private int id;

    @NonNull
    private String name;

    private String info;

    public Friend(@NonNull String name, String info){
        this.name = name;
        this.info = info;
    }

    @Ignore
    public Friend(int id, @NonNull String name, String info){
        this.id = id;
        this.name = name;
        this.info = info;
    }

    // -----------------------------------------
    // Getters and setters
    // -----------------------------------------

    public int getId() {
        return id;
    }

    public void setId(int id) {
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
