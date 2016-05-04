package com.fitaleks.walkwithme;

import android.app.Application;

import com.firebase.client.Firebase;

/**
 * Created by alexanderkulikovskiy on 03.05.16.
 */
public class WalkWithMeApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Firebase.setAndroidContext(this);
    }
}
