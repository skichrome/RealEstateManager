package com.skichrome.realestatemanager.viewmodel

import android.util.Log
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.skichrome.realestatemanager.model.RealEstateDataRepository
import com.skichrome.realestatemanager.model.database.Realty
import com.skichrome.realestatemanager.utils.plusAssign
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableCompletableObserver
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit

class RealtyViewModel(private val realtyDataSource: RealEstateDataRepository) : ViewModel()
{
    val realEstates = MutableLiveData<List<Realty>>() // can be observed from fragment
    val isLoading = ObservableField<Boolean>(false) // can be observed from data binding
    val insertLoading = MutableLiveData<Boolean>()

    private val disposable = CompositeDisposable()

    fun observeDatabaseChanges()
    {
        isLoading.set(true)
        disposable += realtyDataSource.getAllRealty()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {
                    realEstates.value = it; isLoading.set(false); Log.e(
                    "RealtyViewModel",
                    "On Next"
                )
                },
                { Log.e("RealtyViewModel", "ON ERROR", it) },
                { Log.e("RealtyViewModel", "On Complete Method") }
            )
    }

    fun insertRealty(realty: Realty)
    {
        insertLoading.value = true

        disposable += realtyDataSource.insertRealty(realty)
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .timeout(5, TimeUnit.SECONDS)
            .subscribeWith(object : DisposableCompletableObserver()
            {
                override fun onComplete()
                {
                    insertLoading.value = false
                    Log.e("INSERT", "Successfully inserted realty into database !")
                }

                override fun onError(e: Throwable)
                {
                    Log.e("INSERT", "An error occured when thy to insert Realty", e)
                }
            })
    }

    override fun onCleared()
    {
        super.onCleared()
        if (!disposable.isDisposed)
            disposable.dispose()
    }
}