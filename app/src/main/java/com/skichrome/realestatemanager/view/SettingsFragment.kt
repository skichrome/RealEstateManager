package com.skichrome.realestatemanager.view

import android.content.SharedPreferences
import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import com.skichrome.realestatemanager.R
import com.skichrome.realestatemanager.model.database.Agent
import com.skichrome.realestatemanager.model.database.RealEstateDatabase
import com.skichrome.realestatemanager.utils.backgroundJob
import com.skichrome.realestatemanager.utils.backgroundTask
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job

class SettingsFragment : PreferenceFragmentCompat(), SharedPreferences.OnSharedPreferenceChangeListener
{
    // =================================
    //              Fields
    // =================================

    private val settingsFragmentJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + settingsFragmentJob)

    // =================================
    //        Superclass Methods
    // =================================

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) =
        setPreferencesFromResource(R.xml.preferences_screen, rootKey)

    override fun onSharedPreferenceChanged(pref: SharedPreferences?, key: String?)
    {
        if (key == getString(R.string.settings_fragment_username_key))
        {
            uiScope.backgroundJob {
                backgroundTask {
                    val defaultValue = getString(R.string.settings_fragment_username_default_value)
                    val agentName = pref?.getString(key, defaultValue) ?: defaultValue

                    val agent = Agent(name = agentName)
                    RealEstateDatabase.getInstance(context!!).agentDao().update(agent)
                }
            }
        }
    }

    override fun onResume()
    {
        super.onResume()
        preferenceManager.sharedPreferences.registerOnSharedPreferenceChangeListener(this)
    }

    override fun onPause()
    {
        preferenceManager.sharedPreferences.unregisterOnSharedPreferenceChangeListener(this)
        super.onPause()
    }

    override fun onStop()
    {
        settingsFragmentJob.cancel()
        super.onStop()
    }
}