package com.skichrome.realestatemanager.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.skichrome.realestatemanager.model.RealEstateDataRepository

class ViewModelFactory(
    private val dataSource: RealEstateDataRepository
) : ViewModelProvider.Factory
{
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T
    {
        if (modelClass.isAssignableFrom(RealtyViewModel::class.java))
            return RealtyViewModel(dataSource) as T
        throw IllegalArgumentException("Unknown View Model class")
    }
}