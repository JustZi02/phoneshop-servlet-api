package com.es.phoneshop.model.product;

import java.math.BigDecimal;
import java.time.LocalDate;

public class PriceHistory implements Comparable<PriceHistory> {
    LocalDate date;
    BigDecimal price;

    public PriceHistory() {
    }

    public PriceHistory(LocalDate date, BigDecimal price) {
        this.date = date;
        this.price = price;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    @Override
    public int compareTo(PriceHistory o) {
        return this.price.compareTo(o.getPrice());
    }
}
