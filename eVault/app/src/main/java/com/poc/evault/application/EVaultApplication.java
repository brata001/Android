package com.poc.evault.application;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDexApplication;

/**
 * Created by DASP2 on 4/2/2017.
 */
public class EVaultApplication extends MultiDexApplication {
    private static Context sAppContext;

    @Override
    public void onCreate() {
        super.onCreate();
        sAppContext=this;
    }


    public static Context getAppContext(){
        return sAppContext;
    }
}
