package com.skichrome.realestatemanager.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.onNavDestinationSelected
import com.skichrome.realestatemanager.R
import com.skichrome.realestatemanager.utils.ARG_LIST_REALTY_ORIGIN
import kotlinx.android.synthetic.main.activity_main.*
import pub.devrel.easypermissions.EasyPermissions

class MainActivity : AppCompatActivity()
{
    // =================================
    //        Superclass Methods
    // =================================

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val navController = findNavController(R.id.activityMainMainNavHostFragment)

        configureBottomAppBar(navController)
        configureFab(navController)
        configureNavController(navController)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle)
    {
        super.onRestoreInstanceState(savedInstanceState)
        findNavController(R.id.activityMainMainNavHostFragment).currentDestination
    }

    override fun onSaveInstanceState(outState: Bundle)
    {
        findNavController(R.id.activityMainMainNavHostFragment).currentDestination
        super.onSaveInstanceState(outState)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    // =================================
    //              Methods
    // =================================

    private fun configureBottomAppBar(navController: NavController)
    {
        activityMainBottomAppBar?.inflateMenu(R.menu.toolbar_activity_main)
        activityMainBottomAppBar?.setOnMenuItemClickListener {
            return@setOnMenuItemClickListener it.onNavDestinationSelected(navController) || super.onOptionsItemSelected(it)
        }
    }

    private fun configureFab(navController: NavController)
    {
        activityMainFab?.setOnClickListener { navController.navigate(R.id.action_global_addRealtyFragment) }
    }

    private fun configureNavController(navController: NavController)
    {
        val arg = Bundle()
        arg.putBoolean(ARG_LIST_REALTY_ORIGIN, true)
        navController.setGraph(R.navigation.navigation, arg)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            activityMainBottomAppBar?.menu?.clear()

            when (destination.id)
            {
                R.id.detailsRealtyFragment, R.id.realtyListFragment ->
                {
                    changeFabMode()
                    changeAppBarDisplayHomeAsUp(navController)
                }
                R.id.addRealtyFragment, R.id.searchFragment, R.id.mapFragment, R.id.settingsFragment ->
                {
                    changeFabMode(appBarMenu = 0, fabResource = null)
                    changeAppBarDisplayHomeAsUp(navController, true)
                }
            }

            if (resources.getBoolean(R.bool.isTablet))
                return@addOnDestinationChangedListener

            val editRealtyVisibility = destination.id == R.id.detailsRealtyFragment
            activityMainBottomAppBar?.menu?.findItem(R.id.action_detailsRealtyFragment_to_addRealtyFragment)?.isVisible = editRealtyVisibility
        }
    }

    private fun changeAppBarDisplayHomeAsUp(navController: NavController, displayHomeAsUp: Boolean? = false)
    {
        if (displayHomeAsUp == null)
            activityMainBottomAppBar?.navigationIcon = null
        else
        {

            if (displayHomeAsUp)
            {
                activityMainBottomAppBar?.setNavigationIcon(R.drawable.ic_arrow_back_24dp)
                activityMainBottomAppBar?.setNavigationOnClickListener {
                    navController.navigateUp()
                }
            } else
            {
                activityMainBottomAppBar?.setNavigationIcon(R.drawable.ic_menu_24dp)
                activityMainBottomAppBar?.setNavigationOnClickListener {
                    BottomNavigationDrawerFragment().also {
                        it.show(supportFragmentManager, it.tag)
                    }
                }
            }
        }
    }

    private fun changeFabMode(appBarMenu: Int = R.menu.toolbar_activity_main, fabResource: Int? = R.drawable.ic_add_24dp)
    {
        if (appBarMenu == 0)
            activityMainBottomAppBar?.menu?.clear()
        else
            activityMainBottomAppBar?.replaceMenu(appBarMenu)

        fabResource?.let {
            activityMainFab?.setImageResource(fabResource)
            activityMainFab?.show()
        }
            ?: activityMainFab?.hide()
    }
}