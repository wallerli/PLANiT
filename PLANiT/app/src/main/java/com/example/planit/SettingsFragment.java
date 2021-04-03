package com.example.planit;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;
import androidx.preference.PreferenceScreen;
import androidx.preference.SwitchPreference;
import androidx.preference.SwitchPreferenceCompat;

public class SettingsFragment extends PreferenceFragmentCompat {

    SharedPreferences shared;
    SwitchPreferenceCompat dueDateAlert, concentrateAlert;
    Preference manageTag, feedback, faq;
    PreferenceCategory developer;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.settings_preferences, rootKey);
        shared = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
        dueDateAlert = findPreference("dueDateAlert");
        concentrateAlert = findPreference("concentrateAlert");
        manageTag = findPreference("manageTag");
        feedback = findPreference("emailDeveloper");
        faq = findPreference("FAQ");
        developer = findPreference("developer");

        dueDateAlert.setChecked(false);
        dueDateAlert.setEnabled(false);
        concentrateAlert.setChecked(false);
        concentrateAlert.setEnabled(false);
        manageTag.setEnabled(false);
        feedback.setEnabled(false);
        faq.setEnabled(false);
        developer.setVisible(shared.getBoolean("developer", false));
    }
}