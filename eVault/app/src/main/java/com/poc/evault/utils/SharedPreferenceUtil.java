package com.poc.evault.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.poc.evault.application.EVaultApplication;

/**
 * Created by DASP2 on 4/2/2017.
 */
public class SharedPreferenceUtil {
    public static final String PREFS_NAME = "AOP_PREFS";
    private static SharedPreferences sharedPreference;
    private static String email;
    private static String password;

    static{
        sharedPreference = EVaultApplication.getAppContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }


    public static String getEmail() {
        return email;
    }

    public static void setEmail(String email) {
        SharedPreferenceUtil.email = email;
    }

    public static String getPassword() {
        return password;
    }

    public static void setPassword(String password) {
        SharedPreferenceUtil.password = password;
    }
}
