package com.github.box.impl;

import com.github.box.StringCipher;

public class HexStringCipher implements StringCipher {
    @Override
    public String dd(String value) {
        return value + ".hex";
    }

    @Override
    public String ee(String value) {
        return value.replace(".hex", "");
    }
}
