package com.skichrome.realestatemanager.view.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback

abstract class BaseMapFragment<T : ViewDataBinding, V : ViewModel> : BaseFragment<T, V>(), OnMapReadyCallback
{
    // =================================
    //              Fields
    // =================================

    private var mapView: MapView? = null
    protected var map: GoogleMap? = null

    abstract fun getMap(): MapView?

    // =================================
    //        Superclass Methods
    // =================================

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
    {
        val view = super.onCreateView(inflater, container, savedInstanceState)

        mapView = getMap()
        mapView?.onCreate(arguments)
        mapView?.getMapAsync(this)

        return view
    }

    override fun onMapReady(gMap: GoogleMap?)
    {
        this.map = gMap
    }

    override fun onStart()
    {
        super.onStart()
        mapView?.onStart()
    }

    override fun onResume()
    {
        super.onResume()
        mapView?.onResume()
    }

    override fun onPause()
    {
        mapView?.onPause()
        super.onPause()
    }

    override fun onStop()
    {
        mapView?.onStop()
        super.onStop()
    }

    override fun onDestroy()
    {
        mapView?.onDestroy()
        map = null
        super.onDestroy()
    }
}