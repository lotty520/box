package com.github.box.impl;

import com.github.box.StringCipher;

public class XorStringCipher implements StringCipher {
    @Override
    public String dd(String value) {
        return value + ".xor";
    }

    @Override
    public String ee(String value) {
        return value.replace(".xor", "");
    }
}
