package com.es.phoneshop.web;

import com.es.phoneshop.model.cart.Cart;
import com.es.phoneshop.model.cart.CartService;
import com.es.phoneshop.model.product.ArrayListProductDao;
import com.es.phoneshop.model.product.Product;
import com.es.phoneshop.model.product.ProductDao;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Locale;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ProductDetailsPageServletTest {
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private RequestDispatcher requestDispatcher;
    @Mock
    private ServletConfig config;
    private HttpSession session;
    private CartService cartService;
    private ProductDao productDao;
    @Mock
    private Cart cart;


    private ProductDetailsPageServlet servlet = new ProductDetailsPageServlet();

    @Before
    public void setup() throws ServletException {
        servlet.init(config);
        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        session = mock(HttpSession.class);
        cartService = mock(CartService.class);
        productDao = ArrayListProductDao.getInstance();
        Currency usd = Currency.getInstance("USD");
        productDao.save(new Product("htces4g", "HTC EVO Shift 4G", new BigDecimal(320), usd, 3, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/HTC/HTC%20EVO%20Shift%204G.jpg"));
        productDao.save(new Product("iphone", "Apple iPhone", new BigDecimal(200), usd, 10, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Apple/Apple%20iPhone.jpg"));
        productDao.save(new Product("sgs", "Samsung Galaxy S", new BigDecimal(100), usd, 100, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Samsung/Samsung%20Galaxy%20S.jpg"));

        cart = mock(Cart.class);
        requestDispatcher = mock(RequestDispatcher.class);

        when(request.getSession()).thenReturn(session);
        when(request.getLocale()).thenReturn(Locale.US);
        when(request.getRequestDispatcher(anyString())).thenReturn(requestDispatcher);
    }

    @Test()
    public void testDoGet() throws Exception {
        when(request.getPathInfo()).thenReturn("/1");
        servlet.doGet(request, response);
        verify(requestDispatcher).forward(request, response);
        verify(request).setAttribute(eq("product"), eq(productDao.getProduct(1L)));
    }

    @Test
    public void testDoPost() throws Exception {
        when(request.getPathInfo()).thenReturn("/1");
        when(request.getParameter("quantity")).thenReturn("2");
        when(cartService.getCart(request.getSession())).thenReturn(cart);
        servlet.doPost(request, response);
        verify(session).setAttribute(eq("com.es.phoneshop.model.cart.DefaultCartService.cart"), anyObject());
    }

    @Test
    public void testDoPostWithInvalidQuantity() throws Exception {
        when(request.getPathInfo()).thenReturn("/1");
        when(request.getParameter("quantity")).thenReturn("invalid");
        servlet.doPost(request, response);
        verify(session).setAttribute(eq("recentProducts"), anyObject());
    }
}