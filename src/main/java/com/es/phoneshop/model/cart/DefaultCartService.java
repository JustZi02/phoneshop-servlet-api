package com.es.phoneshop.model.cart;

import com.es.phoneshop.model.exceptions.OutOfStockException;
import com.es.phoneshop.model.product.ArrayListProductDao;
import com.es.phoneshop.model.product.Product;
import com.es.phoneshop.model.product.ProductDao;
import jakarta.servlet.http.HttpServletRequest;

import java.math.BigDecimal;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

public class DefaultCartService implements CartService {
    private static final String CART_SESSION_ATTRIBUTE = DefaultCartService.class.getName() + ".cart";
    private static DefaultCartService instance;
    private final ReadWriteLock lock = new ReentrantReadWriteLock();
    private ProductDao productDao;

    private DefaultCartService() {
        productDao = ArrayListProductDao.getInstance();
    }

    public static DefaultCartService getInstance() {
        if (instance == null) {
            instance = new DefaultCartService();
        }
        return instance;
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
            int currentCartQuantity = cart.getItems().stream()
                    .filter(item -> item.getProduct().getId() == productId)
                    .mapToInt(CartItem::getQuantity)
                    .sum();

            if (product.getStock() >= currentCartQuantity + quantity) {
                cart.getItems().stream()
                        .filter(item -> item.getProduct().getId() == productId)
                        .findFirst()
                        .ifPresentOrElse(item -> item.setQuantity(item.getQuantity() + quantity),
                                () -> cart.getItems().add(new CartItem(product, quantity)));
                recalculateCart(cart);
            } else {
                throw new OutOfStockException("We don't have so many items of this product", quantity, product.getStock() - currentCartQuantity);
            }
        } finally {
            lock.writeLock().unlock();
        }
    }


    @Override
    public void update(Cart cart, long productId, int quantity) {
        lock.writeLock().lock();
        try {
            Product product = productDao.getProduct(productId);
            if (product.getStock() >= quantity) {
                cart.getItems().stream()
                        .filter(item -> item.getProduct().getId() == productId)
                        .findFirst()
                        .ifPresent(item -> item.setQuantity(quantity));
                recalculateCart(cart);
            } else {
                throw new OutOfStockException("We don't have so many items of this product", quantity, product.getStock());
            }
        } finally {
            lock.writeLock().unlock();
        }

    }

    @Override
    public void delete(Cart cart, long productId) {
        cart.getItems().removeIf(item ->
                item.getProduct().getId().equals(productId));
        recalculateCart(cart);
    }

    private void recalculateCart(Cart cart) {
        cart.setTotalQuantity(cart.getItems().stream()
                .map(CartItem::getQuantity)
                .collect(Collectors.summingInt(q -> q.intValue())));
        cart.setTotalPrice(cart.getItems().stream()
                .map(item -> item.getProduct().getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add));
    }
}
