package com.skichrome.realestatemanager.view

import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.preference.Preference
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

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?)
    {
        setPreferencesFromResource(R.xml.preferences_screen, rootKey)
        val notificationPreference: Preference? = findPreference(getString(R.string.settings_fragment_notification_key))
        notificationPreference?.setOnPreferenceClickListener { gotToNotificationSettings() }
    }

    override fun onSharedPreferenceChanged(pref: SharedPreferences?, key: String?)
    {
        if (key == getString(R.string.settings_fragment_username_key))
        {
            uiScope.backgroundJob {
                backgroundTask {
                    val defaultValue = getString(R.string.settings_fragment_username_default_value)
                    val agentName = pref?.getString(key, defaultValue) ?: defaultValue

                    val agent = Agent(agentId = 1L, name = agentName)
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

    // =================================
    //              Methods
    // =================================

    private fun gotToNotificationSettings(): Boolean
    {
        val intent = Intent()
        intent.action = when
        {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.O ->
            {
                intent.putExtra(Settings.EXTRA_APP_PACKAGE, context!!.packageName)
                Settings.ACTION_APP_NOTIFICATION_SETTINGS
            }
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP ->
            {
                intent.putExtra("app_package", context!!.packageName)
                intent.putExtra("app_uid", context!!.applicationInfo.uid)
                "android.settings.APP_NOTIFICATION_SETTINGS"
            }
            else ->
            {
                intent.addCategory(Intent.CATEGORY_DEFAULT)
                intent.data = Uri.parse("package:" + context!!.packageName)
                Settings.ACTION_APPLICATION_DETAILS_SETTINGS
            }
        }
        context?.startActivity(intent)
        return true
    }
}