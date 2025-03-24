package com.es.phoneshop.model.product;


import com.es.phoneshop.model.sorting.SortField;
import com.es.phoneshop.model.sorting.SortOrder;

import java.util.*;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

public class ArrayListProductDao implements ProductDao {
    private static ProductDao instance;

    private final ReadWriteLock lock = new ReentrantReadWriteLock();
    private List<Product> products;
    private long maxId;

    private ArrayListProductDao() {
        products = new ArrayList<Product>();
        maxId = 0l;
    }

    public static synchronized ProductDao getInstance() {
        if (instance == null) {
            instance = new ArrayListProductDao();
        }
        return instance;
    }

    @Override
    public Product getProduct(Long id) {
        lock.readLock().lock();
        try {
            return products.stream()
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
            return products.stream()
                    .filter(product -> MatchQueryProducts(product, query))
                    .filter(this::NotNullPriceProducts)
                    .filter(this::NotOutOfStockProducts)
                    .sorted(Comparator.comparingLong((Product product) -> calculateWordMatch(product.getDescription(), query))
                            .thenComparingDouble((Product product) -> calculateRelevance(product.getDescription(), query))
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
        String descLower = description.toLowerCase();
        String queryLower = query.toLowerCase();

        String[] queryWords = queryLower.split("\\s+");
        String[] descWords = descLower.split("\\s+");

        long exactWordMatches = Arrays.stream(queryWords)
                .filter(word -> Arrays.asList(descWords).contains(word))
                .count();
        return exactWordMatches;
    }

    private double calculateRelevance(String description, String query) {
        if (query == null || description == null) {
            return 0.0;
        }
        String descLower = description.toLowerCase();
        String queryLower = query.toLowerCase();

        String[] queryWords = queryLower.split("\\s+");
        String[] descWords = descLower.split("\\s+");

        long exactWordMatches = Arrays.stream(queryWords)
                .filter(word -> Arrays.asList(descWords).contains(word))
                .count();

        double relevance = (double) exactWordMatches / queryWords.length;
        return relevance;
    }


    private boolean MatchQueryProducts(Product product, String query) {
        if (query == null) {
            return true;
        }
        query = query.trim();
        String[] queryParts = query.toLowerCase().split("\\s+");
        return Arrays.stream(queryParts).anyMatch(product.getDescription().toLowerCase()::contains);
    }

    private boolean NotNullPriceProducts(Product product) {
        return product.getPrice() != null;
    }

    private boolean NotOutOfStockProducts(Product product) {
        return product.getStock() > 0;
    }

    @Override
    public void save(Product product) throws NullPointerException {
        lock.writeLock().lock();
        try {
            Objects.requireNonNull(product, "Product cannot be null");
            products.stream().filter(p -> p.getCode().equals(product.getCode())).findFirst().ifPresentOrElse(p -> {
                p.setDescription(product.getDescription());
                updatePriceHistory(p, product);
                p.setPrice(product.getPrice());
                p.setCurrency(product.getCurrency());
                p.setStock(product.getStock());
                p.setImageUrl(product.getImageUrl());
            }, () -> {
                product.setId(maxId++);
                products.add(product);
            });
        } finally {
            lock.writeLock().unlock();
        }
    }

    private void updatePriceHistory(Product p, Product product) {
        List<PriceHistory> priceHistoryList = new ArrayList<>(p.getPriceHistory());

        for (PriceHistory newHistory : product.getPriceHistory()) {
            boolean exists = priceHistoryList.stream()
                    .anyMatch(history -> history.getPrice().compareTo(newHistory.getPrice()) == 0
                            && history.getDate().compareTo(newHistory.getDate()) == 0);
            if (!exists) {
                priceHistoryList.add(newHistory);
            }
        }
        priceHistoryList.sort(Comparator.comparing(PriceHistory::getDate));
        p.setPriceHistory(priceHistoryList);
    }

    @Override
    public void delete(Long id) throws NoSuchElementException {
        lock.writeLock().lock();
        try {
            boolean removed = products.removeIf(product -> id.equals(product.getId()));
            if (!removed) {
                throw new NoSuchElementException("Продукт с ID " + id + " не найден.");
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public void updateQuantity(Long id, int quantity) {
        lock.writeLock().lock();
        try {
            getProduct(id).setStock(getProduct(id).getStock() - quantity);
        } finally {
            lock.writeLock().unlock();
        }
    }
}
