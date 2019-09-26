package com.skichrome.realestatemanager.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.onNavDestinationSelected
import androidx.navigation.ui.setupWithNavController
import com.skichrome.realestatemanager.R
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.toolbar.*
import pub.devrel.easypermissions.EasyPermissions

class MainActivity : AppCompatActivity()
{
    private lateinit var appBarConfiguration: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val navController = findNavController(R.id.activityMainMainNavHostFragment)

        configureToolbar(navController)
        configureNavController(navController)
        configureNavigationView(navController)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    private fun configureToolbar(navController: NavController)
    {
        appBarConfiguration =
            AppBarConfiguration(setOf(R.id.realtyListFragment), activity_main_menu_drawer_layout)

        toolbar?.setupWithNavController(navController, appBarConfiguration)
        toolbar?.inflateMenu(R.menu.toolbar_activity_main)
        toolbar?.setOnMenuItemClickListener {
            return@setOnMenuItemClickListener it.onNavDestinationSelected(navController) || super.onOptionsItemSelected(it)
        }
    }

    private fun configureNavController(navController: NavController)
    {
        navController.addOnDestinationChangedListener { _, destination, _ ->

            toolbar?.menu?.clear()
            if (destination.id != R.id.mapFragment)
                toolbar?.inflateMenu(R.menu.toolbar_activity_main)

            if (resources.getBoolean(R.bool.isTablet))
                return@addOnDestinationChangedListener

            val editRealtyVisibility = destination.id == R.id.detailsRealtyFragment
            toolbar?.menu?.findItem(R.id.action_realtyListFragment_to_addRealtyFragment)?.isVisible = editRealtyVisibility


        }
    }

    private fun configureNavigationView(navController: NavController) =
        activityMainNavigationView?.setupWithNavController(navController)
}