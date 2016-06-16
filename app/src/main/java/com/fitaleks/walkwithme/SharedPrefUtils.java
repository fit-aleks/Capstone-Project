package com.fitaleks.walkwithme;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;

/**
 * Created by alexanderkulikovskiy on 30.03.16.
 */
public class SharedPrefUtils {

    public static void setUserName(@NonNull final Context context, @NonNull final String userName) {
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        prefs.edit()
                .putString(context.getString(R.string.pref_name_key), userName)
                .apply();
    }

    @NonNull
    public static String getUserName(@NonNull final Context context) {
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(context.getString(R.string.pref_name_key), "");
    }

    public static void setUserUid(@NonNull final Context context, @NonNull final String uid) {
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        prefs.edit()
                .putString(context.getString(R.string.pref_uid_key), uid)
                .apply();
    }

    @NonNull
    public static String getUserUid(@NonNull final Context context) {
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(context.getString(R.string.pref_uid_key), "");
    }

    public static void setUserPhoto(@NonNull final Context context, @NonNull final String photoUrl) {
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        prefs.edit()
                .putString(context.getString(R.string.pref_photo_key), photoUrl)
                .apply();
    }

    @NonNull
    public static String getUserPhoto(@NonNull final Context context) {
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(context.getString(R.string.pref_photo_key), "");
    }

    public static boolean isUserSharingInfoEnabled(@NonNull final Context context) {
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getBoolean(context.getString(R.string.pref_enable_sharing_key), false);
    }

    public static boolean isTrackingEnabled(@NonNull final Context context) {
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getBoolean(context.getString(R.string.pref_enable_tracking_location_key), false);
    }

    public static boolean isLocationSharingEnabled(@NonNull final Context context) {
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getBoolean(context.getString(R.string.pref_enable_sharing_location_key), false);
    }
}
