package com.jogosfx.model.game;

public class Gender {

    int genderId;
    String name;
    String description;

    public Gender(int genderId, String name, String description) {
        this.genderId = genderId;
        this.name = name;
        this.description = description;
    }

    public Gender(String name) {
        this.name = name;
    }

    public int getGenderId() {
        return genderId;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }
}
