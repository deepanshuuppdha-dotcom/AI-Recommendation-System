package com.recsys.exception;

/** Thrown when an admin submits an invalid recommendation setting. */
public class InvalidSettingException extends ValidationException {
    public InvalidSettingException(String message) { super(message); }
}
