package com.github.box;

import com.android.ddmlib.Log;
import com.github.box.factory.StringCipherFactory;
import com.github.box.util.CipherUtil;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * MethodVisitor的基类
 *
 * @author lotty
 */
public class InjectMethodVisitor extends MethodVisitor {

    private final static String STATIC_BLOCK_METHOD = "<clinit>";

    private final static String TYPE_BASE64 = "base64";
    private final static String TYPE_HEX = "hex";
    private final static String TYPE_XOR = "xor";
    private final static String TYPE_AES = "aes";

    private final static int ZERO_KEY_SIZE = 0;
    private final static int XOR_KEY_SIZE = 8;
    private final static int AES_KEY_SIZE = 16;

    private final static String STRING_DE_METHOD_BASE64 = "xr";
    private final static String STRING_DE_METHOD_HEX = "rx";
    private final static String STRING_DE_METHOD_AES = "vv";
    private final static String STRING_DE_METHOD_XOR = "vx";

    private final static Map<String, String> METHOD_MAP = new HashMap<>(4);
    private final static Map<String, Integer> KEY_SIZE_MAP = new HashMap<>(4);
    private final static String STRING_ENC_P_3 =
            "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;";

    private final static String STRING_ENC_P_2 =
            "(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;";

    private final static String STRING_ENC_P_1 =
            "(Ljava/lang/String;)Ljava/lang/String;";

    private static String STRING_ENC_OWNER = "com/github/box/XxVv";

    static {
        METHOD_MAP.put(TYPE_BASE64, STRING_DE_METHOD_BASE64);
        METHOD_MAP.put(TYPE_XOR, STRING_DE_METHOD_XOR);
        METHOD_MAP.put(TYPE_HEX, STRING_DE_METHOD_HEX);
        METHOD_MAP.put(TYPE_AES, STRING_DE_METHOD_AES);

        KEY_SIZE_MAP.put(TYPE_BASE64, ZERO_KEY_SIZE);
        KEY_SIZE_MAP.put(TYPE_XOR, XOR_KEY_SIZE);
        KEY_SIZE_MAP.put(TYPE_HEX, ZERO_KEY_SIZE);
        KEY_SIZE_MAP.put(TYPE_AES, AES_KEY_SIZE);
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
    }

    @Override
    public void visitCode() {
        // 在静态代码块中初始化变量
        mv.visitCode();
        if (STATIC_BLOCK_METHOD.equals(method)) {
            Set<String> strings = pairs.keySet();
            Iterator<String> iterator = strings.iterator();
            while (iterator.hasNext()) {
                String k = generateKey();
                String iv = generateIv();
                String field = iterator.next();
                String value = pairs.get(field);
                String encryption = cipher(config.encType).ee(value, k, iv);
                if (config.logOpen) {
                    Log.e("plugin", cls + " || " + value + "--->" + encryption);
                }
                inject(encryption, k, iv);
                mv.visitFieldInsn(Opcodes.PUTSTATIC, cls, field, "Ljava/lang/String;");
            }
        }
    }

    @Override
    public void visitLdcInsn(Object cst) {
        if (cst instanceof String) {
            String k = generateKey();
            String iv = generateIv();
            String encryption = cipher(config.encType).ee((String) cst, k, iv);
            if (config.logOpen) {
                Log.e("plugin", cls + " || " + cst + "--->" + encryption);
            }
            inject(encryption, k, iv);
        } else {
            mv.visitLdcInsn(cst);
        }
    }

    private String generateKey() {
        if (TYPE_XOR.equals(config.encType) || TYPE_AES.equals(config.encType)) {
            int length = confirmLength();
            return CipherUtil.randomString(length);
        }
        return "";
    }

    private String generateIv() {
        if (TYPE_AES.equals(config.encType)) {
            int length = confirmLength();
            return CipherUtil.randomString(length);
        }
        return "";
    }

    private int confirmLength() {
        if (KEY_SIZE_MAP.containsKey(config.encType)) {
            return KEY_SIZE_MAP.get(config.encType);
        } else {
            return ZERO_KEY_SIZE;
        }
    }

    private StringCipher cipher(String cipherType) {
        return StringCipherFactory.create(cipherType);
    }

    private void inject(String encryption, String k, String iv) {
        if (TYPE_XOR.equals(config.encType)) {
            injectWithOnlyKey(encryption, k);
        } else if (TYPE_AES.equals(config.encType)) {
            injectWithIv(encryption, k, iv);
        } else {
            injectWithSelf(encryption);
        }
    }

    private void injectWithIv(String encryption, String k, String iv) {
        mv.visitLdcInsn(encryption);
        mv.visitLdcInsn(k);
        mv.visitLdcInsn(iv);
        mv.visitMethodInsn(Opcodes.INVOKESTATIC, getDecryptClass(), getDecryptMethod(),
                STRING_ENC_P_3, false);
    }

    private void injectWithOnlyKey(String encryption, String k) {
        mv.visitLdcInsn(encryption);
        mv.visitLdcInsn(k);
        mv.visitMethodInsn(Opcodes.INVOKESTATIC, getDecryptClass(), getDecryptMethod(),
                STRING_ENC_P_2, false);
    }

    private void injectWithSelf(String encryption) {
        mv.visitLdcInsn(encryption);
        mv.visitMethodInsn(Opcodes.INVOKESTATIC, getDecryptClass(), getDecryptMethod(),
                STRING_ENC_P_1, false);
    }

    private String getDecryptMethod() {
        if (!TextUtil.isEmpty(config.pkg) && !TextUtil.isEmpty(config.method)) {
            return config.method;
        } else if (METHOD_MAP.containsKey(config.encType)) {
            return METHOD_MAP.get(config.encType);
        } else {
            return STRING_DE_METHOD_BASE64;
        }
    }

    private String getDecryptClass() {
        if (TextUtil.isEmpty(config.pkg) || TextUtil.isEmpty(config.method)) {
            return STRING_ENC_OWNER;
        } else {
            return config.pkg.replace(".","/");
        }
    }

}