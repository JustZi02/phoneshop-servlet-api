package com.es.phoneshop.web;

import com.es.phoneshop.model.cart.Cart;
import com.es.phoneshop.model.cart.CartService;
import com.es.phoneshop.model.cart.DefaultCartService;
import com.es.phoneshop.model.exceptions.OutOfStockException;
import com.es.phoneshop.model.product.ArrayListProductDao;
import com.es.phoneshop.model.product.Product;
import com.es.phoneshop.model.product.ProductDao;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.text.NumberFormat;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;

public class ProductDetailsPageServlet extends HttpServlet {
    private ProductDao productDao;
    private CartService cartService;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        productDao = ArrayListProductDao.getInstance();
        cartService = DefaultCartService.getInstance();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        UpdateSearchHistory(session, request);
        request.setAttribute("product", productDao.getProduct(parseProductId(request)));
        try {
            request.setAttribute("product", productDao.getProduct(parseProductId(request)));
            request.setAttribute("cart", cartService.getCart(request).toString());
        } catch (NoSuchElementException e) {
            request.setAttribute("errorMessage", e.getMessage());
            request.getRequestDispatcher("/WEB-INF/pages/errorNoSuchElementException.jsp").forward(request, response);
        }
        request.getRequestDispatcher("/WEB-INF/pages/productDetails.jsp").forward(request, response);
        session.setAttribute("message", "");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String stringQuantity = request.getParameter("quantity");
        Long productId = parseProductId(request);
        int quantity;
        try {
            NumberFormat formatter = NumberFormat.getInstance(request.getLocale());
            quantity = Integer.parseInt(stringQuantity);
        } catch (NumberFormatException e) {
            request.setAttribute("errorMessage", "This field is for numbers only.");
            doGet(request, response);
            return;
        }
        if (quantity < 1) {
            request.setAttribute("errorMessage", "Quantity must be a positive number.");
            doGet(request, response);
            return;
        }
        Cart cart = cartService.getCart(request);
        try {
            cartService.add(cart, productId, quantity);
        } catch (OutOfStockException e) {
            request.setAttribute("errorMessage", "Sorry, we don't have enough product stock! " +
                    "Asked quantity: " + e.getRequestedQuantity() + ", available quantity: " + e.getAvailableQuantity());
            doGet(request, response);
            return;
        }
        HttpSession session = request.getSession();
        session.setAttribute("message", "Product added successfully!");
        response.sendRedirect(request.getContextPath() + "/products/" + productId);

    }

    private long parseProductId(HttpServletRequest request) throws NumberFormatException {
        return Long.parseLong(request.getPathInfo().substring(1));
    }

    private void UpdateSearchHistory(HttpSession session, HttpServletRequest request) {
        List<Product> recentProducts = (List<Product>) session.getAttribute("recentProducts");
        if (recentProducts == null) {
            recentProducts = new LinkedList<>();
        }
        recentProducts.removeIf(p -> p.getId() == parseProductId(request));

        recentProducts.add(0, productDao.getProduct(parseProductId(request)));

        if (recentProducts.size() > 3) {
            recentProducts.remove(3);
        }
        session.setAttribute("recentProducts", recentProducts);
    }
}
