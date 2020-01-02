package com.github.box;

/**
 * @author lotty
 */
public interface StringCipher {

  /**
   * 解密
   *
   * @param value 初始值
   * @param key Key
   * @param iv Iv
   * @return 解密结果
   */
  String dd(String value, String key, String iv);

  /**
   * 加密
   *
   * @param value 初始值
   * @param key Key
   * @param iv Iv
   * @return 加密结果
   */
  String ee(String value, String key, String iv);
}
