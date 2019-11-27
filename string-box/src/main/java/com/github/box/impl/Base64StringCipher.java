package com.github.box.impl;

import com.github.box.StringCipher;

import java.util.Base64;


public class Base64StringCipher implements StringCipher {

    @Override
    public String dd(String value) {
        byte[] decode = Base64.getDecoder().decode(value);
        return new String(decode);
    }

    @Override
    public String ee(String value) {
        byte[] encode = Base64.getEncoder().encode(value.getBytes());
        return new String(encode);
    }
}
