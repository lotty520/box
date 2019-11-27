package com.github.box;

/**
 * @author lotty
 */
public interface StringCipher {

    /**
     * 解密
     *
     * @param value 初始值
     * @return 解密结果
     */
    String dd(String value);

    /**
     * 加密
     *
     * @param value 初始值
     * @return 加密结果
     */
    String ee(String value);

}
