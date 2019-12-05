package com.skichrome.realestatemanager

import androidx.room.Room
import androidx.sqlite.db.SimpleSQLiteQuery
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.skichrome.realestatemanager.model.database.*
import com.skichrome.realestatemanager.utils.AppCoroutinesConfiguration
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.*
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
        private lateinit var rawQueryDao: RawQueryDao

        private const val AUTO_GENERATED_ID = 1L

        private val AGENT = Agent(agentId = 1L, name = "Boris", lastUpdate = System.currentTimeMillis())
        private val AGENT2 = Agent(agentId = 2L, name = "Imotep", lastUpdate = System.currentTimeMillis())

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
            agentId = 1L,
            latitude = 3.3333,
            longitude = 48.8888
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
            reference = "https://amazing-pict.com/amazing/one",
            shortDesc = "RxJava tests crashes was very difficult to fix but now it's OK, NO MORE RXJAVA, Coroutines will WIN !"
        )

        private val MEDIA_REF2 = MediaReference(
            mediaReferenceId = AUTO_GENERATED_ID + 1,
            realtyId = AUTO_GENERATED_ID + 1,
            reference = "https://amazing-pict.com/amazing/two",
            shortDesc = "RxJava tests crashes was very difficult to fix but now it's OK"
        )

        private val MEDIA_REF3 = MediaReference(
            mediaReferenceId = AUTO_GENERATED_ID + 2,
            realtyId = AUTO_GENERATED_ID,
            reference = "https://amazing-pict.com/amazing/three",
            shortDesc = "RxJava tests crashes was very difficult to fix but now it's OK, Imotep"
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
        rawQueryDao = database.rawQueryDao()
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
    fun insertAndGetRealtyById() = runBlocking {
        agentDao.insertIgnore(AGENT)
        realtyTypeDao.insertIgnore(REALTY_TYPE, REALTY_TYPE2)
        val insertedRealtyId = realtyDao.insertIgnore(REALTY, REALTY2).firstOrNull()

        assertNotNull(insertedRealtyId)

        val realty = realtyDao.getRealtyById(insertedRealtyId!!)
        assertEquals(REALTY, realty)
    }

    @Test
    @Throws(Exception::class)
    fun insertAndGetRealtyWithLatLngNull() = runBlocking {
        agentDao.insertIgnore(AGENT)
        realtyTypeDao.insertIgnore(REALTY_TYPE, REALTY_TYPE2)
        realtyDao.insertIgnore(REALTY, REALTY2).firstOrNull()

        val realtyLatLngNull = realtyDao.getRealtyListLatLngNull()
        assertNotNull(realtyLatLngNull)
        assertEquals(listOf(REALTY), realtyLatLngNull)
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
        assertNotEquals(listOf(REALTY, REALTY2), updatedRealtyList)
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
        assertNotEquals(listOf(POI), updatedPoi)
    }

    // =================================
    //  RealtyType DAO Test
    // =================================

    @Test
    @Throws(Exception::class)
    fun insertAndGetRealtyType() = runBlocking {
        realtyTypeDao.insertIgnore(REALTY_TYPE, REALTY_TYPE2)

        val storedRealtyType = realtyTypeDao.getAllRealtyTypes()
        assertEquals(listOf(REALTY_TYPE, REALTY_TYPE2), storedRealtyType)
    }

    @Test
    @Throws(Exception::class)
    fun updateAndGetRealtyType() = runBlocking {
        realtyTypeDao.insertIgnore(REALTY_TYPE, REALTY_TYPE2)

        val storedRealtyType = realtyTypeDao.getAllRealtyTypes().firstOrNull()
        assertNotNull(storedRealtyType)

        storedRealtyType?.also {
            it.name = "Imotep"
            realtyTypeDao.update(it)
        }

        val updatedRealtyType = realtyTypeDao.getAllRealtyTypes()
        assertEquals(listOf(storedRealtyType, REALTY_TYPE2), updatedRealtyType)
        assertNotEquals(listOf(REALTY_TYPE, REALTY_TYPE2), updatedRealtyType)
    }

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
        mediaReferenceDao.insertIgnore(MEDIA_REF, MEDIA_REF2)

        val storedMediaReference = mediaReferenceDao.getAllMedias().firstOrNull()
        assertNotNull(storedMediaReference)

        storedMediaReference!!.also {
            it.reference = "https://new-reference.fr/"
            mediaReferenceDao.update(it)
        }

        val updatedMediaRef = mediaReferenceDao.getAllMedias()
        assertEquals(listOf(storedMediaReference, MEDIA_REF2), updatedMediaRef)
        assertNotEquals(listOf(MEDIA_REF, MEDIA_REF2), updatedMediaRef)
    }

    @Test
    @Throws(Exception::class)
    fun insertAndGetMediaReferenceFromRealtyId() = runBlocking {
        agentDao.insertIgnore(AGENT)
        realtyTypeDao.insertIgnore(REALTY_TYPE, REALTY_TYPE2)
        val insertedRealtyId = realtyDao.insertIgnore(REALTY, REALTY2).firstOrNull()
        mediaReferenceDao.insertIgnore(MEDIA_REF, MEDIA_REF2, MEDIA_REF3)

        assertNotNull(insertedRealtyId)
        mediaReferenceDao.getMediaOfRealtyById(insertedRealtyId!!).let {
            assertEquals(listOf(MEDIA_REF, MEDIA_REF3), it)
            assertNotEquals(listOf(MEDIA_REF, MEDIA_REF2, MEDIA_REF3), it)
        }
    }

    @Test
    @Throws(Exception::class)
    fun insertAndGetFirstMediaReferenceFromRealtyId() = runBlocking {
        agentDao.insertIgnore(AGENT)
        realtyTypeDao.insertIgnore(REALTY_TYPE, REALTY_TYPE2)
        val insertedRealtyId = realtyDao.insertIgnore(REALTY, REALTY2).firstOrNull()
        mediaReferenceDao.insertIgnore(MEDIA_REF, MEDIA_REF2, MEDIA_REF3)

        mediaReferenceDao.getFirstMediaOfRealtyById(insertedRealtyId!!).let {
            assertNotNull(it)
            assertEquals(MEDIA_REF.reference, it)
        }
    }

    @Test
    @Throws(Exception::class)
    fun insertAndGetMediaReferenceByIdList() = runBlocking {
        agentDao.insertIgnore(AGENT)
        realtyTypeDao.insertIgnore(REALTY_TYPE, REALTY_TYPE2)
        realtyDao.insertIgnore(REALTY, REALTY2).firstOrNull()

        val insertedMediaRef = mediaReferenceDao.insertIgnore(MEDIA_REF, MEDIA_REF2, MEDIA_REF3)
        assertNotNull(insertedMediaRef)
        assertEquals(true, insertedMediaRef.isNotEmpty())
        assertEquals(listOf(MEDIA_REF, MEDIA_REF2, MEDIA_REF3), mediaReferenceDao.getMediaByIdList(insertedMediaRef))
    }

    @Test
    @Throws(Exception::class)
    fun insertAndGetMediaReferenceNumberFromRealty() = runBlocking {
        agentDao.insertIgnore(AGENT)
        realtyTypeDao.insertIgnore(REALTY_TYPE, REALTY_TYPE2)
        val firstRealtyInsertedId = realtyDao.insertIgnore(REALTY, REALTY2).firstOrNull()
        mediaReferenceDao.insertIgnore(MEDIA_REF, MEDIA_REF2, MEDIA_REF3)

        assertNotNull(firstRealtyInsertedId)
        mediaReferenceDao.getMediaReferenceNumberFromRealtyId(firstRealtyInsertedId!!).let {
            assertNotEquals(0, it)
            assertEquals(2, it)
        }
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

    @Test
    @Throws(Exception::class)
    fun insertAndGetAgents() = runBlocking {
        agentDao.insertIgnore(AGENT, AGENT2)
        agentDao.getAllAgents().let {
            assertEquals(true, it.isNotEmpty())
            assertEquals(listOf(AGENT, AGENT2), it)
        }
    }

    // =================================
    //  RawQuery DAO Test
    // =================================

    @Test
    @Throws(Exception::class)
    fun testRawQuery() = runBlocking {
        agentDao.insertIgnore(AGENT)
        realtyTypeDao.insertIgnore(REALTY_TYPE, REALTY_TYPE2)
        realtyDao.insertIgnore(REALTY, REALTY2)

        rawQueryDao.getRealtyFromRaw(SimpleSQLiteQuery("SELECT * FROM Realty")).let {
            assertNotEquals(true, it.isEmpty())
            assertEquals(listOf(REALTY, REALTY2), it)
        }
    }

    @Test
    @Throws(Exception::class)
    fun assertRawQueryReturnOnlyOneItemWithWhereFilter() = runBlocking {
        agentDao.insertIgnore(AGENT)
        realtyTypeDao.insertIgnore(REALTY_TYPE, REALTY_TYPE2)
        realtyDao.insertIgnore(REALTY, REALTY2)

        rawQueryDao.getRealtyFromRaw(SimpleSQLiteQuery("SELECT * FROM REALTY WHERE Realty.id == $AUTO_GENERATED_ID")).let {
            assertNotEquals(true, it.isEmpty())
            assertEquals(listOf(REALTY), it)
        }
    }

    @Test
    @Throws(Exception::class)
    fun assertRawQueryReturnEmptyList() = runBlocking {
        agentDao.insertIgnore(AGENT)
        realtyTypeDao.insertIgnore(REALTY_TYPE, REALTY_TYPE2)
        realtyDao.insertIgnore(REALTY, REALTY2)

        val results = rawQueryDao.getRealtyFromRaw(SimpleSQLiteQuery("SELECT * FROM REALTY WHERE Realty.id == $AUTO_GENERATED_ID + 2"))
        assertEquals(true, results.isEmpty())
    }

    // =================================
    //  Inheritance BaseDAO Test
    // =================================

    @Test
    @Throws(Exception::class)
    fun insertIgnoreTest() = runBlocking {
        agentDao.insertIgnore(AGENT)
        agentDao.insertIgnore(AGENT)

        assertEquals(listOf(AGENT), agentDao.getAllAgents())
    }

    @Test
    @Throws(Exception::class)
    fun insertReplaceTest() = runBlocking {
        realtyTypeDao.insertReplace(REALTY_TYPE)
        realtyTypeDao.insertReplace(REALTY_TYPE2)
        val realtyType3 = REALTY_TYPE2.also { it.name = "Imotep" }
        realtyTypeDao.insertReplace(realtyType3)

        realtyTypeDao.getAllRealtyTypes().let {
            assertEquals(true, it.isNotEmpty())
            assertEquals(listOf(REALTY_TYPE, realtyType3), it)
        }
    }

    @Test
    @Throws(Exception::class)
    fun insertIgnoreListTest() = runBlocking {
        realtyTypeDao.insertIgnore(REALTY_TYPE, REALTY_TYPE2, REALTY_TYPE)
        realtyTypeDao.getAllRealtyTypes().let {
            assertEquals(true, it.isNotEmpty())
            assertEquals(listOf(REALTY_TYPE, REALTY_TYPE2), it)
        }
    }

    @Test
    @Throws(Exception::class)
    fun insertReplaceListTest() = runBlocking {
        val realtyType3 = REALTY_TYPE2.also { it.name = "Imotep" }
        realtyTypeDao.insertReplace(REALTY_TYPE, REALTY_TYPE, realtyType3)
        realtyTypeDao.getAllRealtyTypes().let {
            assertEquals(true, it.isNotEmpty())
            assertEquals(listOf(REALTY_TYPE, realtyType3), it)
        }
    }

    @Test
    @Throws(Exception::class)
    fun updateTest() = runBlocking {
        poiDao.insertIgnore(POI)
        val poiInserted = poiDao.getAllPoi().firstOrNull().also {
            assertNotNull(it)
            it!!.name = "Imotep"
            poiDao.update(it)
        }

        poiDao.getAllPoi().let {
            assertEquals(true, it.isNotEmpty())
            assertEquals(listOf(poiInserted), it)
        }
    }

    // =================================
    //  Migration Test
    // =================================

    @Test
    @Throws(Exception::class)
    fun testMigrationFromOneToTwo() = runBlocking {

    }
}