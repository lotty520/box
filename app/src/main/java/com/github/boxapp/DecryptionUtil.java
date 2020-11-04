package com.github.boxapp;

import android.util.Base64;

public class DecryptionUtil {
    public static String decode(String encryption) {
        return new String(Base64.decode(encryption, Base64.NO_WRAP));
    }
}
