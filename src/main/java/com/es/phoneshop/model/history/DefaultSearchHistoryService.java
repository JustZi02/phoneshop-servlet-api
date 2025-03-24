package com.es.phoneshop.model.history;

import com.es.phoneshop.model.product.Product;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import java.util.LinkedList;
import java.util.List;

public class DefaultSearchHistoryService implements SearchHistoryService {


    @Override
    public void Update(Product product, HttpServletRequest request) {
        HttpSession session = request.getSession();
        List<Product> products = (List<Product>) session.getAttribute("recentProducts");

        if (products == null) {
            products = new LinkedList<>();
        }

        products.removeIf(p -> p.getId() == product.getId());

        products.add(0, product);

        if (products.size() > 3) {
            products.remove(3);
        }

        session.setAttribute("recentProducts", products);
    }

}
