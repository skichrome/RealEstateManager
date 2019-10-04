package com.skichrome.realestatemanager.viewmodel

import android.content.Context
import com.skichrome.realestatemanager.androidmanagers.NetManager
import com.skichrome.realestatemanager.model.RealtyLocalRepository
import com.skichrome.realestatemanager.model.RealtyRepository
import com.skichrome.realestatemanager.model.database.RealEstateDatabase
import com.skichrome.realestatemanager.model.retrofit.RealtyRemoteRepository

object Injection
{
    private fun provideLocalRealtyRepo(context: Context): RealtyLocalRepository
    {
        val db = RealEstateDatabase.getInstance(context)
        return RealtyLocalRepository(db)
    }

    private fun provideRemoteRealtyRepo(): RealtyRemoteRepository
    {
        return RealtyRemoteRepository()
    }

    private fun provideRealtyRepo(context: Context): RealtyRepository
    {
        val netManager = NetManager(context)
        val realtyRepo = provideLocalRealtyRepo(context)
        val remoteDataSource = provideRemoteRealtyRepo()
        return RealtyRepository(netManager, realtyRepo, remoteDataSource)
    }

    fun provideViewModelFactory(context: Context): ViewModelFactory
    {
        val realtyRepo = provideRealtyRepo(context)
        return ViewModelFactory(realtyRepo)
    }
}