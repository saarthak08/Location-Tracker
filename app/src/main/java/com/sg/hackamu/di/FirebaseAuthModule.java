package com.sg.hackamu.di;

import com.google.firebase.auth.FirebaseAuth;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Singleton
@Module
public class FirebaseAuthModule {

    private FirebaseAuth firebaseAuth=FirebaseAuth.getInstance();

    @Provides
    public FirebaseAuth getFirebaseAuth(){
        return firebaseAuth;
    }
}
