package pt.up.hs.uaa.oauth2.userinfo;

import java.util.Map;

/**
 * Holds user information from Twitter OAuth2 provider.
 */
public class TwitterOAuth2UserInfo extends OAuth2UserInfo {

    public TwitterOAuth2UserInfo(Map<String, Object> attributes) {
        super(attributes);
    }

    @Override
    public String getId() {
        return (String) attributes.get("id");
    }

    @Override
    public String getUsername() {
        return (String) attributes.get("screen_name");
    }

    @Override
    public String getName() {
        return (String) attributes.get("name");
    }

    @Override
    public String getFirstName() {
        String name = getName();
        if (name != null && name.indexOf(' ') > -1) {
            return name.substring(0, name.indexOf(' '));
        }
        return name;
    }

    @Override
    public String getLastName() {
        String name = getName();
        if (name != null && name.indexOf(' ') > -1) {
            return name.substring(name.indexOf(' ') + 1);
        }
        return name;
    }

    @Override
    public String getEmail() {
        return (String) attributes.get("email");
    }

    @Override
    public String getImageUrl() {
        return (String) attributes.get("profile_image_url_https");
    }
}
