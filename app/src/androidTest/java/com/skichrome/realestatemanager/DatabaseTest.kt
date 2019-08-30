package com.skichrome.realestatemanager

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.skichrome.realestatemanager.model.database.*
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException
import java.time.Instant
import java.util.*

@RunWith(AndroidJUnit4::class)
class DatabaseTest
{
    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    companion object
    {
        private lateinit var database: RealEstateDatabase
        private lateinit var realtyDao: RealtyDao
        private lateinit var poiDao: PoiDao
        private lateinit var realtyTypeDao: RealtyTypeDao
        private lateinit var mediaReferenceDao: MediaReferenceDao

        private const val AUTO_GENERATED_ID = 1L

        private val REALTY = Realty(
            id = AUTO_GENERATED_ID,
            price = 120_000f,
            address = "12 avenue du pont",
            postCode = 95000,
            city = "J'ai pas d'inspiration",
            agent = "Bob",
            dateAdded = Date.from(Instant.now()),
            fullDescription = "A big description",
            roomNumber = 4,
            status = false,
            surface = 45.57f
        )
        private val POI = Poi(
            poiId = AUTO_GENERATED_ID,
            type = "School",
            realtyId = AUTO_GENERATED_ID
        )
        private val REALTY_TYPE = RealtyType(
            realtyId = AUTO_GENERATED_ID,
            realtyTypeId = AUTO_GENERATED_ID,
            name = "Penthouse"
        )
        private val MEDIA_REF = MediaReference(
            mediaReferenceId = AUTO_GENERATED_ID,
            reference = "https://amazing-pict.com/amazing",
            realtyId = AUTO_GENERATED_ID,
            shortDesc = "RxJava tests crashes was very difficult to fix but now it's OK"
        )
    }

    @Before
    fun initDb()
    {
        database = Room.inMemoryDatabaseBuilder(
            InstrumentationRegistry.getInstrumentation().context,
            RealEstateDatabase::class.java
        )
            .allowMainThreadQueries()
            .build()
        realtyDao = database.realtyDao()
        poiDao = database.poiDao()
        realtyTypeDao = database.realtyTypeDao()
        mediaReferenceDao = database.mediaReferenceDao()

    }

    @After
    @Throws(IOException::class)
    fun closeDb()
    {
        database.close()
    }

    // =================================
    // Realty DAO Test
    // =================================

    @Test
    @Throws(Exception::class)
    fun insertAndGetRealtyAndTestReturnedID()
    {
        realtyDao.insertRealty(REALTY).blockingAwait()
        val storedRealty = realtyDao.getAllRealty().blockingFirst()[0]

        database.realtyDao()
            .getRealtyById(storedRealty.id)
            .test()
            .assertNoErrors()
            .assertValue {
                it.status == REALTY.status
                        && it.address == REALTY.address
                        && it.agent == REALTY.agent
                        && it.city == REALTY.city
                        && it.dateAdded == REALTY.dateAdded
                        && it.dateSell == REALTY.dateSell
                        && it.fullDescription == REALTY.fullDescription
                        && it.postCode == REALTY.postCode
                        && it.price == REALTY.price
                        && it.roomNumber == REALTY.roomNumber
                        && it.surface == REALTY.surface

                        && it.id == AUTO_GENERATED_ID
            }
    }

    @Test
    fun updateAndGetRealty()
    {
        realtyDao.insertRealty(REALTY).blockingAwait()
        val storedRealty = realtyDao.getAllRealty().blockingFirst()[0]
        storedRealty.status = true

        realtyDao.updateRealty(storedRealty).blockingAwait()

        realtyDao.getAllRealty()
            .test()
            .assertValue { it.last().status }
    }

    @Test
    fun insertAndDeleteRealty()
    {
        realtyDao.insertRealty(REALTY).blockingAwait()

        val storedRealty = realtyDao.getAllRealty().blockingFirst()[0]

        realtyDao.deleteRealtyById(storedRealty.id)
            .test()
            .assertComplete()

        realtyDao.getAllRealty()
            .test()
            .assertValue { it.isEmpty() }
    }


    // =================================
    // Poi DAO Test
    // =================================

    @Test
    fun insertAndGetPoi()
    {
        realtyDao.insertRealty(REALTY).blockingAwait()
        poiDao.insertPoi(POI).blockingAwait()

        poiDao.getPoiOfRealtyById(AUTO_GENERATED_ID)
            .test()
            .assertValue {
                it.poiId == POI.poiId
                        && it.realtyId == AUTO_GENERATED_ID
                        && it.type == POI.type
            }
    }

