package com.stanleycen.facebookanalytics;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;

public class PreferencesFragment extends PreferenceFragment {

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.preferences);

        Preference logout = (Preference) findPreference("logout");
        logout.setSummary("Logged in as " + GlobalApp.get().fb.me.getName());
        logout.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {

            @Override
            public boolean onPreferenceClick(Preference preference) {
                Activity act = (Activity) getActivity();
                if (act != null) {
                    Intent ret = new Intent();
                    act.setResult(PreferencesActivity.FACEBOOK_LOGOUT, ret);
                    act.finish();
                }
                return true;
            }
        });
    }

}
