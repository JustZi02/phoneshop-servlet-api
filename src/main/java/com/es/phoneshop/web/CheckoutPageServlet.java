package com.es.phoneshop.web;

import com.es.phoneshop.model.cart.CartService;
import com.es.phoneshop.model.cart.DefaultCartService;
import com.es.phoneshop.model.constants.StoreConstants;
import com.es.phoneshop.model.order.DefaultOrderService;
import com.es.phoneshop.model.order.Order;
import com.es.phoneshop.model.order.OrderService;
import com.es.phoneshop.model.order.PaymentMethod;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class CheckoutPageServlet extends HttpServlet {
    private CartService cartService;
    private OrderService orderService;

    public static final Predicate<String> PHONE_VALIDATOR = value ->
            value.matches("\\d{10,15}");

    public static final Predicate<String> NAME_VALIDATOR = value ->
            value.matches("^[a-zA-Zа-яА-ЯёЁ\\s-]+$");

    public static final Predicate<String> DATE_VALIDATOR = value ->
            value.matches("\\d{4}-\\d{2}-\\d{2}"); // Формат YYYY-MM-DD

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        cartService = DefaultCartService.getInstance();
        orderService = DefaultOrderService.getInstance();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Order order = orderService.getOrder(cartService.getCart(request.getSession()));
        request.setAttribute("order", order);
        request.setAttribute("paymentMethods", orderService.getPaymentMethods());
        request.getRequestDispatcher(StoreConstants.Pages.CHECKOUT).forward(request, response);

    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        Order order = orderService.getOrder(cartService.getCart(session));
        request.setAttribute("order", order);
        Map<String, String> errors = new HashMap<>();
        setRequiredParameter(request, "firstName", errors, order::setFirstName, NAME_VALIDATOR);
        setRequiredParameter(request, "lastName", errors, order::setLastName, NAME_VALIDATOR);
        setRequiredParameter(request, "deliveryAddress", errors, order::setDeliveryAddress, NAME_VALIDATOR);
        setRequiredParameter(request, "phone", errors, order::setPhone, PHONE_VALIDATOR);
        setPaymentMethod(request, errors, order);
        setDeliveryDate(request, errors, order);

        if (errors.isEmpty()) {
            orderService.placeOrder(order);
            response.sendRedirect(request.getContextPath() + "/order/overview/" + order.getSecureId());
        } else {
            request.setAttribute("errors", errors);
            request.setAttribute("order", order);
            request.setAttribute("paymentMethods", orderService.getPaymentMethods());
            request.getRequestDispatcher(StoreConstants.Pages.CHECKOUT).forward(request, response);
        }

    }

    public static void setRequiredParameter(HttpServletRequest request, String parameter, Map<String, String> errors,
                                            Consumer<String> consumer, Predicate<String> validationPredicate) {
        String value = request.getParameter(parameter);
        if (value == null || value.isEmpty()) {
            errors.put(parameter, "This field is required");
        } else if (!validationPredicate.test(value)) {
            errors.put(parameter, "This field is required");
        } else {
            consumer.accept(value);
        }
    }

    public static void setPaymentMethod(HttpServletRequest request, Map<String, String> errors, Order order) {
        String parameter = "paymentMethod";
        String value = request.getParameter(parameter);
        if (value == null || value.isEmpty()) {
            errors.put(parameter, "This field is required");
        } else {
            order.setPaymentMethod(PaymentMethod.valueOf(value));
        }
    }

    public static void setDeliveryDate(HttpServletRequest request, Map<String, String> errors, Order order) {
        String parameter = "deliveryDate";
        String value = request.getParameter(parameter);
        if (value == null || value.isEmpty()) {
            errors.put(parameter, "This field is required");
        } else {
            order.setDeliveryDate(Date.valueOf(value));
        }
    }
}
