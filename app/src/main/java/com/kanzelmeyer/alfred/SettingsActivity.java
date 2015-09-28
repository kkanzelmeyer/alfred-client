package com.kanzelmeyer.alfred;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.util.Log;

import com.kanzelmeyer.alfred.network.NetworkListenerService;

public class SettingsActivity extends PreferenceActivity {

    public static final String KEY_HOST_ADDRESS = "network_host_address";
    public static final String KEY_HOST_PORT = "network_host_port";
    public static final String KEY_SERVICE_RUN = "service_run";
    private static final String TAG = "Settings";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Display the fragment as the main content.
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingsFragment())
                .commit();

    }

    public static class SettingsFragment extends PreferenceFragment
            implements SharedPreferences.OnSharedPreferenceChangeListener {

        private Context mContext;
        private SharedPreferences prefs;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            mContext = getActivity().getApplicationContext();
            prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
            // Load the preferences from an XML resource
            addPreferencesFromResource(R.xml.pref_general);

            Preference networkAddressPref = findPreference(KEY_HOST_ADDRESS);
            Preference networkPortPref = findPreference(KEY_HOST_PORT);
            if(prefs.getBoolean(KEY_SERVICE_RUN, true)) {
                networkAddressPref.setEnabled(false);
                networkPortPref.setEnabled(false);
            } else {
                networkAddressPref.setEnabled(true);
                networkPortPref.setEnabled(true);
            }
        }

        @Override
        public void onResume() {
            super.onResume();
            // Set up a listener whenever a key changes
            prefs.registerOnSharedPreferenceChangeListener(this);
        }

        @Override
        public void onPause() {
            super.onPause();
            // Unregister the listener whenever a key changes
            prefs.unregisterOnSharedPreferenceChangeListener(this);
        }

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            Log.i(TAG, "Preference updated : " + key);
            Preference networkAddressPref = findPreference(KEY_HOST_ADDRESS);
            Preference networkPortPref = findPreference(KEY_HOST_PORT);

            if (key.equals(KEY_SERVICE_RUN)) {
                boolean preferenceRunService = sharedPreferences.getBoolean(KEY_SERVICE_RUN, true);
                Intent serviceIntent;
                serviceIntent = new Intent(mContext, NetworkListenerService.class);
                if(preferenceRunService) {
                    mContext.startService(serviceIntent);
                    // disable network preferences
                    networkAddressPref.setEnabled(false);
                    networkPortPref.setEnabled(false);
                } else {
                    mContext.stopService(serviceIntent);
                    // enable network preferences
                    networkAddressPref.setEnabled(true);
                    networkPortPref.setEnabled(true);
                }
            }
        }
    }
}
