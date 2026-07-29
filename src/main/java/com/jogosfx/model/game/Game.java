package com.jogosfx.model.game;

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

    public double getPrice(){

        return this.price;
    }
}
