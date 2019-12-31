package com.github.box.impl;

import com.github.box.StringCipher;
import com.github.box.util.CipherUtil;
import java.util.Base64;

/**
 * @author lotty
 */
public class XorStringCipher implements StringCipher {
  private final static int KEY_SIZE = 8;

  private String randomKey;

  public XorStringCipher() {
    this.randomKey = CipherUtil.randomString(KEY_SIZE);
  }

  @Override
  public String dd(String value) {
    byte[] key = randomKey.getBytes();
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
  public String ee(String value) {
    byte[] key = randomKey.getBytes();
    byte[] data = value.getBytes();
    byte[] result = new byte[data.length];
    for (int i = 0; i < data.length; i++) {
      result[i] = (byte) (data[i] ^ key[i % key.length]);
    }
    return new String(Base64.getEncoder().encode(result));
  }
}