package com.sg.hackamu.di;

import android.app.Application;

public class App extends Application {
    private Component component;
    private static App application;

    public static App getApp() {
        return application;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        application=this;
        component=DaggerComponent.builder().applicationContextModule(new ApplicationContextModule(application)).firebaseAuthModule(new FirebaseAuthModule()).build();

    }


    public Component getComponent() {
        return component;
    }
}
