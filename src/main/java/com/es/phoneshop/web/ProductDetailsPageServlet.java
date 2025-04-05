package com.es.phoneshop.web;

import com.es.phoneshop.model.cart.Cart;
import com.es.phoneshop.model.cart.CartService;
import com.es.phoneshop.model.cart.DefaultCartService;
import com.es.phoneshop.model.constants.StoreConstants;
import com.es.phoneshop.model.exceptions.OutOfStockException;
import com.es.phoneshop.model.history.DefaultSearchHistoryService;
import com.es.phoneshop.model.history.SearchHistoryService;
import com.es.phoneshop.model.product.ArrayListProductDao;
import com.es.phoneshop.model.product.Product;
import com.es.phoneshop.model.product.ProductDao;
import com.es.phoneshop.model.validation.Validation;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.text.ParseException;
import java.util.NoSuchElementException;

public class ProductDetailsPageServlet extends HttpServlet {
    private ProductDao productDao;
    private CartService cartService;
    private SearchHistoryService searchHistoryService;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        productDao = ArrayListProductDao.getInstance();
        cartService = DefaultCartService.getInstance();
        searchHistoryService = DefaultSearchHistoryService.getInstance();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        try {
            Product product = productDao.getProduct(parseProductId(request));
            searchHistoryService.update(product, request);
            request.setAttribute("product", product);
        } catch (NoSuchElementException e) {
            request.setAttribute(StoreConstants.Parameters.ERROR_MESSAGE, e.getMessage());
            request.getRequestDispatcher(StoreConstants.Pages.ERROR_NO_SUCH_ELEMENT_EXCEPTION).forward(request, response);
        }
        request.setAttribute("cart", cartService.getCart(session).toString());
        request.getRequestDispatcher(StoreConstants.Pages.PRODUCT_DETAILS).forward(request, response);
        session.setAttribute("message", "");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String stringQuantity = request.getParameter("quantity").trim();
        Long productId = parseProductId(request);
        int quantity;
        try {
            quantity = Validation.quantityStringToInt(stringQuantity, request.getLocale());
        } catch (ParseException e) {
            request.setAttribute(StoreConstants.Parameters.ERROR_MESSAGE,
                    StoreConstants.Messages.INVALID_NUMBER_FORMAT_MESSAGE);
            doGet(request, response);
            return;
        }

        HttpSession session = request.getSession();
        Cart cart = cartService.getCart(session);
        try {
            cartService.add(cart, productId, quantity);
        } catch (OutOfStockException e) {
            request.setAttribute(StoreConstants.Parameters.ERROR_MESSAGE,
                    String.format(StoreConstants.Messages.OUT_OF_STOCK_MESSAGE,
                            e.getRequestedQuantity(), e.getAvailableQuantity()));
            doGet(request, response);
            return;
        }

        session.setAttribute(StoreConstants.Parameters.SUCCESS_MESSAGE,
                String.format(StoreConstants.Messages.PRODUCT_ADDED_TO_CART_MESSAGE));
        response.sendRedirect(request.getContextPath() + "/products/" + productId);
    }

    private long parseProductId(HttpServletRequest request) throws NumberFormatException {
        return Long.parseLong(request.getPathInfo().substring(1));
    }

}
