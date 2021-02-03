package pt.up.hs.uaa.service.exceptions;

import org.zalando.problem.Status;

/**
 * Exception thrown by service.
 */
public class ServiceException extends RuntimeException {

    private final String entityName;
    private final String errorKey;
    private final Status errorStatus;

    public ServiceException() {
        entityName = null;
        errorKey = null;
        errorStatus = Status.INTERNAL_SERVER_ERROR;
    }

    public ServiceException(String entityName, String errorKey, String message) {
        super(message);
        this.entityName = entityName;
        this.errorKey = errorKey;
        errorStatus = Status.INTERNAL_SERVER_ERROR;
    }

    public ServiceException(Status errorStatus, String entityName, String errorKey, String message) {
        super(message);
        this.entityName = entityName;
        this.errorKey = errorKey;
        this.errorStatus = errorStatus;
    }

    public String getEntityName() {
        return entityName;
    }

    public Status getErrorStatus() {
        return errorStatus;
    }

    public String getErrorKey() {
        return errorKey;
    }
}
