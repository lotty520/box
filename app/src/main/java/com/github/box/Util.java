package com.github.box;

import android.util.Log;


public class Util {
    private static final String S1 = "this is static final const variable";

    private static String S2 = "this is static const variable";

    static {
        Log.e("wh", "this is static block");
    }

    private final String S3 = "this is final const variable";
    private String S4 = "this is normal variable";

    {
        Log.e("wh", "normal block string");
    }

    public void print() {
        Log.e("wh", "S1=" + S1);
        Log.e("wh", "S2=" + S2);
        Log.e("wh", "S3=" + S3);
        Log.e("wh", "S4=" + S4);
    }

}
