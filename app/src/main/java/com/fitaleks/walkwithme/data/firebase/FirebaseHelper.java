package com.fitaleks.walkwithme.data.firebase;

import android.text.TextUtils;

import com.firebase.client.Firebase;
import com.fitaleks.walkwithme.BuildConfig;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by alexanderkulikovskiy on 03.05.16.
 */
public class FirebaseHelper {
    public static final String KEY_USERS = "users";
    public static final String KEY_STEPS = "steps";
    public static final String KEY_LOCATION = "location";


    private Firebase firebase;
    private FirebaseHelper(Builder builder) {
        firebase = new Firebase(BuildConfig.FIREBAE_SERVER_URL);
        for (String pathPart : builder.additionalPath) {
            firebase = firebase.child(pathPart);
        }
    }

    public Firebase getFirebase() {
        return firebase;
    }

    public static class Builder {
        private List<String> additionalPath;

        public Builder() {

            additionalPath = new ArrayList<>();
        }

        public Builder addChild(String path) {
            if (!TextUtils.isEmpty(path)) {
                additionalPath.add(path);
            }
            return this;
        }

        public FirebaseHelper build() {
            return new FirebaseHelper(this);
        }
    }
}
