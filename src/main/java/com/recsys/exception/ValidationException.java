package com.recsys.exception;

/** Thrown when user-supplied input fails validation. */
public class ValidationException extends Exception {
    public ValidationException(String message) { super(message); }
}
