package com.github.box.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author lotty
 */
public class CipherUtil {

  private final static char[] CHARS = new char[] {
      'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S',
      'T', 'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l',
      'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '*', '&', '$', '#', '@',
      '!', '+', '-'
  };

  private CipherUtil() {
  }

  public static byte[] callAndroidBase64Decode(String value) {
    try {
      Class cls = Class.forName("android.util.Base64");
      Method method = cls.getDeclaredMethod("decode", String.class, int.class);
      return (byte[]) method.invoke(null, value, 2);
    } catch (ClassNotFoundException e) {
      e.printStackTrace();
    } catch (NoSuchMethodException e) {
      e.printStackTrace();
    } catch (IllegalAccessException e) {
      e.printStackTrace();
    } catch (InvocationTargetException e) {
      e.printStackTrace();
    }
    return null;
  }

  /**
   * 获取随机字符序列
   *
   * @param length 获取的长度
   * @return 结果
   */
  public static String randomString(int length) {
    StringBuilder builder = new StringBuilder();
    for (int i = 0; i < length; i++) {
      double random = Math.random();
      int index = (int) (random * CHARS.length);
      builder.append(CHARS[index]);
    }
    return builder.toString();
  }
}
