package com.es.phoneshop.model.product;

import com.es.phoneshop.model.sorting.SortField;
import com.es.phoneshop.model.sorting.SortOrder;

import java.util.List;

public interface ProductDao {
    Product getProduct(Long id);

    List<Product> findProducts(String query, SortField sortField, SortOrder sortOrder);

    void save(Product product);

    void delete(Long id);

    void updateQuantity(Long id, int quantity);
}
