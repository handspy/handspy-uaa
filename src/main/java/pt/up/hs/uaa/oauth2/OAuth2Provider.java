package pt.up.hs.uaa.oauth2;

/**
 * OAuth2 Providers
 */
public enum OAuth2Provider {
    GOOGLE,
    FACEBOOK,
    TWITTER,
    HANDSPY;

    public static OAuth2Provider fromString(String provider) {
        try {
            return OAuth2Provider.valueOf(provider.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
