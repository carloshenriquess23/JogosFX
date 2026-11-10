package com.jogosfx.dao;

import com.jogosfx.model.game.GameRating;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class GameRatingDAO {

    public static int insertGameRating(GameRating gameRating) throws SQLException, ClassNotFoundException {

        String command="INSERT INTO TB_GAME_RATING(GAME_ID_GAME,USER_ID_USER,RATING,DESCRIPTION)\n" +
                "\t\tVALUES(?,?,?,?);";

        try(Connection getConnection=ConnectionDB.generateConnection(); PreparedStatement prepareCommand=getConnection.prepareStatement(command);){

            prepareCommand.setInt(1,gameRating.getGameID());
            prepareCommand.setInt(2,gameRating.getUserID());
            prepareCommand.setInt(3,gameRating.getRating());
            prepareCommand.setString(4,gameRating.getDescription());

            return prepareCommand.executeUpdate();
        }
    }

    public static ArrayList<GameRating> findByGameId(int gameId) throws SQLException, ClassNotFoundException {

        String command="SELECT ID_GAME_RATING,GAME_ID_GAME AS ID_GAME, USER_ID_USER AS ID_USER, RATING, DESCRIPTION FROM TB_GAME_RATING\n" +
                "\t\tWHERE GAME_ID_GAME=?;";

        try(Connection getConnection=ConnectionDB.generateConnection();PreparedStatement prepareCommand=getConnection.prepareStatement(command);){

            prepareCommand.setInt(1,gameId);
            ResultSet executeCommand=prepareCommand.executeQuery();

            ArrayList<GameRating> gameRatingsFound=new ArrayList<>();
            while(executeCommand.next()){

                GameRating gameRatingFound=new GameRating(executeCommand.getInt("ID_GAME_RATING"),executeCommand.getInt("ID_GAME"),executeCommand.getInt("ID_USER"),executeCommand.getInt("RATING"),executeCommand.getString("DESCRIPTION"));
                gameRatingsFound.add(gameRatingFound);
            }

            return gameRatingsFound;
        }
    }

    public static ArrayList<GameRating> findByUserId(int userId) throws SQLException, ClassNotFoundException {

        String command="SELECT ID_GAME_RATING,GAME_ID_GAME AS ID_GAME, USER_ID_USER AS ID_USER, RATING, DESCRIPTION FROM TB_GAME_RATING\n" +
                "\t\tWHERE GAME_ID_GAME=?;";

        try(Connection getConnection=ConnectionDB.generateConnection();PreparedStatement prepareCommand=getConnection.prepareStatement(command);){

            prepareCommand.setInt(1,userId);
            ResultSet executeCommand=prepareCommand.executeQuery();

            ArrayList<GameRating> gameRatingsFound=new ArrayList<>();
            while(executeCommand.next()){

                GameRating gameRatingFound=new GameRating(executeCommand.getInt("ID_GAME_RATING"),executeCommand.getInt("ID_GAME"),executeCommand.getInt("ID_USER"),executeCommand.getInt("RATING"),executeCommand.getString("DESCRIPTION"));
                gameRatingsFound.add(gameRatingFound);
            }

            return gameRatingsFound;
        }
    }
}
