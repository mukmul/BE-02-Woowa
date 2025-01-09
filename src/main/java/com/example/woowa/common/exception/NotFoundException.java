package com.example.woowa.common.exception;

public class NotFoundException extends RuntimeException {

    private final String errorCode;
    private final String resourceName;

    private final Object resourceId;

    public NotFoundException(String message) {
        super(message);
        this.errorCode = null;
        this.resourceName = null;
        this.resourceId = null;
    }

    public NotFoundException(ErrorMessage errorMessage) {
        super(errorMessage.getMessage());
        this.errorCode = errorMessage.name();
        this.resourceName = null;
        this.resourceId = null;
    }

    public NotFoundException(String resourceName, Object resourceId) {
        super(String.format("Resource '%s' with id '%s' was not found.", resourceName, resourceId));
        this.errorCode = "NOT_FOUND";
        this.resourceName = resourceName;
        this.resourceId = resourceId;
    }

    public NotFoundException(String errorCode, String resourceName, Object resourceId) {
        super(String.format("[%s] - Resource '%s' with id '%s' was not found.", errorCode, resourceName, resourceId));
        this.errorCode = errorCode;
        this.resourceName = resourceName;
        this.resourceId = resourceId;
    }
}
