package com.example.secondwork.util;

import android.app.Application;
import android.content.Context;

public class MyApplication extends Application {
    public static Context getContext() {
        return context;
    }

    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
    }

}
