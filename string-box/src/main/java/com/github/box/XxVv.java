package com.github.box;

import com.github.box.impl.AesStringCipher;
import com.github.box.impl.Base64StringCipher;
import com.github.box.impl.HexStringCipher;
import com.github.box.impl.XorStringCipher;

public class XxVv {

  /**
   * base64
   *
   * @param v 密文
   * @return 明文
   */
  public static String xr(String v) {
    return new Base64StringCipher().dd(v, null, null);
  }

  /**
   * hex
   *
   * @param v 密文
   * @return 明文
   */
  public static String rx(String v) {
    return new HexStringCipher().dd(v, null, null);
  }

  /**
   * aes
   *
   * @param v 密文
   * @param k Key
   * @param i Iv
   * @return 明文
   */
  public static String vv(String v, String k, String i) {
    return new AesStringCipher().dd(v, k, i);
  }

  /**
   * xor
   *
   * @param v 密文
   * @param k Key
   * @return 明文
   */
  public static String vx(String v, String k) {
    return new XorStringCipher().dd(v, k, null);
  }
}
