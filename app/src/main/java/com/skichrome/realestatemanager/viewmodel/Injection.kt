package com.skichrome.realestatemanager.viewmodel

import android.content.Context
import com.skichrome.realestatemanager.androidmanagers.NetManager
import com.skichrome.realestatemanager.model.RealEstateDataRepository
import com.skichrome.realestatemanager.model.RealEstateLocalRepository
import com.skichrome.realestatemanager.model.database.RealEstateDatabase

object Injection
{
    private fun provideRealtyRepository(context: Context): RealEstateLocalRepository
    {
        val db = RealEstateDatabase.getInstance(context)
        return RealEstateLocalRepository(db)
    }

    private fun provideRealtyDataSource(context: Context): RealEstateDataRepository
    {
        val netManager = NetManager(context)
        val realtyRepo = provideRealtyRepository(context)
        return RealEstateDataRepository(netManager, realtyRepo)
    }

    fun provideViewModelFactory(context: Context): ViewModelFactory
    {
        val realtyRepo = provideRealtyDataSource(context)

        return ViewModelFactory(realtyRepo)
    }
}