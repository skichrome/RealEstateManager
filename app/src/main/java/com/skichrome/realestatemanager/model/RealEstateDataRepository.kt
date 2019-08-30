package com.skichrome.realestatemanager.model

import com.skichrome.realestatemanager.androidmanagers.NetManager
import com.skichrome.realestatemanager.model.database.Realty
import io.reactivex.Completable
import io.reactivex.Flowable
import java.util.*
import java.util.concurrent.TimeUnit

class RealEstateDataRepository(
    private val netManager: NetManager,
    private val localDataSource: RealEstateLocalRepository
)
{
    fun getAllRealty(): Flowable<List<Realty>>
    {
        netManager.isConnectedToInternet?.let {
            if (it)
            {
                // Todo Save to remote api
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
                // Save to local
                localDataSource.createRealty(realty)
                // Return value
                return Flowable.just(listOf(realty))
                    .delay(2, TimeUnit.SECONDS)
            }
        }

        return localDataSource.getAllRealty()
    }

    fun insertRealty(realty: Realty): Completable =
        localDataSource.createRealty(realty).delay(500, TimeUnit.MILLISECONDS)
}