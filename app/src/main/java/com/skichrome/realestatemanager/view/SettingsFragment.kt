package com.skichrome.realestatemanager.view

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.skichrome.realestatemanager.R

class SettingsFragment : PreferenceFragmentCompat()
{
    // =================================
    //        Superclass Methods
    // =================================

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?)
    {
        setPreferencesFromResource(R.xml.preferences_screen, rootKey)
        val notificationPreference: Preference? = findPreference(getString(R.string.settings_fragment_notification_key))
        notificationPreference?.setOnPreferenceClickListener { gotToNotificationSettings() }
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