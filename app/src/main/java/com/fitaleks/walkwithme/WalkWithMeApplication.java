package com.fitaleks.walkwithme;

import android.app.Application;
import android.content.Context;

import com.firebase.client.Firebase;

/**
 * Created by alexanderkulikovskiy on 03.05.16.
 */
public class WalkWithMeApplication extends Application {
    private static WalkWithMeApplication instance;
    @Override
    public void onCreate() {
        super.onCreate();
        Firebase.setAndroidContext(this);
        instance = this;
    }

    public static WalkWithMeApplication getApplication() {
        return instance;
    }
}
