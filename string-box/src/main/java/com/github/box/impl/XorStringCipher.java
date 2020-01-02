package com.github.box.impl;

import com.github.box.StringCipher;
import com.github.box.util.CipherUtil;
import java.util.Base64;

/**
 * @author lotty
 */
public class XorStringCipher implements StringCipher {

  @Override
  public String dd(String value, String k, String iv) {
    byte[] key = k.getBytes();
    byte[] data = CipherUtil.callAndroidBase64Decode(value);
    if (data != null) {
      byte[] result = new byte[data.length];
      for (int i = 0; i < data.length; i++) {
        result[i] = (byte) (data[i] ^ key[i % key.length]);
      }
      return new String(result);
    }
    return "";
  }

  @Override
  public String ee(String value, String k, String iv) {
    byte[] key = k.getBytes();
    byte[] data = value.getBytes();
    byte[] result = new byte[data.length];
    for (int i = 0; i < data.length; i++) {
      result[i] = (byte) (data[i] ^ key[i % key.length]);
    }
    return new String(Base64.getEncoder().encode(result));
  }
}