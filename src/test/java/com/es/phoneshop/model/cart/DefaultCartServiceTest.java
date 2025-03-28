package com.es.phoneshop.model.cart;

import com.es.phoneshop.model.exceptions.OutOfStockException;
import com.es.phoneshop.model.product.ArrayListProductDao;
import com.es.phoneshop.model.product.Product;
import com.es.phoneshop.model.product.ProductDao;
import jakarta.servlet.http.HttpServletRequest;
import org.hamcrest.CoreMatchers;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.math.BigDecimal;
import java.util.Currency;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

public class DefaultCartServiceTest {
    private CartService cartService;
    private Cart cart;
    private ProductDao productDao;
    @Mock
    private HttpServletRequest request;
    @Mock
    String CART_SESSION_ATTRIBUTE;

    @Before
    public void setup() {
        cartService = DefaultCartService.getInstance();
        cart = new Cart();
        productDao = ArrayListProductDao.getInstance();
        Currency usd = Currency.getInstance("USD");
        productDao.save(new Product("htces4g", "HTC EVO Shift 4G", new BigDecimal(320), usd, 3, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/HTC/HTC%20EVO%20Shift%204G.jpg"));
        productDao.save(new Product("iphone", "Apple iPhone", new BigDecimal(200), usd, 10, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Apple/Apple%20iPhone.jpg"));
        productDao.save(new Product("sgs", "Samsung Galaxy S", new BigDecimal(100), usd, 100, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Samsung/Samsung%20Galaxy%20S.jpg"));
    }

    @Test
    public void ProductAddedQuantity() {
        cartService.add(cart, 2l, 5);
        assertThat(5, CoreMatchers.is(cart.getItems().get(0).getQuantity()));
        cartService.add(cart, 2l, 7);
        assertThat(12, CoreMatchers.is(cart.getItems().get(0).getQuantity()));
    }

    @Test(expected = OutOfStockException.class)
    public void AddToCartIncorrectQuantity() {
        cartService.add(cart, 0l, 1000);
    }

    @Test
    public void AddToCartCorrectQuantity() {
        cartService.add(cart, 0l, 1);
        assertThat(cart, notNullValue());
    }

    @Test
    public void DeleteItem() {
        cartService.delete(cart, 0l);
        assertThat(cart.getItems().size(), CoreMatchers.is(0));
    }
}
