package com.skichrome.realestatemanager.androidmanagers

import android.content.Context
import android.net.ConnectivityManager

class NetManager(private val appContext: Context)
{
    val isConnectedToInternet: Boolean?
        get()
        {
            val conManager =
                appContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val networkInfo = conManager.activeNetworkInfo
            return networkInfo != null && networkInfo.isConnected
        }
}