package com.mathias.inventrix.exceptions;

public class StockNotAvailableException extends RuntimeException {
    public StockNotAvailableException(String message) {
        super(message);
    }

}
