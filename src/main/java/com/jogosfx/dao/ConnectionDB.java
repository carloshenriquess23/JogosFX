package com.jogosfx.dao;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class ConnectionDB {

    private ConnectionDB(){}

    public static Connection generateConnection() throws SQLException{

        final String URL = "jdbc:mysql://localhost:3306/jogosfx";
        final String USER = "root";
        final String PASSWORD = "1912";

        Connection connectionDB = DriverManager.getConnection(URL,USER,PASSWORD);

        System.out.println("Conectado com sucesso!");
        return connectionDB;
    }
}
