package com.github.box;

public class TextUtil {

    private final static String EMPTY = "";

    public static boolean isEmpty(String target) {
        return null == target || EMPTY.equals(target.trim());
    }
}
