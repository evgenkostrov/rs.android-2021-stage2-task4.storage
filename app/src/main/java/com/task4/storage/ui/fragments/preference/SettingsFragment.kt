package com.task4.storage.ui.fragments.preference

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import com.task4.storage.R

class SettingsFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)
    }
}