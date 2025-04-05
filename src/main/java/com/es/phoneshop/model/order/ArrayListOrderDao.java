package com.es.phoneshop.model.order;

import com.es.phoneshop.model.AbstractDao;
import com.es.phoneshop.model.exceptions.OrderNotFoundException;

public class ArrayListOrderDao extends AbstractDao<Order> implements OrderDao {
    private static OrderDao instance;

    private ArrayListOrderDao() {
    }

    public static synchronized OrderDao getInstance() {
        if (instance == null) {
            instance = new ArrayListOrderDao();
        }
        return instance;
    }

    @Override
    protected Long getId(Order item) {
        return item.getId();
    }

    @Override
    protected void setId(Order item, Long id) {
        item.setId(id);
    }

    @Override
    public Order getOrder(Long id) {
        return super.get(id);
    }

    @Override
    public void save(Order order) {
        super.save(order);
    }

    @Override
    public Order getOrderSecureId(String secureId) {
        lock.readLock().lock();
        try {
            return items.stream()
                    .filter(order -> secureId.equals(order.getSecureId()))
                    .findAny()
                    .orElseThrow(() -> new OrderNotFoundException("Order with secureId " + secureId + " was not found."));
        } finally {
            lock.readLock().unlock();
        }
    }
}
