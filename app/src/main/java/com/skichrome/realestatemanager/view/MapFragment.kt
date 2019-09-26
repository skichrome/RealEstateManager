package com.skichrome.realestatemanager.view

import android.content.IntentSender
import android.location.Location
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.skichrome.realestatemanager.R
import com.skichrome.realestatemanager.databinding.FragmentMapsBinding
import com.skichrome.realestatemanager.utils.CHECK_SETTINGS_RC
import com.skichrome.realestatemanager.utils.LOCATION_RC
import com.skichrome.realestatemanager.utils.MANIFEST_LOCATION_PERM
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.EasyPermissions

class MapFragment : Fragment(), OnMapReadyCallback
{
    // =================================
    //              Fields
    // =================================

    private lateinit var binding: FragmentMapsBinding
    private lateinit var map: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    private var lastLocation: Location? = null

    // =================================
    //        Superclass Methods
    // =================================

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
    {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_maps, container, false)
        binding.mapFragmentMapView.onCreate(arguments)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?)
    {
        super.onViewCreated(view, savedInstanceState)
        binding.mapFragmentMapView.getMapAsync(this)

        binding.mapFragmentFab.setOnClickListener {
            lastLocation?.let {
                configureMap()
            } ?: configureLocationPermission()
        }
    }

    override fun onStart()
    {
        super.onStart()
        binding.mapFragmentMapView?.onStart()
    }

    override fun onResume()
    {
        super.onResume()
        binding.mapFragmentMapView?.onResume()
    }

    override fun onPause()
    {
        binding.mapFragmentMapView?.onPause()
        super.onPause()
    }

    override fun onStop()
    {
        binding.mapFragmentMapView?.onStop()
        super.onStop()
    }

    override fun onDestroy()
    {
        binding.mapFragmentMapView?.onDestroy()
        lastLocation?.let {
            fusedLocationClient.removeLocationUpdates(locationCallback)
        }
        lastLocation = null
        super.onDestroy()
    }

    override fun onMapReady(mapReady: GoogleMap)
    {
        map = mapReady
        configureLocationPermission()
    }

    // =================================
    //              Methods
    // =================================

    // ------------ Location ------------

    @AfterPermissionGranted(LOCATION_RC)
    private fun configureLocationPermission()
    {
        Log.e("LocationSettings", "Triggered !")
        if (EasyPermissions.hasPermissions(context!!, MANIFEST_LOCATION_PERM))
            createLocationRequest()
        else
            EasyPermissions.requestPermissions(this, "You need to enable location", LOCATION_RC, MANIFEST_LOCATION_PERM)
    }

    private fun createLocationRequest()
    {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context!!)
        val locationRequest = LocationRequest.create()?.apply {
            interval = 10000
            fastestInterval = 5000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }
        configureLocationSettings(locationRequest)
    }

    private fun configureLocationSettings(locationRequest: LocationRequest?)
    {
        locationRequest?.let {
            val builder = LocationSettingsRequest.Builder()
                .addLocationRequest(it)

            val client = LocationServices.getSettingsClient(context!!)
            val task = client.checkLocationSettings(builder.build())

            task.addOnSuccessListener {
                configureLocationCallback()
                startLocationUpdates(locationRequest)
            }

            task.addOnFailureListener { exception ->
                if (exception is ResolvableApiException)
                {
                    try
                    {
                        exception.startResolutionForResult(activity, CHECK_SETTINGS_RC)
                    } catch (e: IntentSender.SendIntentException)
                    {
                        Log.e("LocationSettings", "Error when try to ask to user to change location settings", e.cause)
                    }
                }
            }
        }
    }

    private fun startLocationUpdates(locationRequest: LocationRequest?)
    {
        locationRequest?.let {
            fusedLocationClient.requestLocationUpdates(it, locationCallback, Looper.getMainLooper())
        }
    }

    private fun configureLocationCallback()
    {
        locationCallback = object : LocationCallback()
        {
            override fun onLocationResult(locationResult: LocationResult?)
            {
                locationResult ?: return
                lastLocation = locationResult.lastLocation
                configureMap()
            }
        }
    }

    // ------------ Map ------------

    private fun configureMap()
    {
        val latLng = LatLng(lastLocation!!.latitude, lastLocation!!.longitude)
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10f))
    }
}