package com.helpdesk.db;

/** Unchecked wrapper so callers don't need to handle SQLException everywhere. */
public class DataAccessException extends RuntimeException {
    public DataAccessException(String message, Throwable cause) {
        super(message, cause);
    }
}
