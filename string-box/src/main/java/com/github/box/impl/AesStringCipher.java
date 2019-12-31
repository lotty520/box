package com.github.box.impl;

import com.github.box.StringCipher;
import com.github.box.util.CipherUtil;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * @author lotty
 */
public class AesStringCipher implements StringCipher {

  private static final String MODE = "AES/CBC/PKCS5Padding";
  private static final String CIPHER = "AES";
  private final static int KEY_SIZE = 16;

  public String randomKey;
  public String randomIv;

  public AesStringCipher() {
    randomKey = CipherUtil.randomString(KEY_SIZE);
    randomIv = CipherUtil.randomString(KEY_SIZE);
  }

  @Override
  public String dd(String value) {
    SecretKey ks = new SecretKeySpec(randomKey.getBytes(), CIPHER);
    IvParameterSpec iv = new IvParameterSpec(randomIv.getBytes());
    try {
      Cipher cipher = Cipher.getInstance(MODE);
      cipher.init(Cipher.DECRYPT_MODE, ks, iv);
      byte[] bytes = CipherUtil.callAndroidBase64Decode(value);
      if (bytes != null) {
        return new String(cipher.doFinal(bytes));
      }
    } catch (NoSuchAlgorithmException e) {
      e.printStackTrace();
    } catch (NoSuchPaddingException e) {
      e.printStackTrace();
    } catch (BadPaddingException e) {
      e.printStackTrace();
    } catch (IllegalBlockSizeException e) {
      e.printStackTrace();
    } catch (InvalidKeyException e) {
      e.printStackTrace();
    } catch (InvalidAlgorithmParameterException e) {
      e.printStackTrace();
    }
    return "";
  }

  @Override
  public String ee(String value) {
    Key ks = new SecretKeySpec(randomKey.getBytes(), CIPHER);
    IvParameterSpec iv = new IvParameterSpec(randomIv.getBytes());
    try {
      Cipher cipher = Cipher.getInstance(MODE);
      cipher.init(Cipher.ENCRYPT_MODE, ks, iv);
      byte[] b = cipher.doFinal(value.getBytes());
      return new String(Base64.getEncoder().encode(b));
    } catch (NoSuchAlgorithmException e) {
      e.printStackTrace();
    } catch (InvalidKeyException e) {
      e.printStackTrace();
    } catch (InvalidAlgorithmParameterException e) {
      e.printStackTrace();
    } catch (NoSuchPaddingException e) {
      e.printStackTrace();
    } catch (BadPaddingException e) {
      e.printStackTrace();
    } catch (IllegalBlockSizeException e) {
      e.printStackTrace();
    }
    return "";
  }
}
