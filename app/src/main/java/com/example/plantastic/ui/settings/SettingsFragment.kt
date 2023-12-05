package com.example.plantastic.ui.settings

import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Bundle
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.example.plantastic.R
import java.util.Locale

object LocaleHelper {

    fun setLocale(context: Context, language: String): Context {
        val locale = Locale(language)
        Locale.setDefault(locale)

        val resources: Resources = context.resources
        val configuration: Configuration = resources.configuration
        configuration.setLocale(locale)

        return context.createConfigurationContext(configuration)
    }
}
class SettingsFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)

        val languagePreference: ListPreference? = findPreference("reply")
        languagePreference?.setOnPreferenceChangeListener { _, newValue ->
            // Change the language when the preference is changed
            switchLanguage(newValue.toString())
            true
        }
    }

    private fun switchLanguage(language: String) {
        // Get the application context and switch the language
        val context = activity?.applicationContext
        if (context != null) {
            val newContext = LocaleHelper.setLocale(context, language)

            // Update the activity to reflect the language change
            activity?.resources?.updateConfiguration(
                newContext.resources.configuration,
                newContext.resources.displayMetrics
            )

            // Recreate the activity to apply the language change
            activity?.recreate()
        }
    }
}
