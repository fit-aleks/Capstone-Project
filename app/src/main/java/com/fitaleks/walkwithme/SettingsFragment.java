package com.fitaleks.walkwithme;

import android.os.Bundle;
import android.support.v7.preference.PreferenceFragmentCompat;

/**
 * Created by alexanderkulikovskiy on 03.05.16.
 */
public class SettingsFragment extends PreferenceFragmentCompat {


    @Override
    public void onCreatePreferences(Bundle bundle, String s) {
        addPreferencesFromResource(R.xml.pref_general);

        findPreference(getString(R.string.pref_name_key));
    }
}
