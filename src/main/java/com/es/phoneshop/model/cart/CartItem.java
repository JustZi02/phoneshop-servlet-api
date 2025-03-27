package com.es.phoneshop.model.cart;

import com.es.phoneshop.model.product.Product;

import java.io.Serial;
import java.io.Serializable;

public class CartItem implements Serializable {
    @Serial
    private static final long serialVersionUID = 123722387956287383L;

    Product product;
    int quantity;

    public CartItem(Product product, int quantity) {
        this.product = product;
        this.quantity = quantity;
    }

    public Product getProduct() {
        return product;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    @Override
    public String toString() {
        return "" + product.getDescription() + " (" + quantity + ")";
    }
}
