package com.es.phoneshop.model.order;


import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class ArrayListOrderDao implements OrderDao {
    private static OrderDao instance;

    private final ReadWriteLock lock = new ReentrantReadWriteLock();
    private List<Order> orders;
    private long maxId;

    private ArrayListOrderDao() {
        orders = new ArrayList<Order>();
        maxId = 0l;
    }

    public static synchronized OrderDao getInstance() {
        if (instance == null) {
            instance = new ArrayListOrderDao();
        }
        return instance;
    }

    @Override
    public Order getOrder(Long id) {
        lock.readLock().lock();
        try {
            return orders.stream()
                    .filter(order -> id.equals(order.getId()))
                    .findAny()
                    .orElseThrow(() -> new NoSuchElementException("Order with id " + id + " was not found."));
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public void save(Order order) throws NullPointerException {
        lock.writeLock().lock();
        try {
            Long id = order.getId();
            if (id != null) {
                orders.remove(getOrder(id));
                orders.add(order);
            } else {
                order.setId(maxId++);
                orders.add(order);
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public Order getOrderSecureId(String secureId) {
        lock.readLock().lock();
        try {
            return orders.stream()
                    .filter(order -> secureId.equals(order.getSecureId()))
                    .findAny()
                    .orElseThrow(() -> new NoSuchElementException("Order with id " + secureId + " was not found."));
        } finally {
            lock.readLock().unlock();
        }
    }
}
