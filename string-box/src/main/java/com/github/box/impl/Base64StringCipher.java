package com.github.box.impl;

import com.github.box.StringCipher;
import com.github.box.util.CipherUtil;

/**
 * @author lotty
 */
public class Base64StringCipher implements StringCipher {

  @Override
  public String dd(String value, String k, String i) {
    byte[] bytes = CipherUtil.callAndroidBase64Decode(value);
    if (bytes != null) {
      return new String(bytes);
    }
    return "";
  }

  @Override
  public String ee(String value, String k, String i) {
    return new String(java.util.Base64.getEncoder().encode(value.getBytes()));
  }
}
