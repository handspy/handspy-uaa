package pt.up.hs.uaa.web.rest.vm;

import java.util.Map;

/**
 * View Model object for storing user attributes from a third-party OAuth2
 * provider.
 */
public class OAuth2LoginVM {

    private String providerId;

    private Map<String, Object> userAttributes;

    public String getProviderId() {
        return providerId;
    }

    public void setProviderId(String providerId) {
        this.providerId = providerId;
    }

    public Map<String, Object> getUserAttributes() {
        return userAttributes;
    }

    public void setUserAttributes(Map<String, Object> userAttributes) {
        this.userAttributes = userAttributes;
    }
}
