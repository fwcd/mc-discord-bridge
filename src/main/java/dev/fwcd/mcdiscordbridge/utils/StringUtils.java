package dev.fwcd.mcdiscordbridge.utils;

public final class StringUtils {
    private StringUtils() {}
    
    public static String pluralize(String str, int n) {
        return n == 1 ? str : str + "s";
    }
}
