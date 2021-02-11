package pt.up.hs.uaa.config;

import pt.up.hs.uaa.domain.LengthUnit;
import pt.up.hs.uaa.domain.TimeUnit;

/**
 * Application constants.
 */
public final class Constants {

    // Regex for acceptable logins
    public static final String LOGIN_REGEX = "^[_.@A-Za-z0-9-]*$";

    // Regex for acceptable passwords
    public static final String FULL_PASSWORD_REGEX = "^(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9])(?=.*[@$!%*?#\\()/&.,_-])[A-Za-z0-9@$!%*?#\\()/&.,_-]+$";

    public static final String INTERNAL_CLIENT_ID = "internal";
    public static final String SYSTEM_ACCOUNT = "system";
    public static final String DEFAULT_LANGUAGE = "en";
    public static final LengthUnit DEFAULT_LENGTH_UNIT = LengthUnit.MM;
    public static final TimeUnit DEFAULT_TIME_UNIT = TimeUnit.MS;
    public static final String ANONYMOUS_USER = "anonymoususer";

    private Constants() {
    }
}
