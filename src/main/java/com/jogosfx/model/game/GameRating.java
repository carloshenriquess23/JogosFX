package com.jogosfx.model.game;

public class GameRating {

    int gameRatingID;
    int gameID;
    int userID;
    int rating;
    String description;

    public GameRating(int gameRatingID, int gameID, int userID, int rating, String description) {
        this.gameRatingID = gameRatingID;
        this.gameID = gameID;
        this.userID = userID;
        this.rating = rating;
        this.description = description;
    }

    public GameRating(int rating, int gameID, int userID, String description) {
        this.rating = rating;
        this.gameID = gameID;
        this.userID = userID;
        this.description = description;
    }

    public int getGameRatingID() {
        return gameRatingID;
    }

    public int getGameID() {
        return gameID;
    }

    public int getUserID() {
        return userID;
    }

    public int getRating() {
        return rating;
    }

    public String getDescription() {
        return description;
    }
}
