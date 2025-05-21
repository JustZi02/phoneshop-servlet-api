package com.es.phoneshop.web.servlet;

import com.es.phoneshop.model.constants.StoreConstants;
import com.es.phoneshop.model.product.ArrayListProductDao;
import com.es.phoneshop.model.product.ProductDao;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.math.BigDecimal;

public class AdvancedSearchPageServlet extends HttpServlet {
    ProductDao productDao;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        productDao = ArrayListProductDao.getInstance();
    }
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String description = request.getParameter("description");
        String minPriceParam = request.getParameter("minPrice");
        String maxPriceParam = request.getParameter("maxPrice");

        boolean searchPerformed = request.getParameterMap().containsKey("description")
                || request.getParameterMap().containsKey("minPrice")
                || request.getParameterMap().containsKey("maxPrice");

        BigDecimal minPrice = null;
        BigDecimal maxPrice = null;
        boolean hasError = false;

        try {
            minPrice = stringToBigDecimal(minPriceParam);
        } catch (IllegalArgumentException e) {
            request.setAttribute("minPriceError", e.getMessage());
            hasError = true;
        }

        try {
            maxPrice = stringToBigDecimal(maxPriceParam);
        } catch (IllegalArgumentException e) {
            request.setAttribute("maxPriceError", e.getMessage());
            hasError = true;
        }
        if (searchPerformed && !hasError) {
            request.setAttribute("products", productDao.advancedSearchProducts(description, minPrice, maxPrice));
        }
        request.setAttribute("searchPerformed", searchPerformed);
        request.getRequestDispatcher(StoreConstants.Pages.ADVANCED_SEARCH).forward(request, response);
    }

    private BigDecimal stringToBigDecimal(String param) {
        if (param == null || param.trim().isEmpty()) {
            return null;
        }
        try {
            return new BigDecimal(param.trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid number format: " + param, e);
        }
    }


}
