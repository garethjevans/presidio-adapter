package com.garethjevans.ai.presidio;

public class BannedElementException extends RuntimeException {
    public BannedElementException(String message) {
        super(message);
    }
}
