package pt.up.hs.uaa.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Properties specific to Uaa.
 * <p>
 * Properties are configured in the {@code application.yml} file.
 * See {@link io.github.jhipster.config.JHipsterProperties} for a good example.
 */
@ConfigurationProperties(prefix = "application", ignoreUnknownFields = false)
public class ApplicationProperties {

    private final OAuth2 oauth2 = new OAuth2();

    public static final class OAuth2 {
        private String authorizedRedirectUri = "";

        public String getAuthorizedRedirectUri() {
            return authorizedRedirectUri;
        }

        public OAuth2 setAuthorizedRedirectUri(String authorizedRedirectUri) {
            this.authorizedRedirectUri = authorizedRedirectUri;
            return this;
        }
    }

    public OAuth2 getOAuth2() {
        return oauth2;
    }
}
