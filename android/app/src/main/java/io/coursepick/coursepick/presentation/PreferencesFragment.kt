package io.coursepick.coursepick.presentation

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import io.coursepick.coursepick.R

class PreferencesFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(
        savedInstanceState: Bundle?,
        rootKey: String?,
    ) {
        setPreferencesFromResource(R.xml.preferences, rootKey)
    }
}
