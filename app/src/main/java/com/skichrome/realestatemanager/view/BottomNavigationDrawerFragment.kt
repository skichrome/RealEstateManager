package com.skichrome.realestatemanager.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.onNavDestinationSelected
import androidx.preference.PreferenceManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.skichrome.realestatemanager.R
import kotlinx.android.synthetic.main.bottom_menu_drawer_fragment.*

class BottomNavigationDrawerFragment : BottomSheetDialogFragment()
{
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.bottom_menu_drawer_fragment, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?)
    {
        super.onViewCreated(view, savedInstanceState)
        bottomNavDrawerFragmentNavigationView?.setNavigationItemSelectedListener {
            if (it.itemId == R.id.changeCurrentAgent)
            {
                activity?.run {
                    val sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this)
                    sharedPrefs.edit()
                        .putLong(getString(R.string.settings_fragment_username_key), -1L)
                        .apply()
                    finish()
                }
                return@setNavigationItemSelectedListener true
            }
            this.dismiss()
            return@setNavigationItemSelectedListener it.onNavDestinationSelected(findNavController()) || super.onContextItemSelected(it)
        }
    }
}