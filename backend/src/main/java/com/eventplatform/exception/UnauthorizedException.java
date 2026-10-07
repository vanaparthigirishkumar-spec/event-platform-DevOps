package com.eventplatform.exception;

public class UnauthorizedException extends ApiException {

    public UnauthorizedException(String message) {
        super(401, message);
    }

    public UnauthorizedException() {
        super(401, "Unauthorized");
    }

}