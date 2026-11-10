package com.jogosfx.model.game;

public class Platform {

    int platformId;
    String name;

    public Platform(int platformId, String name) {
        this.platformId = platformId;
        this.name = name;
    }

    public Platform(String name) {
        this.name = name;
    }

    public int getPlatformId() {
        return platformId;
    }

    public String getName() {
        return name;
    }
}
