package com.skichrome.realestatemanager

import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.skichrome.realestatemanager.model.database.*
import com.skichrome.realestatemanager.utils.AppCoroutinesConfiguration
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException
import java.time.Instant
import java.util.*

@RunWith(AndroidJUnit4::class)
class DatabaseTest
{
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
            surface = 45.57f,
            realtyTypeId = AUTO_GENERATED_ID.toInt()
        )
        private val REALTY2 = Realty(
            id = AUTO_GENERATED_ID + 1,
            price = 120_000f,
            address = "12 avenue du pont",
            postCode = 95000,
            city = "J'ai pas d'inspiration",
            agent = "Bob",
            dateAdded = Date.from(Instant.now()),
            fullDescription = "A big description",
            roomNumber = 4,
            status = false,
            surface = 45.57f,
            realtyTypeId = AUTO_GENERATED_ID.toInt()
        )
        private val POI = Poi(
            poiId = AUTO_GENERATED_ID.toInt(),
            name = "School"
        )
        private val REALTY_TYPE = RealtyType(
            realtyTypeId = AUTO_GENERATED_ID.toInt(),
            name = "Penthouse"
        )
        private val MEDIA_REF = MediaReference(
            mediaReferenceId = AUTO_GENERATED_ID,
            realtyId = AUTO_GENERATED_ID,
            reference = "https://amazing-pict.com/amazing",
            shortDesc = "RxJava tests crashes was very difficult to fix but now it's OK"
        )

        @ExperimentalCoroutinesApi
        @BeforeClass
        @JvmStatic
        fun initCoroutines()
        {
            with(AppCoroutinesConfiguration) {
                uiDispatchers = Dispatchers.Unconfined
                backgroundDispatchers = Dispatchers.Unconfined
                ioDispatchers = Dispatchers.Unconfined
            }
        }
    }

    @Before
    fun initDb()
    {
        database = Room.inMemoryDatabaseBuilder(InstrumentationRegistry.getInstrumentation().context, RealEstateDatabase::class.java)
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
    fun insertAndGetRealty() = runBlocking {
        realtyTypeDao.insertRealtyType(REALTY_TYPE)
        realtyDao.insertRealty(REALTY)
        realtyDao.insertRealty(REALTY2)
        val storedRealty = realtyDao.getAllRealty()

        assertEquals(listOf(REALTY, REALTY2), storedRealty)
    }

    @Test
    @Throws(Exception::class)
    fun updateAndGetRealty() = runBlocking {
        realtyTypeDao.insertRealtyType(REALTY_TYPE)
        realtyDao.insertRealty(REALTY)
        val storedRealty = realtyDao.getAllRealty().first()
        storedRealty.status = true
        realtyDao.updateRealty(storedRealty)

        val updatedRealtyList = realtyDao.getAllRealty()
        assertEquals(listOf(storedRealty), updatedRealtyList)
    }

    @Test
    @Throws(Exception::class)
    fun insertAndDeleteRealty() = runBlocking {
        realtyTypeDao.insertRealtyType(REALTY_TYPE)
        realtyDao.insertRealty(REALTY)
        val storedRealty = realtyDao.getAllRealty().first()
        realtyDao.deleteRealtyById(storedRealty.id)

        val expectedEmptyList = realtyDao.getAllRealty()
        assert(expectedEmptyList.isEmpty())
    }

    // =================================
    // Poi DAO Test
    // =================================

    @Test
    @Throws(Exception::class)
    fun insertAndGetPoi() = runBlocking {
        realtyTypeDao.insertRealtyType(REALTY_TYPE)
        realtyDao.insertRealty(REALTY)
        poiDao.insertPoi(POI)

        val storedPoi = poiDao.getAllPoi()
        assertEquals(listOf(POI), storedPoi)

    }

    @Test
    @Throws(Exception::class)
    fun updateAndGetPoi() = runBlocking {
        realtyTypeDao.insertRealtyType(REALTY_TYPE)
        realtyDao.insertRealty(REALTY)
        val insertedPoiId = poiDao.insertPoi(POI)

        val storedPoi = poiDao.getPoiOfRealtyById(insertedPoiId.toInt())
        storedPoi.name = "restaurant"
        poiDao.updatePoiOfRealty(storedPoi)

        val updatedPoi = poiDao.getAllPoi()
        assertEquals(listOf(storedPoi), updatedPoi)
    }

    @Test
    @Throws(Exception::class)
    fun insertAndDeletePoi() = runBlocking {
        realtyTypeDao.insertRealtyType(REALTY_TYPE)
        realtyDao.insertRealty(REALTY)
        val insertedPoiId = poiDao.insertPoi(POI)

        val storedPoi = poiDao.getPoiOfRealtyById(insertedPoiId.toInt())
        poiDao.deletePoiOfRealtyById(storedPoi.poiId)

        val expectedEmptyList = poiDao.getAllPoi()
        assert(expectedEmptyList.isEmpty())
    }

    // =================================
    // RealtyType DAO Test
    // =================================

    @Test
    @Throws(Exception::class)
    fun insertAndGetRealtyType() = runBlocking {
        realtyTypeDao.insertRealtyType(REALTY_TYPE)

        val storedType = realtyTypeDao.getAllRealtyType()
        assertEquals(listOf(REALTY_TYPE), storedType)
    }

    @Test
    @Throws(Exception::class)
    fun updateAndGetRealtyType() = runBlocking {
        val insertedTypeId = realtyTypeDao.insertRealtyType(REALTY_TYPE)

        val storedRealtyType = realtyTypeDao.getTypeOfRealtyById(insertedTypeId.toInt())
        storedRealtyType.name = "House"
        realtyTypeDao.updateTypeOfRealty(storedRealtyType)

        val updatedType = realtyTypeDao.getAllRealtyType()
        assertEquals(listOf(storedRealtyType), updatedType)
    }

    @Test
    @Throws(Exception::class)
    fun insertAndDeleteRealtyType() = runBlocking {
        val insertedTypeId = realtyTypeDao.insertRealtyType(REALTY_TYPE)

        val storedRealtyType = realtyTypeDao.getTypeOfRealtyById(insertedTypeId.toInt())
        realtyTypeDao.deleteTypeOfRealtyById(storedRealtyType.realtyTypeId)

        val expectedEmptyList = realtyTypeDao.getAllRealtyType()
        assert(expectedEmptyList.isEmpty())
    }

    // =================================
    // MediaReference DAO Test
    // =================================

    @Test
    @Throws(Exception::class)
    fun insertAndGetMediaReference() = runBlocking {
        realtyTypeDao.insertRealtyType(REALTY_TYPE)
        realtyDao.insertRealty(REALTY)
        val insertedMediaRefId = mediaReferenceDao.insertMediaReference(MEDIA_REF)

        val storedMediaRef = mediaReferenceDao.getMediaOfRealtyById(insertedMediaRefId)
        assertEquals(MEDIA_REF, storedMediaRef)
    }

    @Test
    @Throws(Exception::class)
    fun updateAndGetMediaReference() = runBlocking {
        realtyTypeDao.insertRealtyType(REALTY_TYPE)
        realtyDao.insertRealty(REALTY)
        val insertedMediaRefId = mediaReferenceDao.insertMediaReference(MEDIA_REF)

        val storedMediaReference = mediaReferenceDao.getMediaOfRealtyById(insertedMediaRefId)
        storedMediaReference.reference = "https://new-reference.fr/"
        mediaReferenceDao.updateMediaOfRealty(storedMediaReference)

        val updatedMediaRef = mediaReferenceDao.getAllMedias()
        assertEquals(listOf(storedMediaReference), updatedMediaRef)
    }

    @Test
    @Throws(Exception::class)
    fun insertAndDeleteMediaReference() = runBlocking {
        realtyTypeDao.insertRealtyType(REALTY_TYPE)
        realtyDao.insertRealty(REALTY)
        val insertedMediaRefId = mediaReferenceDao.insertMediaReference(MEDIA_REF)

        val storedMediaReference = mediaReferenceDao.getMediaOfRealtyById(insertedMediaRefId)
        mediaReferenceDao.deleteMediaOfRealtyById(storedMediaReference.realtyId)

        val expectedEmptyList = mediaReferenceDao.getAllMedias()
        assert(expectedEmptyList.isEmpty())
    }
}