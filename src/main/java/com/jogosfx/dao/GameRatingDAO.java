package com.jogosfx.dao;

import com.jogosfx.model.game.GameRating;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

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
}
