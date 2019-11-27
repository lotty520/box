package com.github.box.factory;

import com.github.box.StringCipher;
import com.github.box.impl.AesStringCipher;
import com.github.box.impl.Base64StringCipher;
import com.github.box.impl.HexStringCipher;
import com.github.box.impl.XorStringCipher;

public class StringCipherFactory {
    private final static String TYPE_BASE64 = "base64";
    private final static String TYPE_XOR = "xor";
    private final static String TYPE_HEX = "hex";
    private final static String TYPE_AES = "aes";


    public static StringCipher create(String type) {
        if (TYPE_BASE64.equals(type)) {
            return new Base64StringCipher();
        } else if (TYPE_XOR.equals(type)) {
            return new XorStringCipher();
        } else if (TYPE_HEX.equals(type)) {
            return new HexStringCipher();
        } else if (TYPE_AES.equals(type)) {
            return new AesStringCipher();
        }
        return new Base64StringCipher();
    }

}
