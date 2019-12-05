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
        private lateinit var agentDao: AgentDao

        private const val AUTO_GENERATED_ID = 1L

        private val AGENT = Agent(agentId = 1L, name = "Boris", lastUpdate = System.currentTimeMillis())

        private val REALTY = Realty(
            id = AUTO_GENERATED_ID,
            price = 120_000f,
            currency = 0,
            address = "12 avenue du pont",
            postCode = 95000,
            city = "J'ai pas d'inspiration",
            dateAdded = System.currentTimeMillis(),
            fullDescription = "A big description",
            roomNumber = 4,
            status = false,
            surface = 45.57f,
            realtyTypeId = AUTO_GENERATED_ID.toInt(),
            agentId = 1L
        )

        private val REALTY2 = Realty(
            id = AUTO_GENERATED_ID + 1L,
            price = 120_000f,
            currency = 1,
            address = "12 avenue du pont",
            postCode = 95000,
            city = "J'ai pas d'inspiration",
            dateAdded = System.currentTimeMillis(),
            fullDescription = "A big description",
            roomNumber = 4,
            status = false,
            surface = 45.57f,
            realtyTypeId = AUTO_GENERATED_ID.toInt() + 1,
            agentId = 1L
        )

        private val POI = Poi(
            poiId = AUTO_GENERATED_ID.toInt(),
            name = "School"
        )

        private val REALTY_TYPE = RealtyType(
            realtyTypeId = AUTO_GENERATED_ID.toInt(),
            name = "Penthouse"
        )

        private val REALTY_TYPE2 = RealtyType(
            realtyTypeId = AUTO_GENERATED_ID.toInt() + 1,
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
        agentDao = database.agentDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() = database.close()

    // =================================
    //  Realty DAO Test
    // =================================

    @Test
    @Throws(Exception::class)
    fun insertAndGetRealty() = runBlocking {
        agentDao.insertIgnore(AGENT)
        realtyTypeDao.insertIgnore(REALTY_TYPE, REALTY_TYPE2)
        realtyDao.insertIgnore(REALTY, REALTY2)

        val storedRealty = realtyDao.getAllRealty()
        assertEquals(listOf(REALTY, REALTY2), storedRealty)
    }

    @Test
    @Throws(Exception::class)
    fun updateAndGetRealty() = runBlocking {
        agentDao.insertIgnore(AGENT)
        realtyTypeDao.insertIgnore(REALTY_TYPE, REALTY_TYPE2)
        realtyDao.insertIgnore(REALTY, REALTY2)

        val storedRealty = realtyDao.getAllRealty().first()
        storedRealty.status = true
        realtyDao.update(storedRealty)

        val updatedRealtyList = realtyDao.getAllRealty()
        assertEquals(listOf(storedRealty, REALTY2), updatedRealtyList)
    }

    // =================================
    //  Poi DAO Test
    // =================================

    @Test
    @Throws(Exception::class)
    fun insertAndGetPoi() = runBlocking {
        poiDao.insertIgnore(POI)

        val storedPoi = poiDao.getAllPoi()
        assertEquals(listOf(POI), storedPoi)

    }

    @Test
    @Throws(Exception::class)
    fun updateAndGetPoi() = runBlocking {
        poiDao.insertIgnore(POI)

        val storedPoi = poiDao.getAllPoi().first()
        storedPoi.name = "restaurant"
        poiDao.update(storedPoi)

        val updatedPoi = poiDao.getAllPoi()
        assertEquals(listOf(storedPoi), updatedPoi)
    }

    // =================================
    //  RealtyType DAO Test
    // =================================

    // =================================
    //  MediaReference DAO Test
    // =================================

    @Test
    @Throws(Exception::class)
    fun insertAndGetMediaReference() = runBlocking {
        agentDao.insertIgnore(AGENT)
        realtyTypeDao.insertIgnore(REALTY_TYPE, REALTY_TYPE2)
        realtyDao.insertIgnore(REALTY, REALTY2)
        mediaReferenceDao.insertIgnore(MEDIA_REF)

        val storedMediaRef = mediaReferenceDao.getAllMedias()
        assertEquals(listOf(MEDIA_REF), storedMediaRef)
    }

    @Test
    @Throws(Exception::class)
    fun updateAndGetMediaReference() = runBlocking {
        agentDao.insertIgnore(AGENT)
        realtyTypeDao.insertIgnore(REALTY_TYPE, REALTY_TYPE2)
        realtyDao.insertIgnore(REALTY, REALTY2)
        mediaReferenceDao.insertIgnore(MEDIA_REF)

        val storedMediaReference = mediaReferenceDao.getAllMedias().first()
        storedMediaReference.reference = "https://new-reference.fr/"
        mediaReferenceDao.update(storedMediaReference)

        val updatedMediaRef = mediaReferenceDao.getAllMedias()
        assertEquals(listOf(storedMediaReference), updatedMediaRef)
    }

    @Test
    @Throws(Exception::class)
    fun insertAndDeleteMediaReference() = runBlocking {
        agentDao.insertIgnore(AGENT)
        realtyTypeDao.insertIgnore(REALTY_TYPE, REALTY_TYPE2)
        realtyDao.insertIgnore(REALTY, REALTY2)
        mediaReferenceDao.insertIgnore(MEDIA_REF)

        val storedMediaReference = mediaReferenceDao.getAllMedias().first()
        mediaReferenceDao.deleteMediaOfRealtyById(storedMediaReference.realtyId)

        val expectedEmptyList = mediaReferenceDao.getAllMedias()
        assertEquals(true, expectedEmptyList.isEmpty())
    }

    // =================================
    //  Agent DAO Test
    // =================================
}