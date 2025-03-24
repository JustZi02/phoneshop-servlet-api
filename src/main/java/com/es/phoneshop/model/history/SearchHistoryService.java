package com.es.phoneshop.model.history;

import com.es.phoneshop.model.product.Product;
import jakarta.servlet.http.HttpServletRequest;

public interface SearchHistoryService {
    public void Update(Product product, HttpServletRequest request);
}
