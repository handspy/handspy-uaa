package pt.up.hs.uaa.util;

/**
 * Utilities to deal with strings.
 */
public class StringUtils {

    public static boolean isBlank(String str) {
        return str == null || str.trim().isEmpty();
    }

    public static boolean isNotBlank(String str) {
        return str != null && !str.trim().isEmpty();
    }
}
