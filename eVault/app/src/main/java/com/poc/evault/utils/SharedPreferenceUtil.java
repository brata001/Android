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
    private static final String EMAIL="email";
    private static final String PASSWORD="password";

    static{
        sharedPreference = EVaultApplication.getAppContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }


    public static String getEmail() {
        return sharedPreference.getString(EMAIL,"");
    }

    public static void setEmail(String email) {
        SharedPreferences.Editor editor = sharedPreference.edit();
        editor.putString(EMAIL, email);
        editor.commit();
    }

    public static String getPassword() {
        return sharedPreference.getString(PASSWORD,"");
    }

    public static void setPassword(String password) {
        SharedPreferences.Editor editor = sharedPreference.edit();
        editor.putString(PASSWORD, password);
        editor.commit();
    }
}
