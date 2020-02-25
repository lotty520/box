package com.github.box;

import com.android.ddmlib.Log;
import com.github.box.factory.StringCipherFactory;
import com.github.box.util.CipherUtil;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

/**
 * MethodVisitor的基类
 *
 * @author lotty
 */
public class InjectMethodVisitor extends MethodVisitor {

  private final static String STATIC_BLOCK_METHOD = "<clinit>";

  private final static String STRING_DE_METHOD_BASE64 = "xr";
  private final static String STRING_DE_METHOD_HEX = "rx";
  private final static String STRING_DE_METHOD_AES = "vv";
  private final static String STRING_DE_METHOD_XOR = "vx";
  private final static Map<String, String> METHOD_MAP = new HashMap<>(4);
  private final static Map<String, Integer> KEY_SIZE_MAP = new HashMap<>(4);
  private final static String STRING_ENC_P =
      "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;";
  private static String STRING_ENC_OWNER = "com/github/box/XxVv";

  static {
    METHOD_MAP.put("base64", STRING_DE_METHOD_BASE64);
    METHOD_MAP.put("xor", STRING_DE_METHOD_XOR);
    METHOD_MAP.put("hex", STRING_DE_METHOD_HEX);
    METHOD_MAP.put("aes", STRING_DE_METHOD_AES);

    KEY_SIZE_MAP.put("base64", StringCipherFactory.ZERO_KEY_SIZE);
    KEY_SIZE_MAP.put("xor", StringCipherFactory.XOR_KEY_SIZE);
    KEY_SIZE_MAP.put("hex", StringCipherFactory.ZERO_KEY_SIZE);
    KEY_SIZE_MAP.put("aes", StringCipherFactory.AES_KEY_SIZE);
  }

  private String cls;
  private String method;
  private Map<String, String> pairs;
  private PluginConfig config;

  public InjectMethodVisitor(int api, MethodVisitor mv, String cls, String method,
      PluginConfig config, Map<String, String> pairs) {
    super(api, mv);
    this.cls = cls;
    this.pairs = pairs;
    this.method = method;
    this.config = config;
    if (config != null && config.pkg != null && config.pkg.trim() != "") {
      STRING_ENC_OWNER = config.pkg.replace(".","/");
    }
  }

  @Override
  public void visitCode() {
    // 在静态代码块中初始化变量
    mv.visitCode();
    if (STATIC_BLOCK_METHOD.equals(method)) {
      Set<String> strings = pairs.keySet();
      Iterator<String> iterator = strings.iterator();
      while (iterator.hasNext()) {
        String key = iterator.next();
        String value = pairs.get(key);
        int length = randomLength(config.encType);
        String k = CipherUtil.randomString(length);
        String iv = CipherUtil.randomString(length);

        String encryption = cipher(config.encType).ee(value, k, iv);
        Log.e("plugin", cls + " || " + value + "--->" + encryption);
        mv.visitLdcInsn(encryption);
        mv.visitLdcInsn(k);
        mv.visitLdcInsn(iv);
        mv.visitMethodInsn(Opcodes.INVOKESTATIC, STRING_ENC_OWNER, methodString(config.encType),
            STRING_ENC_P, false);
        mv.visitFieldInsn(Opcodes.PUTSTATIC, cls, key, "Ljava/lang/String;");
      }
    }
  }

  @Override
  public void visitLdcInsn(Object cst) {
    if (cst instanceof String) {
      // 字符串加密核心逻辑
      int length = randomLength(config.encType);
      String k = CipherUtil.randomString(length);
      String iv = CipherUtil.randomString(length);
      String encryption = cipher(config.encType).ee((String) cst, k, iv);
      Log.e("plugin", cls + " || " + cst + "--->" + encryption);
      mv.visitLdcInsn(encryption);
      mv.visitLdcInsn(k);
      mv.visitLdcInsn(iv);
      mv.visitMethodInsn(Opcodes.INVOKESTATIC, STRING_ENC_OWNER, methodString(config.encType),
          STRING_ENC_P, false);
    } else {
      mv.visitLdcInsn(cst);
    }
  }

  private StringCipher cipher(String cipher) {
    return StringCipherFactory.create(cipher);
  }

  private int randomLength(String type) {
    if (KEY_SIZE_MAP.containsKey(type)) {
      return KEY_SIZE_MAP.get(type);
    } else {
      return StringCipherFactory.ZERO_KEY_SIZE;
    }
  }

  private String methodString(String type) {
    if (METHOD_MAP.containsKey(type)) {
      return METHOD_MAP.get(type);
    } else {
      return STRING_DE_METHOD_BASE64;
    }
  }
}