package com.es.phoneshop.model.product;

import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.Currency;

import static org.junit.Assert.*;

public class ArrayListProductDaoTest
{
    private ProductDao productDao;

    @Before
    public void setup() {
        productDao = new ArrayListProductDao();
    }

    @Test
    public void testSaveProduct() throws NullPointerException{
        Currency usd = Currency.getInstance("USD");
        Product product  = new Product("testProduct", "Samsung Galaxy S", new BigDecimal(100), usd, 100, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Samsung/Samsung%20Galaxy%20S.jpg");
        productDao.save(product);
        assertNotNull(product.getId());
        assertEquals(product, productDao.getProduct(product.getId()));
        assertNotEquals(productDao.getProduct(product.getId()), productDao.getProduct(product.getId() - 1L));
    }
    @Test
    public void testUpdateProduct() throws NullPointerException{
        Currency usd = Currency.getInstance("USD");
        Product product = new Product(1L,"sgs1", "Samsung Galaxy S II", new BigDecimal(200), usd, 5, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Samsung/Samsung%20Galaxy%20S%20II.jpg");
        productDao.save(product);
        assertEquals(product.getCode(), productDao.getProduct(1L).getCode());
    }

    @Test
    public void testShowProduct() {

        Product product=productDao.getProduct(3L);
        assertNotNull(product);
    }

    @Test
    public void testFindProductsNoResults() {
        assertNotNull(productDao.findProducts());
    }

    @Test
    public void testDeleteProduct() {
        productDao.delete(3L);
        assertNull(productDao.getProduct(3L));
    }
}
