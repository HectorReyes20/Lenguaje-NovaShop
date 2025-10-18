package com.example.novashop.exception;

public class InsufficientStockException extends RuntimeException {
    public InsufficientStockException(String message) {
        super(message);
    }

    public InsufficientStockException(String producto, int disponible) {
        super(String.format("Stock insuficiente para %s. Disponible: %d", producto, disponible));
    }
}
