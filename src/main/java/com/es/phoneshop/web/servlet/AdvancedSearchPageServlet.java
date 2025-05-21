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

public class AdvancedSearchPageServlet extends HttpServlet {
    ProductDao productDao;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        productDao = ArrayListProductDao.getInstance();
    }
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setAttribute("products", productDao.advancedSearchProducts());
        request.getRequestDispatcher(StoreConstants.Pages.ADVANCED_SEARCH).forward(request, response);
    }


}
