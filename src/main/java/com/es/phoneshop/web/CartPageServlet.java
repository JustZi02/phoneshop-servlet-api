package com.es.phoneshop.web;

import com.es.phoneshop.model.cart.Cart;
import com.es.phoneshop.model.cart.CartService;
import com.es.phoneshop.model.cart.DefaultCartService;
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
        HttpSession session = request.getSession();
        request.setAttribute("cart", cartService.getCart(session));
        request.setAttribute("errors", session.getAttribute("errors"));
        session.removeAttribute("errors");
        request.setAttribute("quantities", session.getAttribute("quantities"));
        session.removeAttribute("quantities");
        request.getRequestDispatcher("/WEB-INF/pages/cart.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        String[] quantities = request.getParameterValues("quantity");
        String[] productIds = request.getParameterValues("productId");
        Cart cart = cartService.getCart(request.getSession());
        Map<Long, String> errors = new HashMap<>();
        Map<Long, String> quantityValues = new HashMap<>();

        for (int i = 0; i < productIds.length; i++) {
            Long productId = Long.parseLong(productIds[i]);
            String quantityInput = quantities[i].trim();
            quantityValues.put(productId, quantityInput);

            try {
                int quantity = Validation.quantityStringToInt(quantityInput, request.getLocale());
                cartService.update(cart, productId, quantity);
            } catch (ParseException | OutOfStockException e) {
                if (e instanceof ParseException) {
                    errors.put(productId, "This field is for numbers only.");
                } else if (e instanceof OutOfStockException) {
                    errors.put(productId, "Sorry, we don't have enough product stock! " +
                            "Asked quantity: " + ((OutOfStockException) e).getRequestedQuantity() +
                            ", available quantity: " + ((OutOfStockException) e).getAvailableQuantity());
                }
            }
        }

        if (errors.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/cart?message=Cart updated successfully!");
        } else {
            session.setAttribute("errors", errors);
            session.setAttribute("quantities", quantityValues);
            response.sendRedirect(request.getContextPath() + "/cart");
        }
    }


}
