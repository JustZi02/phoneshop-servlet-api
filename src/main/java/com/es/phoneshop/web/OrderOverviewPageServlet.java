package com.es.phoneshop.web;

import com.es.phoneshop.model.constants.StoreConstants;
import com.es.phoneshop.model.exceptions.OrderNotFoundException;
import com.es.phoneshop.model.order.ArrayListOrderDao;
import com.es.phoneshop.model.order.Order;
import com.es.phoneshop.model.order.OrderDao;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public class OrderOverviewPageServlet extends HttpServlet {
    private OrderDao orderDao;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        orderDao = ArrayListOrderDao.getInstance();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Order order;
        try {
            order = orderDao.getOrderSecureId(request.getPathInfo().substring(1));
        } catch (OrderNotFoundException e) {
            request.setAttribute(StoreConstants.Parameters.ERROR_MESSAGE, e.getMessage());
            request.getRequestDispatcher(StoreConstants.Pages.ERROR_ORDER_NOT_FOUND).forward(request, response);
            return;
        }
        request.setAttribute("order", order);
        request.getRequestDispatcher(StoreConstants.Pages.ORDER_OVERVIEW).forward(request, response);
    }
}
