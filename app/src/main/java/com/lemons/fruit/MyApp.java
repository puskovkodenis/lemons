package com.lemons.fruit;

import android.app.Application;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;

public class MyApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        FacebookSdk.fullyInitialize();
        AppEventsLogger.activateApp(this);
    }

}
