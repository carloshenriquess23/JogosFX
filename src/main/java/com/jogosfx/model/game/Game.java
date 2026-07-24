package com.jogosfx.model.game;

public class Game {

    private int gameId;
    private String name;
    private Enum gender;
    private Enum platform;
    private double price;
    private int stock;
    private String description;
    private String developer;
    private double rating;
    private String poster;
    private boolean active;

    public double getPrice(){

        return this.price;
    }
}
