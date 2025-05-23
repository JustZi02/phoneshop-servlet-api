package com.es.phoneshop.web.servlet;

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
import org.codehaus.plexus.util.StringUtils;

import java.io.IOException;
import java.sql.Date;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class CheckoutPageServlet extends HttpServlet {
    private CartService cartService;
    private OrderService orderService;

    public static final Predicate<String> PHONE_VALIDATOR = value ->
            value.matches("^\\+\\d{12}$");

    public static final Predicate<String> NAME_VALIDATOR = value ->
            value.matches("^[a-zA-Zа-яА-ЯёЁ\\s-]+$");

    public static final Predicate<String> ADDRESS_VALIDATOR = value ->
            value.matches("^[a-zA-Zа-яА-ЯёЁ0-9\\s,/-]+$");

    public static final Predicate<String> DATE_VALIDATOR = value ->
            value.matches("\\d{4}-\\d{2}-\\d{2}"); // YYYY-MM-DD

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        cartService = DefaultCartService.getInstance();
        orderService = DefaultOrderService.getInstance();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Order order = orderService.getOrder(cartService.getCart(request.getSession()));
        request.setAttribute(StoreConstants.Parameters.ORDER, order);
        request.setAttribute("paymentMethods", orderService.getPaymentMethods());
        request.getRequestDispatcher(StoreConstants.Pages.CHECKOUT).forward(request, response);

    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        Order order = orderService.getOrder(cartService.getCart(session));
        request.setAttribute(StoreConstants.Parameters.ORDER, order);
        Map<String, String> errors = new HashMap<>();
        setRequiredParameter(request, "firstName", errors, order::setFirstName, NAME_VALIDATOR);
        setRequiredParameter(request, "lastName", errors, order::setLastName, NAME_VALIDATOR);
        setRequiredParameter(request, "deliveryAddress", errors, order::setDeliveryAddress, ADDRESS_VALIDATOR);
        setRequiredParameter(request, "phone", errors, order::setPhone, PHONE_VALIDATOR);
        setPaymentMethod(request, errors, order);
        setDeliveryDate(request, errors, order);

        if (errors.isEmpty()) {
            orderService.placeOrder(order);
            cartService.clear(cartService.getCart(request.getSession()));
            response.sendRedirect(request.getContextPath() + "/order/overview/" + order.getSecureId());
        } else {
            request.setAttribute("errors", errors);
            request.setAttribute(StoreConstants.Parameters.ORDER, order);
            request.setAttribute("paymentMethods", orderService.getPaymentMethods());
            request.getRequestDispatcher(StoreConstants.Pages.CHECKOUT).forward(request, response);
        }

    }

    public static void setRequiredParameter(HttpServletRequest request, String parameter, Map<String, String> errors,
                                            Consumer<String> consumer, Predicate<String> validationPredicate) {
        String value = request.getParameter(parameter);
        if (value == null || value.isEmpty() || StringUtils.isBlank(value)) {
            errors.put(parameter, "This field is required.");
        } else if (!validationPredicate.test(value)) {
            errors.put(parameter, "Check the field correctly.");
        } else {
            consumer.accept(value);
        }
    }

    public static void setPaymentMethod(HttpServletRequest request, Map<String, String> errors, Order order) {
        String parameter = "paymentMethod";
        String value = request.getParameter(parameter);
        if (value == null || value.isEmpty()) {
            errors.put(parameter, "This field is required.");
        } else {
            order.setPaymentMethod(PaymentMethod.valueOf(value));
        }
    }

    public static void setDeliveryDate(HttpServletRequest request, Map<String, String> errors, Order order) {
        String parameter = "deliveryDate";
        String value = request.getParameter(parameter);
        if (value == null || value.isEmpty()) {
            errors.put(parameter, "This field is required.");
        } else {
            Date date = Date.valueOf(value);
            if (date.before(Date.valueOf(LocalDate.now()))) {
                errors.put(parameter, "Check your date correctly.");
            }
            order.setDeliveryDate(Date.valueOf(value));
        }
    }
}
