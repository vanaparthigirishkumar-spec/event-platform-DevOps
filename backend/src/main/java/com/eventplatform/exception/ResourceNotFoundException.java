package com.eventplatform.exception;

public class ResourceNotFoundException extends ApiException {

    public ResourceNotFoundException(String resourceName, String fieldName, Object fieldValue) {
        super(404, String.format("%s not found with %s: '%s'", resourceName, fieldName, fieldValue));
    }

    public ResourceNotFoundException(String message) {
        super(404, message);
    }

}