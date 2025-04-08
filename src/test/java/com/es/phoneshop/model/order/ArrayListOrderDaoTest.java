package com.es.phoneshop.model.order;

import com.es.phoneshop.model.cart.Cart;
import com.es.phoneshop.model.cart.CartService;
import com.es.phoneshop.model.cart.DefaultCartService;
import com.es.phoneshop.model.product.ArrayListProductDao;
import com.es.phoneshop.model.product.Product;
import com.es.phoneshop.model.product.ProductDao;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.Currency;

import static org.junit.Assert.*;

public class ArrayListOrderDaoTest {
    private OrderDao orderDao;
    private OrderService orderService;
    private CartService cartService;
    private Cart cart;
    private ProductDao productDao;
    Order order;

    @Before
    public void setup() {
        cartService = DefaultCartService.getInstance();
        orderService = DefaultOrderService.getInstance();
        orderDao = ArrayListOrderDao.getInstance();
        cart = new Cart();
        productDao = ArrayListProductDao.getInstance();
        Currency usd = Currency.getInstance("USD");
        productDao.save(new Product("htces4g", "HTC EVO Shift 4G", new BigDecimal(320), usd, 100, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/HTC/HTC%20EVO%20Shift%204G.jpg"));
        productDao.save(new Product("iphone", "Apple iPhone", new BigDecimal(200), usd, 10, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Apple/Apple%20iPhone.jpg"));
        productDao.save(new Product("sgs", "Samsung Galaxy S", new BigDecimal(100), usd, 100, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Samsung/Samsung%20Galaxy%20S.jpg"));
        cartService.add(cart, 0l, 10);
        cartService.add(cart, 1l, 1);
        cartService.add(cart, 2l, 20);
    }

    @Test
    public void testChangeOrder() {
        order = orderService.getOrder(cart);
        cartService.delete(cart, 0l);
        order = orderService.getOrder(cart);
        assertEquals(order.getItems().size(), 2);

    }

    @Test
    public void testDefaultOrderService() {
        order = orderService.getOrder(cart);
        orderService.placeOrder(order);
        assertNotNull(order);
    }

    @Test
    public void calculateDeliveryCost() {
        order = orderService.getOrder(cart);
        assertEquals(order.getDeliveryCost(), BigDecimal.valueOf(5));

    }

    @Test
    public void testGetOrderCorrectId() {
        order = orderService.getOrder(cart);
        orderService.placeOrder(order);
        Order copyOrder = orderDao.getOrder(order.getId());
        assertEquals(order.getId(), copyOrder.getId());
    }

    @Test
    public void testGetOrderInCorrectId() {
        order = orderService.getOrder(cart);
        orderService.placeOrder(order);
        Order copyOrder = orderDao.getOrder(order.getId());
        assertFalse(order.getId().equals(8l));
    }
}
