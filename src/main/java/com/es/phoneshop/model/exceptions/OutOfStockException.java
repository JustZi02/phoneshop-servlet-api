package com.es.phoneshop.model.exceptions;

public class OutOfStockException extends RuntimeException {
    String message;
    int requestedQuantity;
    int availableQuantity;

    public OutOfStockException(String message, int requestedQuantity, int availableQuantity) {
        this.message = message;
        this.requestedQuantity = requestedQuantity;
        this.availableQuantity = availableQuantity;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public int getRequestedQuantity() {
        return requestedQuantity;
    }

    public int getAvailableQuantity() {
        return availableQuantity;
    }
}
