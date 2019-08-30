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
import java.util.*
import java.util.concurrent.TimeUnit

class RealtyViewModel(private val realtyDataSource: RealEstateDataRepository) : ViewModel()
{
    val realEstates = MutableLiveData<List<Realty>>() // can be observed from fragment
    val isLoading = ObservableField<Boolean>(false) // can be observed from data binding

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

    fun insertRealty()
    {
        val realty = Realty(
            price = 120_000f,
            address = "12 avenue du pont",
            postCode = 95000,
            city = "Brooklyn",
            agent = "Penthouse",
            dateAdded = Date(System.currentTimeMillis()),
            fullDescription = "A big description",
            roomNumber = 4,
            status = false,
            surface = 45.57f
        )

        isLoading.set(true)

        disposable += realtyDataSource.insertRealty(realty)
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .timeout(5, TimeUnit.SECONDS)
            .subscribeWith(object : DisposableCompletableObserver()
            {
                override fun onComplete()
                {
                    isLoading.set(false)
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