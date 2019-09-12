package com.skichrome.realestatemanager.androidmanagers

import android.content.Context
import android.net.ConnectivityManager

class NetManager(appContext: Context)
{
    private val conManager: ConnectivityManager = appContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    val isConnectedToInternet: Boolean?
        get()
        {
            val networkInfo = conManager.activeNetworkInfo
            return networkInfo != null && networkInfo.isConnected
        }
}