package com.github.box;

import com.github.box.impl.AesStringCipher;
import com.github.box.impl.Base64StringCipher;
import com.github.box.impl.HexStringCipher;

public class XxVv {

    /**
     * base64
     *
     * @param v 密文
     * @return 明文
     */
    public static String xr(String v) {
        return new Base64StringCipher().dd(v);
    }

    /**
     * hex
     *
     * @param v 密文
     * @return 明文
     */
    public static String rx(String v) {
        return new HexStringCipher().dd(v);
    }

    /**
     * aes
     *
     * @param v 密文
     * @return 明文
     */
    public static String vv(String v) {
        return new AesStringCipher().dd(v);
    }

    /**
     * xor
     *
     * @param v 密文
     * @return 明文
     */
    public static String vx(String v) {
        return new HexStringCipher().dd(v);
    }
}
