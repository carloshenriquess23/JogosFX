package com.jogosfx.model.purchase;

public class OrderItem {

    private int orderItemId;
    private int orderId;
    private int gameId;
    private double unitPrice;

    public void orderItem(int orderItemId, int orderId, int gameId, int amount, double gamePrice){

        this.orderItemId = orderItemId;
        this.orderId = orderId;
        this.gameId = gameId;
        this.unitPrice = gamePrice;
    }

     double getUnitPrice() {
        return this.unitPrice;
    }
}
