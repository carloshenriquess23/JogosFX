package com.jogosfx.dao;

import com.jogosfx.model.purchase.Order;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class OrderDAO {

    public static int insertOrder(Order order, int paymentMethodId) throws SQLException {

        String command="INSERT INTO TB_ORDER(USER_ID_USER,ORDER_DATE,STATUS,TOTAL,ID_PAYMENT_METHOD,GAME_ID_GAME)\n" +
                "\tVALUES(?,?,?,?,?,?);";

        try(Connection getConnection=ConnectionDB.generateConnection();
            PreparedStatement prepareCommand=getConnection.prepareStatement(command);){

            prepareCommand.setInt(1,order.getUserId());
            prepareCommand.setString(2,order.getOrderDate());
            prepareCommand.setBoolean(3,order.getStatus());
            prepareCommand.setDouble(4,order.getTotal());
            prepareCommand.setInt(5,paymentMethodId);
            prepareCommand.setInt(6,order.getGameId());

            return  prepareCommand.executeUpdate();
        }
    }

    public static ArrayList<Order> findById(int orderID) throws SQLException {

        String command="SELECT ID_ORDER, USER_ID_USER AS ORDER_USER, ORDER_DATE, STATUS, TOTAL, TB_PAYMENT_METHOD.NAME AS PAYMENT_METHOD, GAME_ID_GAME AS ORDER_GAME\n" +
                "\tFROM TB_ORDER\n" +
                "    INNER JOIN TB_PAYMENT_METHOD\n" +
                "\t\tON TB_ORDER.ID_PAYMENT_METHOD=TB_PAYMENT_METHOD.ID_PAYMENT_METHOD\n" +
                "    WHERE ID_ORDER=?;";

        try(Connection getConnection=ConnectionDB.generateConnection();
            PreparedStatement commandPreparation=getConnection.prepareStatement(command);) {

            commandPreparation.setInt(1, orderID);

            ResultSet executeCommand = commandPreparation.executeQuery();
            ArrayList<Order> ordersFound = new ArrayList<>();

            while (executeCommand.next()) {

                Order orderFound=new Order(executeCommand.getInt("ID_ORDER"), executeCommand.getInt("ORDER_USER"), executeCommand.getObject("ORDER_DATE", LocalDateTime.class), executeCommand.getBoolean("STATUS"),executeCommand.getDouble("TOTAL"),executeCommand.getString("PAYMENT_METHOD"),executeCommand.getInt("ORDER_GAME"));
                ordersFound.add(orderFound);
            }

            return ordersFound;
        }
    }

    public static ArrayList<Order> findByUserId(int userID) throws SQLException {

        String command="SELECT ID_ORDER, USER_ID_USER AS ORDER_USER, ORDER_DATE, STATUS, TOTAL, ID_PAYMENT_METHOD, GAME_ID_GAME AS ORDER_GAME\n" +
                "\tFROM TB_ORDER\n" +
                "    WHERE USER_ID_USER=?;";

        try(Connection getConnection=ConnectionDB.generateConnection();
            PreparedStatement commandPreparation=getConnection.prepareStatement(command);) {

            commandPreparation.setInt(1, userID);

            ResultSet executeCommand = commandPreparation.executeQuery();
            ArrayList<Order> ordersFound = new ArrayList<>();

            while (executeCommand.next()) {

                Order orderFound=new Order(executeCommand.getInt("ID_ORDER"), executeCommand.getInt("ORDER_USER"), executeCommand.getObject("ORDER_DATE", LocalDateTime.class), executeCommand.getBoolean("STATUS"),executeCommand.getDouble("TOTAL"),executeCommand.getString("PAYMENT_METHOD"),executeCommand.getInt("ORDER_GAME"));
                ordersFound.add(orderFound);
            }

            return ordersFound;
        }
    }

    public static int updateStatusOrder(int status, int orderID)  throws SQLException {

        String command="UPDATE TB_ORDER\n" +
                "\tSET STATUS=?\n" +
                "    WHERE ID_ORDER=?;";

        try(Connection getConnection=ConnectionDB.generateConnection();
        PreparedStatement commandPreparation=getConnection.prepareStatement(command);) {

            commandPreparation.setInt(1, status);
            commandPreparation.setInt(2, orderID);

            return commandPreparation.executeUpdate();
        }
    }

    public static int deleteOrder(int orderID) throws SQLException {

        String command="DELETE FROM TB_ORDER WHERE ID_ORDER=?;";

        try(Connection getConnection=ConnectionDB.generateConnection();
            PreparedStatement commandPreparation=getConnection.prepareStatement(command);){

            commandPreparation.setInt(1, orderID);

            return   commandPreparation.executeUpdate();
        }
    }
}
