import com.es.phoneshop.model.history.DefaultSearchHistoryService;
import com.es.phoneshop.model.history.SearchHistoryService;
import com.es.phoneshop.model.product.Product;
import com.es.phoneshop.model.product.ProductDao;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.*;

public class DefaultSearchHistoryServiceTest {
    private SearchHistoryService searchHistoryService;
    private HttpServletRequest request;
    private HttpSession session;
    ProductDao productDao;

    @Before
    public void setUp() {
        searchHistoryService = new DefaultSearchHistoryService();
        request = mock(HttpServletRequest.class);
        session = mock(HttpSession.class);
        when(request.getSession()).thenReturn(session);

    }

    @Test
    public void testAddNewProduct() {
        Currency usd = Currency.getInstance("USD");
        Product product = new Product("htces4g", "HTC EVO Shift 4G", new BigDecimal(320), usd, 3, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/HTC/HTC%20EVO%20Shift%204G.jpg");
        List<Product> products = new LinkedList<>();

        when(session.getAttribute("recentProducts")).thenReturn(products);

        searchHistoryService.Update(product, request);

        verify(session).setAttribute("recentProducts", products);
        assertEquals(1, products.size());
        assertEquals(product, products.get(0));
    }

    @Test
    public void testMoveExistingProductToFront() {
        Currency usd = Currency.getInstance("USD");
        Product product1 = new Product(1l, "test1", "HTC EVO Shift 47", new BigDecimal(320), usd, 3, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/HTC/HTC%20EVO%20Shift%204G.jpg");
        Product product2 = new Product(2l, "test2", "HTC EVO Shift 4G", new BigDecimal(320), usd, 3, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/HTC/HTC%20EVO%20Shift%204G.jpg");

        List<Product> products = new LinkedList<>();

        when(session.getAttribute("recentProducts")).thenReturn(products);

        searchHistoryService.Update(product1, request);
        searchHistoryService.Update(product2, request);

        verify(session, times(2)).setAttribute("recentProducts", products);

        assertEquals(2, products.size());
        assertEquals(product2, products.get(0));

    }

    @Test
    public void testRemoveOldestProductWhenLimitExceeded() {
        Currency usd = Currency.getInstance("USD");
        Product product1 = new Product(0l, "test1", "HTC EVO Shift 47", new BigDecimal(320), usd, 3, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/HTC/HTC%20EVO%20Shift%204G.jpg");
        Product product2 = new Product(1l, "test2", "HTC EVO Shift 4G", new BigDecimal(320), usd, 3, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/HTC/HTC%20EVO%20Shift%204G.jpg");
        Product product3 = new Product(2l, "test3", "HTC EVO Shift 4G", new BigDecimal(320), usd, 3, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/HTC/HTC%20EVO%20Shift%204G.jpg");
        Product product4 = new Product(3l, "test4", "HTC EVO Shift 47", new BigDecimal(320), usd, 3, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/HTC/HTC%20EVO%20Shift%204G.jpg");

        List<Product> products = new LinkedList<>();
        when(session.getAttribute("recentProducts")).thenReturn(products);

        searchHistoryService.Update(product1, request);
        searchHistoryService.Update(product2, request);
        searchHistoryService.Update(product3, request);
        searchHistoryService.Update(product4, request);

        verify(session, times(4)).setAttribute("recentProducts", products);
        assertEquals(3, products.size());

        assertFalse(products.contains(product1));
    }

}
