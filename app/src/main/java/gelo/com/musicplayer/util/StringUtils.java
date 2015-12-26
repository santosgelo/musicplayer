package gelo.com.musicplayer.util;

/**
 * Created by samsung on 10/29/2015.
 */
public class StringUtils {
    private static final String EMPTY_STRING = "";

    private StringUtils() {
        //Empty
    }

    public static boolean isNullOrEmpty(final String string) {
        return string == null || EMPTY_STRING.equals(string);
    }
}
