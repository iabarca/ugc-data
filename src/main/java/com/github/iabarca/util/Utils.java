
package com.github.iabarca.util;

import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormat;

import java.util.Locale;

public class Utils {

    public static LocalDateTime parseDate(String date, String pattern) {
        return LocalDateTime.parse(date,
                DateTimeFormat.forPattern(pattern).withLocale(Locale.ENGLISH));
    }

    public static String toLowerCamelCase(String s) {
        s = toCamelCase(s);
        return s.substring(0, 1).toLowerCase() + s.substring(1);
    }

    public static String toCamelCase(String s) {
        String[] parts = s.split("_| ");
        String camelCaseString = "";
        for (String part : parts) {
            camelCaseString = camelCaseString + toProperCase(part);
        }
        return camelCaseString;
    }

    public static String toProperCase(String s) {
        return s.substring(0, 1).toUpperCase() + s.substring(1).toLowerCase();
    }

    public static String now(String format) {
        return LocalDateTime.now().toString(DateTimeFormat.forPattern(format));
    }

    private Utils() {

    }

}
