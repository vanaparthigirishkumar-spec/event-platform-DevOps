package com.eventplatform.exception;

import java.util.Map;

public class ValidationException extends ApiException {

    private final Map<String, String> details;

    public ValidationException(String message, Map<String, String> details) {
        super(400, message);
        this.details = details;
    }

    public Map<String, String> getDetails() {
        return details;
    }

}