    @Test
    fun updateAndGetPoi()
    {
        realtyDao.insertRealty(REALTY).blockingAwait()
        poiDao.insertPoi(POI).blockingAwait()
        val storedPoi = poiDao.getPoiOfRealtyById(AUTO_GENERATED_ID).blockingFirst()
        storedPoi.type = "restaurant"

        poiDao.updatePoiOfRealty(storedPoi).blockingAwait()

        poiDao.getPoiOfRealtyById(AUTO_GENERATED_ID)
            .test()
            .assertValue { it.type == "restaurant" }
    }

    @Test
    fun insertAndDeletePoi()
    {
        realtyDao.insertRealty(REALTY).blockingAwait()
        poiDao.insertPoi(POI).blockingAwait()

        val storedPoi = poiDao.getPoiOfRealtyById(AUTO_GENERATED_ID).blockingFirst()
        poiDao.deletePoiOfRealtyById(storedPoi.poiId).blockingAwait()

        poiDao.getAllPoi()
            .test()
            .assertValue { it.isEmpty() }
    }

    // =================================
    // RealtyType DAO Test
    // =================================

    @Test
    fun insertAndGetRealtyType()
    {
        realtyDao.insertRealty(REALTY).blockingAwait()
        realtyTypeDao.insertRealtyType(REALTY_TYPE).blockingAwait()

        realtyTypeDao.getTypeOfRealtyById(AUTO_GENERATED_ID)
            .test()
            .assertValue {
                it.realtyId == REALTY_TYPE.realtyId
                        && it.realtyTypeId == AUTO_GENERATED_ID
                        && it.name == REALTY_TYPE.name
            }
    }

    @Test
    fun updateAndGetRealtyType()
    {
        realtyDao.insertRealty(REALTY).blockingAwait()
        realtyTypeDao.insertRealtyType(REALTY_TYPE).blockingAwait()
        val storedRealtyType = realtyTypeDao.getTypeOfRealtyById(AUTO_GENERATED_ID).blockingFirst()
        storedRealtyType.name = "House"

        realtyTypeDao.updateTypeOfRealty(storedRealtyType).blockingAwait()

        realtyTypeDao.getTypeOfRealtyById(AUTO_GENERATED_ID)
            .test()
            .assertValue { it.name == "House" }
    }

    @Test
    fun insertAndDeleteRealtyType()
    {
        realtyDao.insertRealty(REALTY).blockingAwait()
        realtyTypeDao.insertRealtyType(REALTY_TYPE).blockingAwait()

        val storedRealtyType = realtyTypeDao.getTypeOfRealtyById(AUTO_GENERATED_ID).blockingFirst()
        realtyTypeDao.deleteTypeOfRealtyById(storedRealtyType.realtyId).blockingAwait()

        realtyTypeDao.getAllRealtyType()
            .test()
            .assertValue { it.isEmpty() }
    }

    // =================================
    // MediaReference DAO Test
    // =================================

    @Test
    fun insertAndGetMediaReference()
    {
        realtyDao.insertRealty(REALTY).blockingAwait()
        mediaReferenceDao.insertMediaReference(MEDIA_REF).blockingAwait()

        mediaReferenceDao.getMediaOfRealtyById(AUTO_GENERATED_ID)
            .test()
            .assertValue {
                it.realtyId == REALTY_TYPE.realtyId
                        && it.mediaReferenceId == AUTO_GENERATED_ID
                        && it.reference == MEDIA_REF.reference
                        && it.realtyId == MEDIA_REF.realtyId
                        && it.shortDesc == MEDIA_REF.shortDesc
            }
    }

    @Test
    fun updateAndGetMediaReference()
    {
        realtyDao.insertRealty(REALTY).blockingAwait()
        mediaReferenceDao.insertMediaReference(MEDIA_REF).blockingAwait()
        val storedMediaReference =
            mediaReferenceDao.getMediaOfRealtyById(AUTO_GENERATED_ID).blockingFirst()
        storedMediaReference.reference = "https://new-reference.fr/"

        mediaReferenceDao.updateMediaOfRealty(storedMediaReference).blockingAwait()

        mediaReferenceDao.getMediaOfRealtyById(AUTO_GENERATED_ID)
            .test()
            .assertValue { it.reference == "https://new-reference.fr/" }
    }

    @Test
    fun insertAndDeleteMediaReference()
    {
        realtyDao.insertRealty(REALTY).blockingAwait()
        mediaReferenceDao.insertMediaReference(MEDIA_REF).blockingAwait()

        val storedMediaReference =
            mediaReferenceDao.getMediaOfRealtyById(AUTO_GENERATED_ID).blockingFirst()
        mediaReferenceDao.deleteMediaOfRealtyById(storedMediaReference.realtyId).blockingAwait()

        mediaReferenceDao.getMediaOfRealtyById(AUTO_GENERATED_ID)
            .test()
            .assertEmpty()
    }
}