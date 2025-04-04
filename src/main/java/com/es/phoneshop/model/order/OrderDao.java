package com.es.phoneshop.model.order;

import com.es.phoneshop.model.exceptions.OrderNotFoundException;

public interface OrderDao {
    Order getOrder(Long id);

    Order getOrderSecureId(String secureId) throws OrderNotFoundException;

    void save(Order order);
}
