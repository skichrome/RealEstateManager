package com.skichrome.realestatemanager.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.skichrome.realestatemanager.model.OnlineSyncRepository
import com.skichrome.realestatemanager.model.RealtyRepository

class ViewModelFactory(
    private val realtyRepository: RealtyRepository? = null,
    private val onlineSyncRepository: OnlineSyncRepository? = null
) : ViewModelProvider.Factory
{
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T
    {
        if (modelClass.isAssignableFrom(RealtyViewModel::class.java))
            return RealtyViewModel(realtyRepository!!) as T
        if (modelClass.isAssignableFrom(OnlineSyncViewModel::class.java))
            return OnlineSyncViewModel(onlineSyncRepository!!) as T
        throw IllegalArgumentException("Unknown View Model class")
    }
}