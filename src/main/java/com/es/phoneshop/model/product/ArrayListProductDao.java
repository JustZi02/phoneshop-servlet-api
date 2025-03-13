package com.es.phoneshop.model.product;

import java.util.*;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

public class ArrayListProductDao implements ProductDao {
    private static ProductDao instance;

    private final ReadWriteLock lock = new ReentrantReadWriteLock();
    private List<Product> products;
    private long maxId;

    public static synchronized ProductDao getInstance() {
        if (instance == null) {
            instance = new ArrayListProductDao();
        }
        return instance;
    }

    private ArrayListProductDao() {
        products = new ArrayList<Product>();
        maxId = 0l;
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
                    .orElse(null);
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
                    .sorted(Comparator.comparingInt((Product product) -> calculateRelevance(product.getDescription(), query))
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

    private int calculateRelevance(String description, String query) {
        if (query == null || description == null) return 0;

        String descLower = description.toLowerCase();
        String queryLower = query.toLowerCase();

        if (descLower.equals(queryLower)) return Integer.MAX_VALUE;
        if (descLower.contains(queryLower)) return 10000;

        String[] queryWords = queryLower.split("\\s+");
        String[] descWords = descLower.split("\\s+");

        long exactWordMatches = Arrays.stream(queryWords).filter(word -> Arrays.asList(descWords).contains(word)).count();

        return (int) exactWordMatches;
    }

    private boolean MatchQueryProducts(Product product, String query) {
        if (query == null) return true;
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
            products.stream().filter(p -> p.getId().equals(product.getId())).findFirst().ifPresentOrElse(p -> {
                p.setCode(product.getCode());
                p.setDescription(product.getDescription());
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

   /* private void saveSampleProducts() {
        Currency usd = Currency.getInstance("USD");
        save(new Product("sgs", "Samsung Galaxy S", new BigDecimal(100), usd, 100, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Samsung/Samsung%20Galaxy%20S.jpg"));
        save(new Product("sgs2", "Samsung Galaxy S II", new BigDecimal(200), usd, 5, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Samsung/Samsung%20Galaxy%20S%20II.jpg"));
        save(new Product("sgs3", "Samsung Galaxy S III", new BigDecimal(300), usd, 0, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Samsung/Samsung%20Galaxy%20S%20III.jpg"));
        save(new Product("iphone", "Apple iPhone", new BigDecimal(200), usd, 10, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Apple/Apple%20iPhone.jpg"));
        save(new Product("iphone6", "Apple iPhone 6", new BigDecimal(1000), usd, 30, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Apple/Apple%20iPhone%206.jpg"));
        save(new Product("htces4g", "HTC EVO Shift 4G", new BigDecimal(320), usd, 3, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/HTC/HTC%20EVO%20Shift%204G.jpg"));
        save(new Product("sec901", "Sony Ericsson C901", new BigDecimal(420), usd, 30, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Sony/Sony%20Ericsson%20C901.jpg"));
        save(new Product("xperiaxz", "Sony Xperia XZ", new BigDecimal(120), usd, 100, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Sony/Sony%20Xperia%20XZ.jpg"));
        save(new Product("nokia3310", "Nokia 3310", new BigDecimal(70), usd, 100, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Nokia/Nokia%203310.jpg"));
        save(new Product("palmp", "Palm Pixi", new BigDecimal(170), usd, 30, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Palm/Palm%20Pixi.jpg"));
        save(new Product("simc56", "Siemens C56", new BigDecimal(70), usd, 20, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Siemens/Siemens%20C56.jpg"));
        save(new Product("simc61", "Siemens C61", new BigDecimal(80), usd, 30, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Siemens/Siemens%20C61.jpg"));
        save(new Product("simsxg75", "Siemens SXG75", new BigDecimal(150), usd, 40, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Siemens/Siemens%20SXG75.jpg"));
    }*/
}