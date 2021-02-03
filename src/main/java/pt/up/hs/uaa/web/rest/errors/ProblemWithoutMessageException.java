package pt.up.hs.uaa.web.rest.errors;

import org.zalando.problem.AbstractThrowableProblem;
import org.zalando.problem.Status;
import pt.up.hs.uaa.constants.ErrorTypes;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

/**
 * Base exception thrown from problems which do NOT have an associated message.
 */
public class ProblemWithoutMessageException extends AbstractThrowableProblem {
    private static final long serialVersionUID = 1L;

    private final String entityName;

    public ProblemWithoutMessageException(String entityName) {
        this(Status.BAD_REQUEST, entityName);
    }

    public ProblemWithoutMessageException(Status status, String entityName) {
        this(ErrorTypes.DEFAULT_TYPE, status, entityName);
    }

    public ProblemWithoutMessageException(URI type, Status status, String entityName) {
        super(type, null, status, null, null, null, getParameters(entityName));
        this.entityName = entityName;
    }

    public String getEntityName() {
        return entityName;
    }

    private static Map<String, Object> getParameters(String entityName) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("params", entityName);
        return parameters;
    }
}
