package com.jogosfx.model.cart;

public class CartItem {

    private int cartItemId;
    private int cartId;
    private int gameId;
    private int amount;

    int getAmount() {
        return this.amount;
    }

    public void setAmount(int amount) {}
    void updateAmount(int amount) {

        if(amount>0)
            this.amount+=amount;

        else
            System.out.println("Invalid amount");
    }
}
