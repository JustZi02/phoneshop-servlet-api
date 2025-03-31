package com.es.phoneshop.web;

import com.es.phoneshop.model.cart.Cart;
import com.es.phoneshop.model.cart.CartService;
import com.es.phoneshop.model.cart.DefaultCartService;
import com.es.phoneshop.model.exceptions.OutOfStockException;
import com.es.phoneshop.model.history.DefaultSearchHistoryService;
import com.es.phoneshop.model.history.SearchHistoryService;
import com.es.phoneshop.model.product.ArrayListProductDao;
import com.es.phoneshop.model.product.Product;
import com.es.phoneshop.model.product.ProductDao;
import com.es.phoneshop.model.validation.Validation;
import jakarta.servlet.RequestDispatcher;
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
            request.setAttribute("errorMessage", e.getMessage());
            request.getRequestDispatcher("/WEB-INF/pages/errorNoSuchElementException.jsp").forward(request, response);
        }
        request.setAttribute("cart", cartService.getCart(request.getSession()).toString());
        request.getRequestDispatcher("/WEB-INF/pages/productDetails.jsp").forward(request, response);
        session.setAttribute("message", "");
        session.setAttribute("errorMessage", "");
        session.setAttribute("quantity", "");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String stringQuantity = request.getParameter("quantity").trim();
        Long productId = parseProductId(request);
        Validation validation = new Validation();
        int quantity;
        try {
            quantity = validation.quantityStringToInt(stringQuantity, request.getLocale());
        } catch (ParseException e) {
            request.getSession().setAttribute("errorMessage", "Invalid number format.");
            request.getSession().setAttribute("quantity", stringQuantity);
            response.sendRedirect(request.getContextPath() + "/products/" + productId);
            return;
        }

        Cart cart = cartService.getCart(request.getSession());
        try {
            cartService.add(cart, productId, quantity);
        } catch (OutOfStockException e) {
            request.getSession().setAttribute("errorMessage", "Sorry, we don't have enough product stock! " +
                    "Asked quantity: " + e.getRequestedQuantity() + ", available quantity: " + e.getAvailableQuantity());
            request.getSession().setAttribute("quantity", stringQuantity);
            response.sendRedirect(request.getContextPath() + "/products/" + productId);
            return;
        }

        HttpSession session = request.getSession();
        session.setAttribute("message", "Product added successfully!");
        response.sendRedirect(request.getContextPath() + "/products/" + productId);
    }

    private long parseProductId(HttpServletRequest request) throws NumberFormatException {
        return Long.parseLong(request.getPathInfo().substring(1));
    }

}
