package com.skichrome.realestatemanager.view

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.onNavDestinationSelected
import androidx.navigation.ui.setupWithNavController
import com.skichrome.realestatemanager.R
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.toolbar.*

class MainActivity : AppCompatActivity()
{
    private lateinit var appBarConfiguration: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        configureNavigationComponent()
    }

    private fun configureNavigationComponent()
    {
        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        appBarConfiguration =
            AppBarConfiguration(setOf(R.id.realtyListFragment), activity_main_menu_drawer_layout)

        toolbar?.inflateMenu(R.menu.toolbar_activity_main)
        toolbar?.setupWithNavController(navController, appBarConfiguration)

        activity_main_navigation_view?.setupWithNavController(navController)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean
    {
        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        return item.onNavDestinationSelected(navController) || super.onOptionsItemSelected(item)
    }
}