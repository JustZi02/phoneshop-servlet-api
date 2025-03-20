package com.es.phoneshop.model.cart;

public interface CartService {
    Cart getCart();

    public void add(long productId, int quantity);
}
