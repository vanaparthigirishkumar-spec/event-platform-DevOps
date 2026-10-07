package com.eventplatform.exception;

public class ForbiddenException extends ApiException {

    public ForbiddenException(String message) {
        super(403, message);
    }

    public ForbiddenException() {
        super(403, "Forbidden");
    }

}