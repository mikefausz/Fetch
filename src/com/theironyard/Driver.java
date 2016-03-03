package com.theironyard;

/**
 * Created by PiratePowWow on 3/3/16.
 */
public class Driver {
    int id;
    String name;

    public Driver(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
