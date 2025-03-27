package com.es.phoneshop.web;

import com.es.phoneshop.model.cart.Cart;
import com.es.phoneshop.model.cart.CartService;
import com.es.phoneshop.model.cart.DefaultCartService;
import com.es.phoneshop.model.exceptions.OutOfStockException;
import com.es.phoneshop.model.product.ArrayListProductDao;
import com.es.phoneshop.model.product.ProductDao;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.text.NumberFormat;
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
        request.setAttribute("cart", cartService.getCart(request));
        request.getRequestDispatcher("/WEB-INF/pages/cart.jsp").forward(request, response);
        HttpSession session = request.getSession();
        session.setAttribute("message", "");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        String[] quantities = request.getParameterValues("quantity");
        String[] productIds = request.getParameterValues("productId");
        Cart cart = cartService.getCart(request);
        Map<Long, String> errors = new HashMap<>();

        for (int i = 0; i < productIds.length; i++) {
            Long productId = Long.parseLong(productIds[i]);

            int quantity;
            quantities[i] = quantities[i].trim();
            try {
                if (!quantities[i].matches("[\\d\\s,.]+")) {
                    throw new ParseException("Invalid number format", 0);
                }
                NumberFormat formatter = NumberFormat.getInstance(request.getLocale());
                if (formatter.parse(quantities[i]).doubleValue() % 1 != 0) {
                    throw new ParseException("Invalid number format", 0);
                }
                quantity = formatter.parse(quantities[i]).intValue();
                if (quantity < 1) {
                    errors.put(productId, "Quantity must be a positive number.");
                }
                cartService.update(cart, productId, quantity);
            } catch (ParseException | OutOfStockException e) {
                if (e.getClass() == ParseException.class) {
                    errors.put(productId, "This field is for numbers only.");
                } else if (e.getClass() == OutOfStockException.class) {
                    errors.put(productId, "Sorry, we don't have enough product stock! " +
                            "Asked quantity: " + ((OutOfStockException) e).getRequestedQuantity() + ", available quantity: " + ((OutOfStockException) e).getAvailableQuantity());
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
