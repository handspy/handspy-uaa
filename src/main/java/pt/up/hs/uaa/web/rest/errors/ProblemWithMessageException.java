package pt.up.hs.uaa.web.rest.errors;

import org.zalando.problem.AbstractThrowableProblem;
import org.zalando.problem.Status;
import pt.up.hs.uaa.constants.ErrorTypes;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

/**
 * Base exception thrown from problems which have an associated message.
 */
public class ProblemWithMessageException extends AbstractThrowableProblem {
    private static final long serialVersionUID = 1L;

    private final String entityName;
    private final String errorKey;

    public ProblemWithMessageException(String defaultMessage, String entityName, String errorKey) {
        this(ErrorTypes.DEFAULT_TYPE, Status.BAD_REQUEST, defaultMessage, entityName, errorKey);
    }

    public ProblemWithMessageException(Status status, String defaultMessage, String entityName, String errorKey) {
        this(ErrorTypes.DEFAULT_TYPE, status, defaultMessage, entityName, errorKey);
    }

    public ProblemWithMessageException(URI type, Status status, String defaultMessage, String entityName, String errorKey) {
        super(type, defaultMessage, status, null, null, null, getParameters(entityName, errorKey));
        this.entityName = entityName;
        this.errorKey = errorKey;
    }

    public String getEntityName() {
        return entityName;
    }

    public String getErrorKey() {
        return errorKey;
    }

    private static Map<String, Object> getParameters(String entityName, String errorKey) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("message", errorKey);
        parameters.put("params", entityName);
        return parameters;
    }
}
