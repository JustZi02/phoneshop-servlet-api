package com.es.phoneshop.model.cart;

import com.es.phoneshop.model.exceptions.NotEnoughProductStockException;
import com.es.phoneshop.model.product.ArrayListProductDao;
import com.es.phoneshop.model.product.Product;
import com.es.phoneshop.model.product.ProductDao;

public class DefaultCartService implements CartService {
    private Cart cart = new Cart();
    private ProductDao productDao;

    private static DefaultCartService instance;

    public static DefaultCartService getInstance() {
        if (instance == null) {
            instance = new DefaultCartService();
        }
        return instance;
    }

    private DefaultCartService() {
        productDao = ArrayListProductDao.getInstance();
    }

    @Override
    public Cart getCart() {
        return cart;
    }

    @Override
    public void add(long productId, int quantity) throws NotEnoughProductStockException {
        Product product = productDao.getProduct(productId);
        if (product.getStock() >= quantity) {
            cart.getItems().stream()
                    .filter(item -> item.getProduct().getId() == productId)
                    .findFirst()
                    .ifPresentOrElse(item -> item.setQuantity(item.getQuantity() + quantity),
                            () -> cart.getItems().add(new CartItem(product, quantity)));
            product.setStock(product.getStock() - quantity);
        } else {
            throw new NotEnoughProductStockException();
        }
    }
}
