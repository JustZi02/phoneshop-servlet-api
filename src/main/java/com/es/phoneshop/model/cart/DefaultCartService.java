package com.es.phoneshop.model.cart;

import com.es.phoneshop.model.exceptions.OutOfStockException;
import com.es.phoneshop.model.product.ArrayListProductDao;
import com.es.phoneshop.model.product.Product;
import com.es.phoneshop.model.product.ProductDao;
import jakarta.servlet.http.HttpServletRequest;

import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class DefaultCartService implements CartService {
    private static final String CART_SESSION_ATTRIBUTE = DefaultCartService.class.getName() + ".cart";
    private ProductDao productDao;
    private final ReadWriteLock lock = new ReentrantReadWriteLock();

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
    public Cart getCart(HttpServletRequest request) {
        lock.readLock().lock();
        try {
            Cart cart = (Cart) request.getSession().getAttribute(CART_SESSION_ATTRIBUTE);
            if (cart == null) {
                request.getSession().setAttribute(CART_SESSION_ATTRIBUTE, cart = new Cart());
            }
            return cart;
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public void add(Cart cart, long productId, int quantity) throws OutOfStockException {
        lock.writeLock().lock();
        try {
            Product product = productDao.getProduct(productId);
            if (product.getStock() >= quantity) {
                cart.getItems().stream()
                        .filter(item -> item.getProduct().getId() == productId)
                        .findFirst()
                        .ifPresentOrElse(item -> item.setQuantity(item.getQuantity() + quantity),
                                () -> cart.getItems().add(new CartItem(product, quantity)));
                productDao.updateQuantity(productId, quantity);
            } else {
                throw new OutOfStockException("We don't have so many items of this product", quantity, product.getStock());
            }
        } finally {
            lock.writeLock().unlock();
        }
    }
}
