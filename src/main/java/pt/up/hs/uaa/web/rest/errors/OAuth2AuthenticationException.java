package pt.up.hs.uaa.web.rest.errors;

/**
 * Exception thrown when an error with OAuth2 authentication occurs.
 */
public class OAuth2AuthenticationException extends ProblemWithMessageException {

    public OAuth2AuthenticationException(String defaultMessage, String errorKey) {
        super(defaultMessage, "userManagement", errorKey);
    }
}
