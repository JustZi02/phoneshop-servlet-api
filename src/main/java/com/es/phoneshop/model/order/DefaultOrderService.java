package com.es.phoneshop.model.order;

import com.es.phoneshop.model.cart.Cart;
import com.es.phoneshop.model.cart.CartItem;
import com.es.phoneshop.model.product.ArrayListProductDao;
import com.es.phoneshop.model.product.ProductDao;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

public class DefaultOrderService implements OrderService {
    private static final String CART_SESSION_ATTRIBUTE = DefaultOrderService.class.getName() + ".cart";
    private static DefaultOrderService instance;
    private final ReadWriteLock lock = new ReentrantReadWriteLock();
    private ProductDao productDao;
    private OrderDao orderDao;

    private DefaultOrderService() {
        productDao = ArrayListProductDao.getInstance();
        orderDao = ArrayListOrderDao.getInstance();
    }

    public static DefaultOrderService getInstance() {
        if (instance == null) {
            instance = new DefaultOrderService();
        }
        return instance;
    }

    @Override
    public Order getOrder(Cart cart) {
        Order order = new Order();
        order.setItems(cart.getItems().stream()
                .map(cartItem -> {
                    try {
                        return (CartItem) cartItem.clone();
                    } catch (CloneNotSupportedException e) {
                        throw new RuntimeException(e);
                    }
                })
                .collect(Collectors.toList()));
        order.setSubtotal(cart.getTotalPrice());
        order.setDeliveryCost(calculateDeliveryCost());
        order.setTotalPrice(order.getSubtotal().add(order.getDeliveryCost()));
        order.setTotalQuantity(cart.getTotalQuantity());
        return order;
    }

    private BigDecimal calculateDeliveryCost() {
        return BigDecimal.valueOf(5);
    }

    @Override
    public List<PaymentMethod> getPaymentMethods() {
        return Arrays.asList(PaymentMethod.values());
    }

    @Override
    public void placeOrder(Order order) {
        order.setSecureId(UUID.randomUUID().toString());
        orderDao.save(order);
    }
}
