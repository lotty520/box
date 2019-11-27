package com.github.box.util;

import com.github.box.StringCipher;

public class Xx implements StringCipher {
    @Override
    public String dd(String value) {
        return value.substring(0, value.length() - 3);
    }

    @Override
    public String ee(String value) {
        return value + "eee";
    }
}
