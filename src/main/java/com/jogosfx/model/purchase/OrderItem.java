package com.jogosfx.model.purchase;

public class OrderItem {

    private int orderItemId;
    private int orderId;
    private int gameId;
    private int amount;
    private double unitPrice;

    public void orderItem(int orderItemId, int orderId, int gameId, int amount, double gamePrice){

        this.orderItemId = orderItemId;
        this.orderId = orderId;
        this.gameId = gameId;
        this.amount = amount;
        this.unitPrice = this.amount * gamePrice;
    }

     double getUnitPrice() {
        return this.unitPrice;
    }
}
