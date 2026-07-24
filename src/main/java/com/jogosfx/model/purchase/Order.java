package com.jogosfx.model.purchase;

import java.time.LocalDateTime;
import java.util.List;

public class Order {

    private int orderId;
    private int userId;
    private int cartId;
    private LocalDateTime orderDate;
    private boolean status;
    private List<OrderItem> orderItems;
    private double total;

    private void  calculateTotal(){

        for(OrderItem orderItem : orderItems){

            this.total+= orderItem.getUnitPrice();
        }
    }
}
