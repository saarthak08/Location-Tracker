package com.sg.hackamu.di;


import android.app.Application;
import android.content.Context;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Singleton
@Module
public class ApplicationContextModule {

    private Application application;

    public ApplicationContextModule(Application application){
        this.application=application;
    }

    @Provides
    Context getApplicationContext(){
        return application;
    }
}
