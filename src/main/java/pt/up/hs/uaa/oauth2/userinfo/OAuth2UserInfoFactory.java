package pt.up.hs.uaa.oauth2.userinfo;

import pt.up.hs.uaa.oauth2.OAuth2Provider;
import pt.up.hs.uaa.web.rest.errors.OAuth2AuthenticationException;

import java.util.Map;

public class OAuth2UserInfoFactory {

    public static OAuth2UserInfo getOAuth2UserInfo(String registrationId, Map<String, Object> attributes) {
        if (registrationId.equalsIgnoreCase(OAuth2Provider.GOOGLE.toString())) {
            return new GoogleOAuth2UserInfo(attributes);
        } else if (registrationId.equalsIgnoreCase(OAuth2Provider.FACEBOOK.toString())) {
            return new FacebookOAuth2UserInfo(attributes);
        } else if (registrationId.equalsIgnoreCase(OAuth2Provider.TWITTER.toString())) {
            return new TwitterOAuth2UserInfo(attributes);
        } else {
            throw new OAuth2AuthenticationException(
                "Login with " + registrationId + " is not supported.",
                "oauth2methodnotsupported"
            );
        }
    }
}
