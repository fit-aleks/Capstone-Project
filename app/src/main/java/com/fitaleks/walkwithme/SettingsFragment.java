package com.fitaleks.walkwithme;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;

import com.fitaleks.walkwithme.data.firebase.FirebaseHelper;

/**
 * Created by alexanderkulikovskiy on 03.05.16.
 */
public class SettingsFragment extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener,
    Preference.OnPreferenceChangeListener {


    @Override
    public void onCreatePreferences(Bundle bundle, String s) {
        addPreferencesFromResource(R.xml.pref_general);

        bindPreference(findPreference(getString(R.string.pref_enable_sharing_key)));
        bindPreference(findPreference(getString(R.string.pref_enable_sharing_location_key)));
        bindPreference(findPreference(getString(R.string.pref_enable_tracking_location_key)));
    }

    private void bindPreference(Preference preference) {
        preference.setOnPreferenceChangeListener(this);
    }

    @Override
    public void onResume() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getContext());
        sp.registerOnSharedPreferenceChangeListener(this);
        super.onResume();
    }

    @Override
    public void onPause() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getContext());
        sp.unregisterOnSharedPreferenceChangeListener(this);
        super.onPause();
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object o) {
        preference.setSummary(o.toString());
        return true;
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(getString(R.string.pref_enable_sharing_key))) {
            // check and remove data from firebase or send it
            if (!sharedPreferences.getBoolean(key, false)) {
                final String uid = SharedPrefUtils.getUserUid(getContext());
                FirebaseHelper helper = new FirebaseHelper.Builder()
                        .addChild("users")
                        .addChild(uid)
                        .build();
                helper.getFirebase().setValue(null);
                helper = new FirebaseHelper.Builder()
                        .addChild("steps")
                        .addChild(uid)
                        .build();
                helper.getFirebase().setValue(null);
            }
        }
    }
}
