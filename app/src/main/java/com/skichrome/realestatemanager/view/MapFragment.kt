package com.skichrome.realestatemanager.view

import android.content.IntentSender
import android.location.Location
import android.os.Looper
import android.util.Log
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.model.LatLng
import com.skichrome.realestatemanager.R
import com.skichrome.realestatemanager.databinding.FragmentMapsBinding
import com.skichrome.realestatemanager.utils.CHECK_SETTINGS_RC
import com.skichrome.realestatemanager.utils.LOCATION_RC
import com.skichrome.realestatemanager.utils.MANIFEST_LOCATION_PERM
import com.skichrome.realestatemanager.view.base.BaseMapFragment
import com.skichrome.realestatemanager.viewmodel.RealtyViewModel
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.EasyPermissions

class MapFragment : BaseMapFragment<FragmentMapsBinding, RealtyViewModel>()
{
    // =================================
    //              Fields
    // =================================

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    private var lastLocation: Location? = null

    // =================================
    //        Superclass Methods
    // =================================

    override fun getFragmentLayout(): Int = R.layout.fragment_maps
    override fun getViewModelClass(): Class<RealtyViewModel> = RealtyViewModel::class.java
    override fun getMap(): MapView = binding.mapFragmentMapView

    override fun configureFragment()
    {
        binding.mapFragmentFab.setOnClickListener {
            lastLocation?.let {
                configureMap()
            } ?: configureLocationPermission()
        }
    }

    override fun onDestroy()
    {
        lastLocation?.let {
            fusedLocationClient.removeLocationUpdates(locationCallback)
        }
        lastLocation = null
        super.onDestroy()
    }

    override fun onMapReady(gMap: GoogleMap)
    {
        super.onMapReady(gMap)
        configureLocationPermission()
    }

    // =================================
    //              Methods
    // =================================

    // ------------ Location ------------

    @AfterPermissionGranted(LOCATION_RC)
    private fun configureLocationPermission()
    {
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