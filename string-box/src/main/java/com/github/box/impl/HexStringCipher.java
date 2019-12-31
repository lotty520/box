package com.github.box.impl;

import com.github.box.StringCipher;

/**
 * @author lotty
 */
public class HexStringCipher implements StringCipher {
  private final static String ZERO_STRING = "0";
  private final static String HEX_PREFIX = "0x";

  @Override
  public String dd(String value) {
    int byteLen = value.length() / 2;
    byte[] ret = new byte[byteLen];
    for (int i = 0; i < byteLen; i++) {
      Integer intVal = Integer.decode(HEX_PREFIX + value.substring(i * 2, i * 2 + 2));
      ret[i] = intVal.byteValue();
    }
    return new String(ret);
  }

  @Override
  public String ee(String value) {
    byte[] bytes = value.getBytes();
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < bytes.length; i++) {
      String hex = Integer.toHexString(bytes[i] & 0xFF);
      if (hex.length() == 1) {
        sb.append(ZERO_STRING);
      }
      sb.append(hex);
    }
    return sb.toString();
  }
}
