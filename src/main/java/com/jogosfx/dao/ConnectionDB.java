package com.jogosfx.dao;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Connection {

    protected static generateConnection() throws SQLException {

        String url = "jdbc:mysql://localhost:3306/jogosfx";
        String user = "root";
        String password = "1912";

        return DriverManager.getConnection(url,user,password);
    }
}
