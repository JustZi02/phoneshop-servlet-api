package com.es.phoneshop.web.servlet;

import com.es.phoneshop.model.cart.Cart;
import com.es.phoneshop.model.cart.CartService;
import com.es.phoneshop.model.cart.DefaultCartService;
import com.es.phoneshop.model.constants.StoreConstants;
import com.es.phoneshop.model.exceptions.OutOfStockException;
import com.es.phoneshop.model.product.ArrayListProductDao;
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
import java.util.HashMap;
import java.util.Map;

public class CartPageServlet extends HttpServlet {
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
        request.setAttribute(StoreConstants.Parameters.CART, cartService.getCart(request.getSession()));
        request.getRequestDispatcher(StoreConstants.Pages.CART).forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        String[] quantities = request.getParameterValues(StoreConstants.Parameters.PRODUCT_QUANTITY);
        String[] productIds = request.getParameterValues(StoreConstants.Parameters.PRODUCT_ID);
        Cart cart = cartService.getCart(request.getSession());
        Map<Long, String> errors = new HashMap<>();

        for (int i = 0; i < productIds.length; i++) {
            Long productId = Long.parseLong(productIds[i]);

            int quantity;
            quantities[i] = quantities[i].trim();
            try {
                quantity = Validation.quantityStringToInt(quantities[i], request.getLocale());
                cartService.update(cart, productId, quantity);
            } catch (ParseException | OutOfStockException e) {
                if (e.getClass() == ParseException.class) {
                    errors.put(productId, StoreConstants.Messages.INVALID_NUMBER_FORMAT_MESSAGE);
                } else if (e.getClass() == OutOfStockException.class) {
                    errors.put(productId, String.format(StoreConstants.Messages.OUT_OF_STOCK_MESSAGE,
                            ((OutOfStockException) e).getRequestedQuantity(),
                            ((OutOfStockException) e).getAvailableQuantity()));
                }
            }
        }
        if (errors.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/cart?message=Cart updated successfully!");
        } else {
            request.setAttribute("errors", errors);
            doGet(request, response);
        }
    }

}