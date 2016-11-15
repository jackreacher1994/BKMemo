package com.jackreacher.bkmemo;

import android.Manifest;
import android.app.Activity;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.RingtoneManager;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.Toast;

public class SettingsActivity extends PreferenceActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);

        LinearLayout root = (LinearLayout) findViewById(android.R.id.list).getParent().getParent().getParent();
        Toolbar toolbar = (Toolbar) LayoutInflater.from(this).inflate(R.layout.settings_toolbar, root, false);
        toolbar.setTitle(R.string.action_setting);
        root.addView(toolbar, 0); // insert at top

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        prefs.registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
        switch (key) {
            case "vibration_setting":
                //App.dispatch(new Action<>(Actions.Settings.SET_VIBRATE, prefs.getBoolean(key, false)));
                break;
            case "ringtone_setting":
                String s = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM).toString();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                        && prefs.getString(key, s).startsWith("content://media/external/")
                        && checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                    requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 0);
                }
                Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
                //App.dispatch(new Action<>(Actions.Settings.SET_RINGTONE, prefs.getString(key, s)));
                break;
        }
    }
}
