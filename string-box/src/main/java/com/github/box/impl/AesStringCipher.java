package com.github.box.impl;

import com.github.box.StringCipher;

public class AesStringCipher implements StringCipher {
    @Override
    public String dd(String value) {
        return value + ".aes";
    }

    @Override
    public String ee(String value) {
        return value.replace(".aes", "");
    }
}
