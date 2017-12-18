package com.example.taeksu.chatkut;


import android.app.Application;

import com.sendbird.android.SendBird;

public class BaseApplication extends Application {

    private static final String APP_ID = "615116CC-CC78-4E40-A4D1-688453713410"; // US-1 Demo
    public static final String VERSION = "3.0.38";

    @Override
    public void onCreate() {
        super.onCreate();
        SendBird.init(APP_ID, getApplicationContext());
    }
}
