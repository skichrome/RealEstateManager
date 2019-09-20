package com.skichrome.realestatemanager.view

import android.os.Bundle
import android.view.Menu
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
        val navController = findNavController(R.id.activityMainMainNavHostFragment)
        appBarConfiguration =
            AppBarConfiguration(setOf(R.id.realtyListFragment), activity_main_menu_drawer_layout)

        setSupportActionBar(toolbar)
        toolbar?.setupWithNavController(navController, appBarConfiguration)

        activityMainNavigationView?.setupWithNavController(navController)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean
    {
        val result = super.onCreateOptionsMenu(menu)
        menuInflater.inflate(R.menu.toolbar_activity_main, menu)
        return result
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean
    {
        val navController = findNavController(R.id.activityMainMainNavHostFragment)
        return item.onNavDestinationSelected(navController) || super.onOptionsItemSelected(item)
    }
}