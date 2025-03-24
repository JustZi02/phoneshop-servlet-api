package com.es.phoneshop.model.history;

import com.es.phoneshop.model.product.Product;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class DefaultSearchHistoryService implements SearchHistoryService {
    private static final String RECENT_PRODUCTS = "recentProducts";
    private static final int MAX_RECENT_PRODUCTS = 3;

    private static volatile DefaultSearchHistoryService instance;
    private final ReadWriteLock lock = new ReentrantReadWriteLock();

    private DefaultSearchHistoryService() {
    }

    public static DefaultSearchHistoryService getInstance() {
        if (instance == null) {
            synchronized (DefaultSearchHistoryService.class) {
                if (instance == null) {
                    instance = new DefaultSearchHistoryService();
                }
            }
        }
        return instance;
    }

    @Override
    public void update(Product product, HttpServletRequest request) {
        HttpSession session = request.getSession();

        lock.writeLock().lock();
        try {
            List<Product> products = (List<Product>) session.getAttribute(RECENT_PRODUCTS);

            if (products == null) {
                products = new LinkedList<>();
            }

            products.removeIf(p -> p.getId().equals(product.getId()));
            products.add(0, product);

            if (products.size() > MAX_RECENT_PRODUCTS) {
                products.remove(MAX_RECENT_PRODUCTS);
            }

            session.setAttribute(RECENT_PRODUCTS, products);
        } finally {
            lock.writeLock().unlock();
        }
    }
}
