package com.jogosfx.model.game;

import java.util.List;

public class Game {

    private int gameId;
    private int platformId;
    private String name;
    private double price;
    private String description;
    private String developer;
    private double rating;
    private String poster;
    private boolean active;
    List<Gender> genders;
    List<Platform> platforms;

    public double getPrice(){

        return this.price;
    }
}
