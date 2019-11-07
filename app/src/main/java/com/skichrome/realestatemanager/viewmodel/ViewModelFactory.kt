package com.skichrome.realestatemanager.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.skichrome.realestatemanager.model.OnlineSyncRepository
import com.skichrome.realestatemanager.model.RealtyRepository
import com.skichrome.realestatemanager.model.SignInRepository

class ViewModelFactory(
    private val realtyRepository: RealtyRepository? = null,
    private val onlineSyncRepository: OnlineSyncRepository? = null,
    private val signInRepository: SignInRepository? = null
) : ViewModelProvider.Factory
{
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T
    {
        return when
        {
            modelClass.isAssignableFrom(RealtyViewModel::class.java) -> RealtyViewModel(realtyRepository!!) as T
            modelClass.isAssignableFrom(OnlineSyncViewModel::class.java) -> OnlineSyncViewModel(onlineSyncRepository!!) as T
            modelClass.isAssignableFrom(SignInViewModel::class.java) -> SignInViewModel(signInRepository!!) as T
            else -> throw IllegalArgumentException("Unknown View Model class")
        }
    }
}