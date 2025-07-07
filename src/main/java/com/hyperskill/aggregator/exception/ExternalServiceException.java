package com.hyperskill.aggregator.exception;

import org.springframework.http.HttpStatusCode;

public class ExternalServiceException extends RuntimeException {
    public ExternalServiceException(HttpStatusCode errorCode) {
        super("Server error: " + errorCode);
    }
}
