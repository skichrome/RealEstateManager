package com.skichrome.realestatemanager

import android.content.ContentResolver
import android.content.ContentUris
import android.util.Log
import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.skichrome.realestatemanager.model.database.RealEstateDatabase
import com.skichrome.realestatemanager.provider.RealtyContentProvider.Companion.REALTY_URI
import kotlinx.coroutines.runBlocking
import org.hamcrest.Matchers.notNullValue
import org.junit.Assert.*
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ContentProviderTests
{
    // =================================
    //              Fields
    // =================================

    private lateinit var contentResolver: ContentResolver

    companion object
    {
        const val DB_NAME = "RealEstateDatabaseTest"
        const val REALTY_ID = 1L

        @BeforeClass
        @JvmStatic
        fun clean()
        {
            InstrumentationRegistry.getInstrumentation().context.databaseList().forEach {
                Log.e("Tests", "Deleting database ! $it")
                InstrumentationRegistry.getInstrumentation().context.deleteDatabase(it)
            }
        }
    }

    // =================================
    //              Methods
    // =================================

    @Before
    fun initDb()
    {
        Room.databaseBuilder(InstrumentationRegistry.getInstrumentation().context, RealEstateDatabase::class.java, DB_NAME)
            .allowMainThreadQueries()
            .build()
        contentResolver = InstrumentationRegistry.getInstrumentation().context.contentResolver
    }

    @Test
    @Throws(Exception::class)
    fun getRealtyFromContentProvider() = runBlocking {
        val cursor = contentResolver.query(ContentUris.withAppendedId(REALTY_URI, REALTY_ID), null, null, null, null)

        assertThat(cursor, notNullValue())
        assertNotEquals("You must test this app with a non existing production database ! Please clear app data in settings !", 1, cursor!!.count)
        assertEquals(0, cursor.count)
    }
}