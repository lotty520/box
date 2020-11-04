package com.github.box;


public class PluginConfig {

    /**
     * 包含的路径
     */
    public String[] include;

    /**
     * 选择的加解密类型
     */
    public String encType;

    /**
     * 自定义解密的包名
     */
    public String pkg;

    /**
     * 自定义解密的方法名
     */
    public String method;

    /**
     * 是否打印日志
     */
    public boolean logOpen;

}
