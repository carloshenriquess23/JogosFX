package com.jogosfx.model.game;

import java.util.List;

public class Game {

    private int gameId;
    private String name;
    private double price;
    private String description;
    Developer developer;
    private double rating;
    private String poster;
    private boolean active;
    Gender gender;
    Platform platform;

    public Game(int gameId, String name, double price, String description,
                double rating, boolean active, String poster,
                Developer developer, Gender gender, Platform platform) {

        this.gameId = gameId;
        this.name = name;
        this.price = price;
        this.description = description;
        this.developer = developer;
        this.rating = rating;
        this.active = active;
        this.poster = poster;
        this.gender = gender;
        this.platform = platform;
    }

    public Game(String name, double price, String description, Developer developer,
                boolean active, String poster, Gender gender, Platform platform) {
        this.name = name;
        this.price = price;
        this.description = description;
        this.developer = developer;
        this.active = active;
        this.poster = poster;
        this.gender = gender;
        this.platform = platform;
    }

    public int getGameId() { return gameId; }

    public String getName() {
        return name;
    }

    public double getPrice(){

        return price;
    }

    public String getDescription(){
        return description;
    }

    public Developer getDeveloper(){

        return developer;
    }

    public double getRating(){

        return rating;
    }

    public String getPoster(){

        return poster;
    }

    public boolean isActive(){
        return active;
    }

    public Gender getGender(){

        return gender;
    }

    public Platform getPlatform(){

        return platform;
    }
}
