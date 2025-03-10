package com.es.phoneshop.model.product;

import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.NoSuchElementException;

import static org.junit.Assert.*;

public class ArrayListProductDaoTest {
    private ProductDao productDao;

    @Before
    public void setup() {
        productDao = new ArrayListProductDao();
    }

    @Test
    public void testGetProductCorrectId() {
        Currency usd = Currency.getInstance("USD");
        Product product = new Product("iphone", "Apple iPhone", new BigDecimal(200), usd, 10, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Apple/Apple%20iPhone.jpg");
        assertNotNull(productDao.getProduct(3L));
        assertEquals(productDao.getProduct(3L).getStock(), product.getStock());
    }

    @Test
    public void testGetProductNotCorrectId() {
        assertNull(productDao.getProduct(14L));
    }

    @Test
    public void testFindProductsNoResults() {
        assertNotNull(productDao.findProducts());
    }

    @Test
    public void testSaveProduct() throws NullPointerException {
        Currency usd = Currency.getInstance("USD");
        Product product = new Product("testProduct", "Samsung Galaxy S", new BigDecimal(100), usd, 100, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Samsung/Samsung%20Galaxy%20S.jpg");
        productDao.save(product);
        assertNotNull(product.getId());
        assertEquals(product, productDao.getProduct(product.getId()));
        assertNotEquals(productDao.getProduct(product.getId()), productDao.getProduct(product.getId() - 1L));
    }

    @Test
    public void testUpdateProduct() throws NullPointerException {
        Currency usd = Currency.getInstance("USD");
        Product product = new Product(1L, "sgs1", "Samsung Galaxy S II", new BigDecimal(200), usd, 5, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Samsung/Samsung%20Galaxy%20S%20II.jpg");
        productDao.save(product);
        assertEquals(product.getCode(), productDao.getProduct(1L).getCode());
    }

    @Test
    public void testDeleteProductCorrectId() {
        productDao.delete(3L);
        assertNull(productDao.getProduct(3L));
    }

    @Test(expected = NoSuchElementException.class)
    public void testDeleteProductIncorrectId() throws NoSuchElementException {
        productDao.delete(15L);
    }
}
