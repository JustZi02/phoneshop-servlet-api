package com.es.phoneshop.web;

import com.es.phoneshop.model.cart.Cart;
import com.es.phoneshop.model.cart.CartService;
import com.es.phoneshop.model.cart.DefaultCartService;
import com.es.phoneshop.model.constants.StoreConstants;
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
        request.getRequestDispatcher(StoreConstants.Pages.PRODUCT_LIST).forward(request, response);
        request.getSession().setAttribute("message", "");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String stringQuantity = request.getParameter("quantity").trim();
        Long productId = Long.parseLong(request.getParameter("productId"));
        int quantity;
        try {
            quantity = Validation.quantityStringToInt(stringQuantity, request.getLocale());
        } catch (ParseException e) {
            handleException(e.getMessage(), productId, request, response);
            return;
        }
        Cart cart = cartService.getCart(request.getSession());
        try {
            cartService.add(cart, productId, quantity);
        } catch (OutOfStockException e) {
            handleException(String.format(StoreConstants.Messages.OUT_OF_STOCK_MESSAGE,
                    e.getRequestedQuantity(), e.getAvailableQuantity()), productId, request, response);
            return;
        }
        request.getSession().setAttribute(StoreConstants.Parameters.SUCCESS_MESSAGE,
                StoreConstants.Messages.PRODUCT_ADDED_TO_CART_MESSAGE);
        response.sendRedirect(request.getContextPath() + "/products");
    }

    public void handleException(String message, long productId, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setAttribute(StoreConstants.Parameters.ERROR_MESSAGE, message);
        request.setAttribute(StoreConstants.Parameters.ERROR_PRODUCT_ID, productId);
        doGet(request, response);
    }
}
