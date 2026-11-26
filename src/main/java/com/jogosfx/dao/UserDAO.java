package com.jogosfx.dao;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import com.jogosfx.model.user.Client;
import com.jogosfx.model.user.User;
import com.jogosfx.util.PasswordUtils;

public class UserDAO {

    public static int insertUser(User user,String password) throws SQLException {

        if (password == null) throw new IllegalArgumentException("Password must not be null");

        String command="INSERT INTO TB_USER(EMAIL,PASSWORD,NAME)\n" +
                "\tVALUES(?,?,?);";

        try(Connection testConnection=ConnectionDB.generateConnection();
        PreparedStatement commandPreparation=testConnection.prepareStatement(command);) {

            commandPreparation.setString(1, user.getEmail());
            commandPreparation.setString(2, PasswordUtils.hash(password));
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

                Client userFound=new Client(executeCommand.getInt("ID_USER"), executeCommand.getString("EMAIL"), executeCommand.getString("NAME"), executeCommand.getDouble("BALANCE"));
                usersFound.add(userFound);
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

                Client userFound=new Client(executeCommand.getInt("ID_USER"), executeCommand.getString("EMAIL"), executeCommand.getString("NAME"), executeCommand.getDouble("BALANCE"));
                usersFound.add(userFound);
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

                Client userFound=new Client(executeCommand.getInt("ID_USER"), executeCommand.getString("EMAIL"), executeCommand.getString("NAME"), executeCommand.getDouble("BALANCE"));
                usersFound.add(userFound);
            }

            testConnection.close();
            return usersFound;
        }
    }

    public static int updateUser(User user, String password) throws SQLException {

        if (password == null) throw new IllegalArgumentException("Password must not be null");

        String hashedPassword = PasswordUtils.hash(password);

        String command="UPDATE TB_USER\n" +
                "\tSET EMAIL=?,PASSWORD=?,NAME=?,BALANCE=?\n" +
                "\tWHERE ID_USER=?;";

        try(Connection testConnection=ConnectionDB.generateConnection();
        PreparedStatement commandPreparation=testConnection.prepareStatement(command);) {

            commandPreparation.setString(1, user.getEmail());
            commandPreparation.setString(2, hashedPassword);
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
