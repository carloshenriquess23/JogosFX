package com.jogosfx.model.purchase;

import java.time.LocalDateTime;
import java.util.List;

import static java.lang.String.format;

public class Order {

    private int orderId;
    private int userId;
    private LocalDateTime orderDate;
    private boolean status;
    private double total;
    private String paymentMethod;
    private int gameId;


    public Order(int orderId, int userId, LocalDateTime orderDate, boolean status, double total, String paymentMethod, int gameId) {
        this.orderId = orderId;
        this.userId = userId;
        this.orderDate = orderDate;
        this.status = status;
        this.total = total;
        this.paymentMethod = paymentMethod;
        this.gameId = gameId;
    }

    public int getOrderId() {
        return orderId;
    }

    public int getUserId() {
        return this.userId;
    }
    public String getOrderDate() {
        return orderDate.toString();
    }

    public boolean getStatus() {
        return status;
    }

    public double getTotal() {
        return total;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public int getGameId() {
        return gameId;
    }
}
