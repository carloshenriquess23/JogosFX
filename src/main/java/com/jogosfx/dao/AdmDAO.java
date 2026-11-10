package com.jogosfx.dao;

import com.jogosfx.model.user.Adm;
import com.jogosfx.model.user.Client;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class AdmDAO {

    public static int insertAdmin(Adm adm) throws SQLException {

        String command="INSERT INTO TB_ADMIN(CREDENTIAL,USER_ID_USER)\n" +
                "\t\tVALUES(?,?);";


        try(Connection generateConnection=ConnectionDB.generateConnection();
        PreparedStatement commandPreparation = generateConnection.prepareStatement(command);) {

            commandPreparation.setString(1, adm.getCredential());
            commandPreparation.setInt(2, adm.getUserId());

            int executeCommand = commandPreparation.executeUpdate();

            generateConnection.close();
            return executeCommand;
        }
    }

    public static ArrayList findByCredential(String credential) throws SQLException {

        String command="SELECT ID_USER, EMAIL, NAME, CREDENTIAL FROM TB_ADMIN\n" +
                "\t\tINNER JOIN TB_USER\n" +
                "\t\t\t\tON USER_ID_USER=ID_USER\n" +
                "\t\tWHERE CREDENTIAL=?;";


        try(Connection generateConnection=ConnectionDB.generateConnection();
        PreparedStatement commandPreparation = generateConnection.prepareStatement(command);) {

            commandPreparation.setString(1, credential);

            ResultSet executeCommand = commandPreparation.executeQuery();
            ArrayList<Adm> usersFound = new ArrayList<>();

            while (executeCommand.next()) {

                usersFound.add(new Adm(executeCommand.getInt(1), executeCommand.getString(2), executeCommand.getString(3), executeCommand.getString(4)));
            }

            generateConnection.close();
            return usersFound;
        }
    }
}
