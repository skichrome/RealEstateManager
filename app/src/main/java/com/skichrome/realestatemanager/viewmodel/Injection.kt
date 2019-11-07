package com.skichrome.realestatemanager.viewmodel

import android.content.Context
import com.skichrome.realestatemanager.androidmanagers.NetManager
import com.skichrome.realestatemanager.model.*
import com.skichrome.realestatemanager.model.database.RealEstateDatabase

object Injection
{
    // =======================================
    //                  Fields
    // =======================================

    private var netManager: NetManager? = null

    // =======================================
    //                 Methods
    // =======================================

    private fun getNetManager(context: Context) =
        netManager ?: synchronized(this) {
            netManager ?: NetManager(context).also {
                netManager = it
            }
        }

    private fun provideLocalRealtyRepo(context: Context): LocalRepository
    {
        val db = RealEstateDatabase.getInstance(context)
        return LocalRepository(db)
    }

    private fun provideRemoteRealtyRepo(): RemoteRepository
    {
        return RemoteRepository()
    }

    // --------- RealtyViewModel --------- //

    private fun provideRealtyRepo(context: Context): RealtyRepository
    {
        val nm = getNetManager(context)
        val localRealtyRepo = provideLocalRealtyRepo(context)
        val remoteRealtyRepo = provideRemoteRealtyRepo()
        return RealtyRepository(nm, localRealtyRepo, remoteRealtyRepo)
    }

    fun provideViewModelFactory(context: Context): ViewModelFactory
    {
        val realtyRepo = provideRealtyRepo(context)
        return ViewModelFactory(realtyRepository = realtyRepo)
    }

    // --------- SyncViewModel --------- //

    private fun provideOnlineSyncRepo(context: Context): OnlineSyncRepository
    {
        val nm = getNetManager(context)
        val localSyncRepo = provideLocalRealtyRepo(context)
        val remoteSyncRepo = provideRemoteRealtyRepo()
        return OnlineSyncRepository(nm, localSyncRepo, remoteSyncRepo)
    }

    fun provideSyncViewModelFactory(context: Context): ViewModelFactory
    {
        val syncRepo = provideOnlineSyncRepo(context)
        return ViewModelFactory(onlineSyncRepository = syncRepo)
    }

    // --------- SignInViewModel --------- //

    private fun provideSignInRepository(context: Context): SignInRepository
    {
        val nm = getNetManager(context)
        val localSyncRepo = provideLocalRealtyRepo(context)
        val remoteSyncRepo = provideRemoteRealtyRepo()
        return SignInRepository(nm, localSyncRepo, remoteSyncRepo)
    }

    fun provideSignInViewModelFactory(context: Context): ViewModelFactory
    {
        val signInRepo = provideSignInRepository(context)
        return ViewModelFactory(signInRepository = signInRepo)
    }
}