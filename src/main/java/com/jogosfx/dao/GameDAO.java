package com.jogosfx.dao;
import com.jogosfx.model.game.Developer;
import com.jogosfx.model.game.Game;
import com.jogosfx.model.game.Gender;
import com.jogosfx.model.game.Platform;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;


public class GameDAO {

    private static final String commandQueryBase="SELECT ID_GAME,TB_GAME.NAME,PRICE,TB_GAME.DESCRIPTION,TB_DEVELOPER.NAME AS DEVELOPER_NAME,RATING,POSTER,ACTIVE,TB_GENDER.NAME AS GENDER_NAME,TB_PLATFORM.NAME AS PLATFORM_NAME FROM TB_GAME\n" +
            "\t\tINNER JOIN TB_DEVELOPER\n" +
            "\t\t\tON TB_GAME.DEVELOPER_ID_DEVELOPER=TB_DEVELOPER.ID_DEVELOPER\n" +
            "\t\tINNER JOIN TB_GENDER\n" +
            "\t\t\tON GENDER_ID_GENDER=ID_GENDER\n" +
            "\t\tINNER JOIN TB_PLATFORM\n" +
            "\t\t\tON PLATFORM_ID_PLATFORM=ID_PLATFORM ";

    public static int insertGame(Game game) throws SQLException, ClassNotFoundException {

        String command="INSERT INTO TB_GAME(NAME,PRICE,DESCRIPTION,DEVELOPER_ID_DEVELOPER,POSTER,ACTIVE, GENDER_ID_GENDER,PLATFORM_ID_PLATFORM)\n" +
                "\t\tVALUES(?,?,?,?,?);";

        try(Connection getConnection=ConnectionDB.generateConnection();
            PreparedStatement preparedCommand=getConnection.prepareStatement(command)){

            preparedCommand.setString(1, game.getName());
            preparedCommand.setDouble(2, game.getPrice());
            preparedCommand.setString(3, game.getDescription());
            preparedCommand.setInt(4, game.getDeveloper().getDeveloperID());
            preparedCommand.setString(5, game.getPoster());
            preparedCommand.setBoolean(6, game.isActive());
            preparedCommand.setInt(7, game.getGender().getGenderId());
            preparedCommand.setInt(8, game.getPlatform().getPlatformId());

            return preparedCommand.executeUpdate();
        }
    }

    public static ArrayList<Game> FindGameById(int idGame) throws SQLException, ClassNotFoundException {

        String command=commandQueryBase+"WHERE ID_GAME=?;";

        try(Connection getConnection=ConnectionDB.generateConnection();
        PreparedStatement preparedCommand=getConnection.prepareStatement(command);) {

            preparedCommand.setInt(1, idGame);
            ResultSet executeCommand = preparedCommand.executeQuery();

            ArrayList<Game> gamesFounded = new ArrayList<>();
            while (executeCommand.next()) {

                Developer developerGameFounded = new Developer(executeCommand.getString("DEVELOPER_NAME"));
                Gender genderGameFounded = new Gender(executeCommand.getString("GENDER_NAME"));
                Platform platformGameFounded = new Platform(executeCommand.getString("PLATFORM_NAME"));
                Game gameFounded = new Game(
                        executeCommand.getInt("ID_GAME"),
                        executeCommand.getString("NAME"),
                        executeCommand.getDouble("PRICE"),
                        executeCommand.getString("DESCRIPTION"),
                        executeCommand.getDouble("RATING"),
                        executeCommand.getBoolean("ACTIVE"),
                        executeCommand.getString("POSTER"),
                        developerGameFounded,
                        genderGameFounded,
                        platformGameFounded
                );
                gamesFounded.add(gameFounded);
            }

            return gamesFounded;
        }
    }

    public static ArrayList<Game> findGameByName(String gameName,boolean gameActive) throws SQLException, ClassNotFoundException {

        String command=commandQueryBase+"WHERE TB_GAME.NAME \n" +
                "\t\t\tLIKE CONCAT('%',?,'%')\n" +
                "\t\tAND TB_GAME.ACTIVE=?;";

        try(Connection getConnection=ConnectionDB.generateConnection();
        PreparedStatement preparedCommand=getConnection.prepareStatement(command);) {

            preparedCommand.setString(1, gameName);
            preparedCommand.setBoolean(2, gameActive);
            ResultSet executeCommand = preparedCommand.executeQuery();

            ArrayList<Game> gamesFounded = new ArrayList<>();
            while (executeCommand.next()) {

                Developer developerGameFounded = new Developer(executeCommand.getString("DEVELOPER_NAME"));
                Gender genderGameFounded = new Gender(executeCommand.getString("GENDER_NAME"));
                Platform platformGameFounded = new Platform(executeCommand.getString("PLATFORM_NAME"));
                Game gameFounded = new Game(
                        executeCommand.getInt("ID_GAME"),
                        executeCommand.getString("NAME"),
                        executeCommand.getDouble("PRICE"),
                        executeCommand.getString("DESCRIPTION"),
                        executeCommand.getDouble("RATING"),
                        executeCommand.getBoolean("ACTIVE"),
                        executeCommand.getString("POSTER"),
                        developerGameFounded,
                        genderGameFounded,
                        platformGameFounded
                );
                gamesFounded.add(gameFounded);
            }

            return gamesFounded;
        }
    }

    public static int updateGame(Game game)  throws SQLException, ClassNotFoundException {

        String command = "UPDATE TB_GAME NAME,PRICE,STOCK,DESCRIPTION,DEVELOPER_ID_DEVELOPER,RATING,POSTER,ACTIVE,GENDER_ID_GENDER,PLATFORM_ID_PLATFORM\n" +
                "\t\t\tSET NAME=?,PRICE=?,DESCRIPTION=?,DEVELOPER_ID_DEVELOPER=?,RATING=?,POSTER=?,ACTIVE=?,GENDER_ID_GENDER=?,PLATFORM_ID_PLATFORM=?\n" +
                "            WHERE ID_GAME=?;";

        try(Connection getConnection=ConnectionDB.generateConnection();PreparedStatement prepareCommand=getConnection.prepareStatement(command)){

            prepareCommand.setString(1, game.getName());
            prepareCommand.setDouble(2, game.getPrice());
            prepareCommand.setString(3, game.getDescription());
            prepareCommand.setInt(4, game.getDeveloper().getDeveloperID());
            prepareCommand.setDouble(5,game.getRating());
            prepareCommand.setString(6, game.getPoster());
            prepareCommand.setBoolean(7, game.isActive());
            prepareCommand.setInt(8, game.getGender().getGenderId());
            prepareCommand.setInt(9, game.getPlatform().getPlatformId());
            prepareCommand.setInt(10,game.getGameId());

            return prepareCommand.executeUpdate();
        }
    }

    public static int deleteGame(int idGame)  throws SQLException, ClassNotFoundException {

        String command="DELETE FROM TB_GAME\n" +
                "\t\t\tWHERE ID_GAME=?;";

        try(Connection getConnection=ConnectionDB.generateConnection();PreparedStatement prepareCommand=getConnection.prepareStatement(command)){

            prepareCommand.setInt(1, idGame);

            return prepareCommand.executeUpdate();
        }
    }
}
