package com.es.phoneshop.web;

import com.es.phoneshop.model.cart.Cart;
import com.es.phoneshop.model.cart.CartService;
import com.es.phoneshop.model.cart.DefaultCartService;
import com.es.phoneshop.model.exceptions.OutOfStockException;
import com.es.phoneshop.model.product.ArrayListProductDao;
import com.es.phoneshop.model.product.ProductDao;
import com.es.phoneshop.model.sorting.SortField;
import com.es.phoneshop.model.sorting.SortOrder;
import com.es.phoneshop.model.validation.Validation;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.text.ParseException;
import java.util.Optional;

public class ProductListPageServlet extends HttpServlet {
    ProductDao productDao;
    CartService cartService;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        productDao = ArrayListProductDao.getInstance();
        cartService = DefaultCartService.getInstance();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String query = request.getParameter("query");
        String sortField = request.getParameter("field");
        String sortOrder = request.getParameter("order");
        request.setAttribute("products",
                productDao.findProducts(
                        query,
                        Optional.ofNullable(sortField).map(SortField::valueOf).orElse(null),
                        Optional.ofNullable(sortOrder).map(SortOrder::valueOf).orElse(null)
                ));
        request.getRequestDispatcher("/WEB-INF/pages/productList.jsp").forward(request, response);
        HttpSession session = request.getSession();
        session.setAttribute("message", "");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String stringQuantity = request.getParameter("quantity").trim();
        Long productId = Long.parseLong(request.getParameter("productId"));
        Validation validation = new Validation();
        String errorMessage = "";

        int quantity;
        try {
            quantity = validation.QuantityStringToInt(stringQuantity, request.getLocale());
        } catch (ParseException e) {
            errorMessage = "Invalid number format.";
            request.setAttribute("errorMessage", errorMessage);
            request.setAttribute("errorItem", productId);
            doGet(request, response);
            return;
        }

        Cart cart = cartService.getCart(request);
        try {
            cartService.add(cart, productId, quantity);
        } catch (OutOfStockException e) {
            errorMessage = "Sorry, we don't have enough product stock! " +
                    "Asked quantity: " + e.getRequestedQuantity() + ", available quantity: " + e.getAvailableQuantity();
            request.setAttribute("errorMessage", errorMessage);
            request.setAttribute("errorItem", productId);
            doGet(request, response);
            return;
        }

        if (errorMessage.equals("")) {
            request.getSession().setAttribute("message", "Product added successfully!");
        }
        response.sendRedirect(request.getContextPath() + "/products");
    }
}
