/*
 * Copyright (C) 2019 Baidu, Inc. All Rights Reserved.
 */
package com.github.box;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import java.util.HashMap;
import java.util.Map;

/**
 * 全局的ClassVisitor,通过对方法名的判断，加载对应的MethodVisitor
 *
 * @author lotty
 */
public class InjectClassVisitor extends ClassVisitor {

    /**
     * String 格式描述
     */
    private final static String STRING_DESC = "Ljava/lang/String;";
    /**
     * static + final 的16进制严格校验
     */
    private final static int ACCESS = 0x18;

    /**
     * 当前类名
     */
    private String owner;

    private PluginConfig config;

    /**
     * static final member para Key-> name Value->defaultValue
     */
    private Map<String, String> fieldPairs = new HashMap<>();

    public InjectClassVisitor(ClassVisitor cv) {
        super(Opcodes.ASM5, cv);
    }

    public InjectClassVisitor(ClassVisitor cv, PluginConfig config) {
        super(Opcodes.ASM5, cv);
        this.config = config;
    }


    @Override
    public void visit(int version, int access, String name, String signature, String superName,
                      String[] interfaces) {
        super.visit(version, access, name, signature, superName, interfaces);
        owner = name;
    }

    @Override
    public FieldVisitor visitField(int access, String name, String desc, String signature, Object defaultValue) {
        // 被 final + static 修饰,在类加载时被初始化赋值, instanceof covered null
        if (STRING_DESC.equals(desc) && (ACCESS & access) == ACCESS && defaultValue instanceof String) {
            String value = (String) defaultValue;
            if (!"".equals(value)) {
                fieldPairs.put(name, value);
                return super.visitField(access, name, desc, signature, null);
            }
        }
        return super.visitField(access, name, desc, signature, defaultValue);
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature,
                                     String[] exceptions) {
        MethodVisitor mv = cv.visitMethod(access, name, desc, signature, exceptions);
        return new InjectMethodVisitor(Opcodes.ASM5, mv, owner, name, config, fieldPairs);
    }
}
