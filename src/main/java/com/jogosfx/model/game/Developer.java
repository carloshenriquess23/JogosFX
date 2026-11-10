package com.jogosfx.model.game;

public class Developer {

    int developerID;
    String name;

    public Developer(int developerID, String name) {
        this.developerID = developerID;
        this.name = name;
    }

    public Developer(String name){

        this.name=name;
    }

    public int getDeveloperID() {
        return developerID;
    }

    public String getName() {
        return name;
    }
}
