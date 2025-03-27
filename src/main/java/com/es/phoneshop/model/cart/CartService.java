package com.es.phoneshop.model.cart;

import jakarta.servlet.http.HttpServletRequest;

public interface CartService {
    Cart getCart(HttpServletRequest request);

    public void add(Cart cart, long productId, int quantity);

    public void update(Cart cart, long productId, int quantity);

    public void delete(Cart cart, long productId);
}
