package com.es.phoneshop.model.product;

import com.es.phoneshop.model.AbstractDao;
import com.es.phoneshop.model.sorting.SortField;
import com.es.phoneshop.model.sorting.SortOrder;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

public class ArrayListProductDao extends AbstractDao<Product> implements ProductDao {
    private static ProductDao instance;

    private ArrayListProductDao() {
        super();
    }

    public static synchronized ProductDao getInstance() {
        if (instance == null) {
            instance = new ArrayListProductDao();
        }
        return instance;
    }

    @Override
    protected Long getId(Product product) {
        return product.getId();
    }

    @Override
    protected void setId(Product product, Long id) {
        product.setId(id);
    }

    @Override
    public Product getProduct(Long id) {
        lock.readLock().lock();
        try {
            return items.stream()
                    .filter(product -> id.equals(product.getId()))
                    .filter(this::NotNullPriceProducts)
                    .filter(this::NotOutOfStockProducts)
                    .findAny()
                    .orElseThrow(() -> new NoSuchElementException("Product with id " + id + " was not found."));
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public List<Product> findProducts(String query, SortField sortField, SortOrder sortOrder) {
        lock.readLock().lock();
        try {
            return items.stream()
                    .filter(product -> MatchQueryProducts(product, query))
                    .filter(this::NotNullPriceProducts)
                    .filter(this::NotOutOfStockProducts)
                    .sorted(Comparator.comparingLong((Product p) -> calculateWordMatch(p.getDescription(), query))
                            .thenComparingDouble(p -> calculateRelevance(p.getDescription(), query))
                            .reversed()
                            .thenComparing((p1, p2) -> sortByFieldAndOrder(p1, p2, sortField, sortOrder)))
                    .collect(Collectors.toList());
        } finally {
            lock.readLock().unlock();
        }
    }

    private int sortByFieldAndOrder(Product p1, Product p2, SortField sortField, SortOrder sortOrder) {
        if (sortField == null) {
            return 0;
        }

        return switch (sortField) {
            case description -> {
                int result = p1.getDescription().compareToIgnoreCase(p2.getDescription());
                yield sortOrder == SortOrder.desc ? result : -result;
            }
            case price -> {
                int result = p1.getPrice().compareTo(p2.getPrice());
                yield sortOrder == SortOrder.desc ? -result : result;
            }
        };
    }

    private long calculateWordMatch(String description, String query) {
        if (query == null || description == null) {
            return 0;
        }

        String[] queryWords = query.toLowerCase().split("\\s+");
        String[] descWords = description.toLowerCase().split("\\s+");

        return Arrays.stream(queryWords)
                .filter(word -> Arrays.asList(descWords).contains(word))
                .count();
    }

    private double calculateRelevance(String description, String query) {
        if (query == null || description == null) return 0.0;

        String[] queryWords = query.toLowerCase().split("\\s+");
        String[] descWords = description.toLowerCase().split("\\s+");

        long matchCount = Arrays.stream(queryWords)
                .filter(word -> Arrays.asList(descWords).contains(word))
                .count();

        return (double) matchCount / queryWords.length;
    }

    private boolean MatchQueryProducts(Product product, String query) {
        if (query == null) {
            return true;
        }

        String[] queryParts = query.trim().toLowerCase().split("\\s+");
        return Arrays.stream(queryParts)
                .anyMatch(part -> product.getDescription().toLowerCase().contains(part));
    }

    private boolean NotNullPriceProducts(Product product) {
        return product.getPrice() != null;
    }

    private boolean NotOutOfStockProducts(Product product) {
        return product.getStock() > 0;
    }

    @Override
    public void save(Product product) {
        lock.writeLock().lock();
        try {
            Objects.requireNonNull(product, "Product cannot be null");
            items.stream()
                    .filter(p -> p.getCode().equals(product.getCode()))
                    .findFirst()
                    .ifPresentOrElse(p -> {
                        p.setDescription(product.getDescription());
                        updatePriceHistory(p, product);
                        p.setPrice(product.getPrice());
                        p.setCurrency(product.getCurrency());
                        p.setStock(product.getStock());
                        p.setImageUrl(product.getImageUrl());
                    }, () -> {
                        product.setId(maxId++);
                        items.add(product);
                    });
        } finally {
            lock.writeLock().unlock();
        }
    }

    private void updatePriceHistory(Product p, Product newProduct) {
        List<PriceHistory> history = new ArrayList<>(p.getPriceHistory());

        for (PriceHistory newEntry : newProduct.getPriceHistory()) {
            boolean exists = history.stream().anyMatch(
                    h -> h.getPrice().compareTo(newEntry.getPrice()) == 0 &&
                            h.getDate().compareTo(newEntry.getDate()) == 0
            );
            if (!exists) {
                history.add(newEntry);
            }
        }

        history.sort(Comparator.comparing(PriceHistory::getDate));
        p.setPriceHistory(history);
    }

    @Override
    public void delete(Long id) {
        super.delete(id);
    }

    @Override
    public void updateQuantity(Long id, int quantity) {
        lock.writeLock().lock();
        try {
            Product product = getProduct(id);
            product.setStock(product.getStock() - quantity);
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public List<Product> advancedSearchProducts(String description, BigDecimal minPrice, BigDecimal maxPrice)
    {
        if(description == null && minPrice == null && maxPrice == null) {
            return items.stream().collect(Collectors.toList());
        }
        else {
            return items.stream()
                    .filter(product -> MatchQueryProducts(product, description))
                    .filter(this::NotNullPriceProducts)
                    .filter(this::NotOutOfStockProducts)
                    .collect(Collectors.toList());
        }
    }
}
