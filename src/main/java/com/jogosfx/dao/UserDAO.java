package com.jogosfx.dao;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import com.jogosfx.model.user.Client;
import com.jogosfx.model.user.User;

public class UserDAO {

    public static int insertUser(User user,String password) throws SQLException {

        String command="INSERT INTO TB_USER(EMAIL,PASSWORD,NAME)\n" +
                "\tVALUES(?,?,?);";

        try(Connection testConnection=ConnectionDB.generateConnection();
        PreparedStatement commandPreparation=testConnection.prepareStatement(command);) {

            commandPreparation.setString(1, user.getEmail());
            commandPreparation.setString(2, password);
            commandPreparation.setString(3, user.getName());

            int executeCommand = commandPreparation.executeUpdate();

            testConnection.close();
            return executeCommand;
        }
    }

    public static ArrayList findById(int userID) throws SQLException {

        String command="SELECT ID_USER, EMAIL,NAME,BALANCE FROM TB_USER\n" +
                "\tWHERE ID_USER=?;";

        try(Connection testConnection=ConnectionDB.generateConnection();
        PreparedStatement commandPreparation=testConnection.prepareStatement(command);) {

            commandPreparation.setInt(1, userID);

            ResultSet executeCommand = commandPreparation.executeQuery();
            ArrayList<Client> usersFound = new ArrayList<>();

            while (executeCommand.next()) {

                usersFound.add(new Client(executeCommand.getInt(1), executeCommand.getString(2), executeCommand.getString(3), executeCommand.getDouble(4)));
            }

            testConnection.close();
            return usersFound;
        }
    }

    public static ArrayList findByEmail(String userEmail) throws SQLException {

        String command="SELECT ID_USER, EMAIL,NAME,BALANCE FROM TB_USER\n" +
                "\tWHERE EMAIL\n" +
                "\t\tLIKE ?;";

        try(Connection testConnection=ConnectionDB.generateConnection();
        PreparedStatement commandPreparation=testConnection.prepareStatement(command);) {

            commandPreparation.setString(1, "%" + userEmail + "%");

            ResultSet executeCommand = commandPreparation.executeQuery();
            ArrayList<Client> usersFound = new ArrayList<>();

            while (executeCommand.next()) {

                usersFound.add(new Client(executeCommand.getInt(1), executeCommand.getString(2), executeCommand.getString(3), executeCommand.getDouble(4)));
            }

            testConnection.close();
            return usersFound;
        }
    }

    public static ArrayList findByName(String userName) throws SQLException {

        String command="SELECT ID_USER, EMAIL,NAME,BALANCE FROM TB_USER\n" +
                "\tWHERE NAME\n" +
                "\t\tLIKE ?;";

        try(Connection testConnection=ConnectionDB.generateConnection();
        PreparedStatement commandPreparation=testConnection.prepareStatement(command);) {

            commandPreparation.setString(1, "%" + userName + "%");

            ResultSet executeCommand = commandPreparation.executeQuery();
            ArrayList<Client> usersFound = new ArrayList<>();

            while (executeCommand.next()) {

                usersFound.add(new Client(executeCommand.getInt(1), executeCommand.getString(2), executeCommand.getString(3), executeCommand.getDouble(4)));
            }

            testConnection.close();
            return usersFound;
        }
    }

    public static int updateUser(User user, String password) throws SQLException {

        String command="UPDATE TB_USER\n" +
                "\tSET EMAIL=?,PASSWORD=?,NAME=?,BALANCE=?\n" +
                "\tWHERE ID_USER=?;";

        try(Connection testConnection=ConnectionDB.generateConnection();
        PreparedStatement commandPreparation=testConnection.prepareStatement(command);) {

            commandPreparation.setString(1, user.getEmail());
            commandPreparation.setString(2, password);
            commandPreparation.setString(3, user.getName());
            commandPreparation.setDouble(4, user.getBalance());
            commandPreparation.setInt(5, user.getUserId());

            int executeCommand = commandPreparation.executeUpdate();

            testConnection.close();
            return executeCommand;
        }
    }

    public static int deleteUser(int IDUser) throws SQLException {

        String command="DELETE FROM TB_USER\n" +
                "\tWHERE ID_USER=?;";

        try(Connection testConnection=ConnectionDB.generateConnection();
        PreparedStatement commandPreparation=testConnection.prepareStatement(command);) {

            commandPreparation.setInt(1, IDUser);

            int executeCommand = commandPreparation.executeUpdate();

            testConnection.close();
            return executeCommand;
        }
    }
}
