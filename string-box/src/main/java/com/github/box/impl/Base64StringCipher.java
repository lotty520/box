package com.github.box.impl;

import com.github.box.StringCipher;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Base64;

/**
 * @author lotty
 */
public class Base64StringCipher implements StringCipher {

  private Method method;

  @Override
  public String dd(String value) {
    if (method == null) {
      try {
        Class cls = Class.forName("android.util.Base64");
        method = cls.getDeclaredMethod("decode", String.class, Integer.class);
      } catch (ClassNotFoundException e) {
        e.printStackTrace();
      } catch (NoSuchMethodException e) {
        e.printStackTrace();
      }
    }
    try {
      return new String((byte[]) method.invoke(null, value, 2));
    } catch (IllegalAccessException e) {
      e.printStackTrace();
    } catch (InvocationTargetException e) {
      e.printStackTrace();
    }
    return "";
  }

  @Override
  public String ee(String value) {
    byte[] encode = Base64.getEncoder().encode(value.getBytes());
    return new String(encode);
  }
}
