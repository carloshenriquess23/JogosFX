package com.jogosfx.dao;

import com.jogosfx.model.user.Adm;
import com.jogosfx.model.user.Client;
import com.jogosfx.util.PasswordUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Optional;

public class AdmDAO {

    public static int insertAdmin(Adm adm) throws SQLException {

        if (adm.getCredential() == null) {
            throw new IllegalArgumentException("Credential must not be null");
        }

        String hashedCredential = PasswordUtils.hash(adm.getCredential());

        String command="INSERT INTO TB_ADMIN(CREDENTIAL,USER_ID_USER)\n" +
                "\t\tVALUES(?,?);";


        try(Connection generateConnection=ConnectionDB.generateConnection();
        PreparedStatement commandPreparation = generateConnection.prepareStatement(command);) {

            commandPreparation.setString(1, hashedCredential);
            commandPreparation.setInt(2, adm.getUserId());

            int executeCommand = commandPreparation.executeUpdate();

            generateConnection.close();
            return executeCommand;
        }
    }

    public static ArrayList findByCredential(String credential) throws SQLException {

        String command="SELECT ID_USER, EMAIL, NAME, CREDENTIAL FROM TB_ADMIN\n" +
                "\t\tINNER JOIN TB_USER\n" +
                "\t\t\t\tON USER_ID_USER=ID_USER;";


        try(Connection generateConnection=ConnectionDB.generateConnection();
        PreparedStatement commandPreparation = generateConnection.prepareStatement(command);) {

            ResultSet executeCommand = commandPreparation.executeQuery();
            ArrayList<Adm> usersFound = new ArrayList<>();

            while (executeCommand.next()) {

                Adm userFound=new Adm(executeCommand.getInt("ID_USER"), executeCommand.getString("EMAIL"), executeCommand.getString("NAME"), executeCommand.getString("CREDENTIAL"));
                if (PasswordUtils.verify(credential, userFound.getCredential())) {
                    usersFound.add(userFound);
                }
            }

            generateConnection.close();
            return usersFound;
        }
    }

    public static Optional<Adm> findByUserId(int userId) throws SQLException {

        String command = "SELECT ID_USER, EMAIL, NAME, CREDENTIAL FROM TB_ADMIN " +
                "INNER JOIN TB_USER ON USER_ID_USER=ID_USER WHERE USER_ID_USER=?";

        try (Connection generateConnection = ConnectionDB.generateConnection();
             PreparedStatement commandPreparation = generateConnection.prepareStatement(command)) {

            commandPreparation.setInt(1, userId);

            ResultSet executeCommand = commandPreparation.executeQuery();

            if (executeCommand.next()) {
                Adm adm = new Adm(
                        executeCommand.getInt("ID_USER"),
                        executeCommand.getString("EMAIL"),
                        executeCommand.getString("NAME"),
                        executeCommand.getString("CREDENTIAL")
                );
                return Optional.of(adm);
            }

            return Optional.empty();
        }
    }

    public static boolean verifyCredential(int userId, String plainCredential) throws SQLException {
        Optional<Adm> adm = findByUserId(userId);
        if (adm.isEmpty()) {
            return false;
        }
        return PasswordUtils.verify(plainCredential, adm.get().getCredential());
    }
}
