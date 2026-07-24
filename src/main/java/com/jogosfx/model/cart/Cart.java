package com.jogosfx.model.cart;

import java.util.List;

public class Cart {

    private int cartId;
    private int userId;
    private List<CartItem> cartItems;

    void addItem(CartItem item) {

        cartItems.add(item);
    }

    void removeItem(CartItem item) {

        cartItems.remove(item);
    }

    double calculateTotal(double gamePrice) {

        double total=0;

        for (CartItem item : cartItems) {

            total+=item.getAmount()*gamePrice;
        }

        return total;
    }

    void clearCart() {

        cartItems.clear();
    }
}
