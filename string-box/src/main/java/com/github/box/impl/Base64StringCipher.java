package com.github.box.impl;

import com.github.box.StringCipher;

/**
 * @author lotty
 */
public class Base64StringCipher implements StringCipher {

  @Override
  public String dd(String value) {
    return new String(android.util.Base64.decode(value, android.util.Base64.NO_WRAP));
  }

  @Override
  public String ee(String value) {
    return new String(android.util.Base64.encode(value.getBytes(), android.util.Base64.NO_WRAP));
  }
}
