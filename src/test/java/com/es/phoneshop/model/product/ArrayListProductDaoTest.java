package com.es.phoneshop.model.product;

import com.es.phoneshop.model.sorting.SortField;
import com.es.phoneshop.model.sorting.SortOrder;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.Assert.*;

public class ArrayListProductDaoTest {
    private ProductDao productDao;

    @Before
    public void setup() {
        productDao = ArrayListProductDao.getInstance();
        Currency usd = Currency.getInstance("USD");
        productDao.save(new Product("htces4g", "HTC EVO Shift 4G", new BigDecimal(320), usd, 3, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/HTC/HTC%20EVO%20Shift%204G.jpg"));
        productDao.save(new Product("iphone", "Apple iPhone", new BigDecimal(200), usd, 10, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Apple/Apple%20iPhone.jpg"));
        productDao.save(new Product("sgs", "Samsung Galaxy S", new BigDecimal(100), usd, 100, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Samsung/Samsung%20Galaxy%20S.jpg"));
    }

    @Test
    public void testGetProductCorrectId() {
        Currency usd = Currency.getInstance("USD");
        productDao.save(new Product("iphone", "Apple iPhone", new BigDecimal(200), usd, 10, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Apple/Apple%20iPhone.jpg"));
        assertNotNull(productDao.getProduct(0L));
    }

    @Test(expected = NoSuchElementException.class)
    public void testGetProductNotCorrectId() {
        productDao.getProduct(14L);
    }

    @Test
    public void testFindProductsNoResults() {
        assertNotNull(productDao.findProducts(null, null, null));
    }

    @Test
    public void testSaveProduct() throws NullPointerException {
        Currency usd = Currency.getInstance("USD");
        Product product = new Product("testProduct", "Samsung Galaxy S", new BigDecimal(100), usd, 100, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Samsung/Samsung%20Galaxy%20S.jpg");
        productDao.save(product);
        assertNotNull(product.getId());
        assertEquals(product, productDao.getProduct(product.getId()));
    }

    @Test
    public void testSaveProductWithPriceHistoryChange() throws NullPointerException {
        Currency usd = Currency.getInstance("USD");
        productDao.save(new Product("sgs", "Samsung Galaxy S", new BigDecimal(110), usd, 100, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Samsung/Samsung%20Galaxy%20S.jpg"));
        productDao.save(new Product("sgs", "Samsung Galaxy S", new BigDecimal(120), usd, 100, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Samsung/Samsung%20Galaxy%20S.jpg"));
        productDao.save(new Product("sgs", "Samsung Galaxy S", new BigDecimal(130), usd, 100, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Samsung/Samsung%20Galaxy%20S.jpg"));
        assertTrue(productDao.getProduct(2L).getPriceHistory().size() == 4);
        assertTrue(productDao.getProduct(2L).getPriceHistory().get(0).getPrice().equals(BigDecimal.valueOf(100)));
        assertTrue(productDao.getProduct(2L).getPriceHistory().get(1).getPrice().equals(BigDecimal.valueOf(110)));
        assertTrue(productDao.getProduct(2L).getPriceHistory().get(2).getPrice().equals(BigDecimal.valueOf(120)));
        assertTrue(productDao.getProduct(2L).getPriceHistory().get(3).getPrice().equals(BigDecimal.valueOf(130)));
    }

    @Test
    public void testUpdateProduct() throws NullPointerException {
        Currency usd = Currency.getInstance("USD");
        Product productBeforeUpdate = new Product("sgs1", "Samsung Galaxy S II", new BigDecimal(200), usd, 5, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Samsung/Samsung%20Galaxy%20S%20II.jpg");
        Product productAfterUpdate = new Product("sgs1", "Samsung Galaxy S II", new BigDecimal(200), usd, 3, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Samsung/Samsung%20Galaxy%20S%20II.jpg");
        productDao.save(productBeforeUpdate);
        productDao.save(productAfterUpdate);
        assertEquals(productAfterUpdate.getStock(), productDao.getProduct(0L).getStock());
    }

    @Test(expected = NoSuchElementException.class)
    public void testDeleteProductCorrectId() {
        productDao.delete(3L);
    }

    @Test(expected = NoSuchElementException.class)
    public void testDeleteProductIncorrectId() throws NoSuchElementException {
        productDao.delete(15L);
    }

    @Test
    public void testFindProductsByQuery() {
        Currency usd = Currency.getInstance("USD");
        productDao.save(new Product("sgs2", "Samsung Galaxy S II", new BigDecimal(200), usd, 5, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Samsung/Samsung%20Galaxy%20S%20II.jpg"));
        List<Product> searched = productDao.findProducts("Samsung Galaxy S", null, null);
        assertTrue(searched.size() == 3);
        assertEquals(searched.get(0), productDao.getProduct(2L));
        assertEquals(searched.get(1), productDao.getProduct(3L));
        productDao.delete(3l);
    }

    @Test
    public void testFindProductsByPrice() {
        List<Product> searchedDesc = productDao.findProducts(null, SortField.price, SortOrder.desc);
        assertEquals(searchedDesc.get(0), productDao.getProduct(0L));
        assertEquals(searchedDesc.get(1), productDao.getProduct(1L));
        assertEquals(searchedDesc.get(2), productDao.getProduct(2L));

        List<Product> searchedAsc = productDao.findProducts(null, SortField.price, SortOrder.asc);
        assertEquals(searchedAsc.get(0), productDao.getProduct(2L));
        assertEquals(searchedAsc.get(1), productDao.getProduct(1L));
        assertEquals(searchedAsc.get(2), productDao.getProduct(0L));

    }

    @Test
    public void testFindProductsByDescription() {
        List<Product> searchedDesc = productDao.findProducts(null, SortField.description, SortOrder.desc);
        assertEquals(searchedDesc.get(0), productDao.getProduct(1L));
        assertEquals(searchedDesc.get(1), productDao.getProduct(0L));
        assertEquals(searchedDesc.get(2), productDao.getProduct(2L));

        List<Product> searchedAsc = productDao.findProducts(null, SortField.description, SortOrder.asc);
        assertEquals(searchedAsc.get(0), productDao.getProduct(2L));
        assertEquals(searchedAsc.get(1), productDao.getProduct(0L));
        assertEquals(searchedAsc.get(2), productDao.getProduct(1L));

    }
}
