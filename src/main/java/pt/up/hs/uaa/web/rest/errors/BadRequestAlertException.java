package pt.up.hs.uaa.web.rest.errors;

import org.zalando.problem.Status;

import java.net.URI;

public class BadRequestAlertException extends ProblemWithMessageException {
    private static final long serialVersionUID = 1L;

    public BadRequestAlertException(String defaultMessage, String entityName, String errorKey) {
        super(defaultMessage, entityName, errorKey);
    }

    public BadRequestAlertException(URI type, String defaultMessage, String entityName, String errorKey) {
        super(type, Status.BAD_REQUEST, defaultMessage, entityName, errorKey);
    }
}
