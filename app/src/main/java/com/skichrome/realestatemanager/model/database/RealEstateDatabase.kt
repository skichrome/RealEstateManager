package com.skichrome.realestatemanager.model.database

import android.content.ContentValues
import android.content.Context
import androidx.preference.PreferenceManager
import androidx.room.*
import androidx.sqlite.db.SupportSQLiteDatabase
import com.skichrome.realestatemanager.R
import com.skichrome.realestatemanager.utils.DATABASE_NAME

@Database(
    entities = [Realty::class, Poi::class, RealtyType::class, MediaReference::class, PoiRealty::class, Agent::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class RealEstateDatabase : RoomDatabase()
{
    abstract fun realtyDao(): RealtyDao
    abstract fun poiDao(): PoiDao
    abstract fun realtyTypeDao(): RealtyTypeDao
    abstract fun mediaReferenceDao(): MediaReferenceDao
    abstract fun agentDao(): AgentDao

    companion object
    {
        @Volatile
        private var INSTANCE: RealEstateDatabase? = null

        fun getInstance(context: Context): RealEstateDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: buildDatabase(context).also {
                    INSTANCE = it
                }
            }

        private fun buildDatabase(context: Context) =
            Room.databaseBuilder(
                context.applicationContext,
                RealEstateDatabase::class.java,
                DATABASE_NAME
            )
                .addCallback(object : RoomDatabase.Callback()
                {
                    override fun onCreate(db: SupportSQLiteDatabase)
                    {
                        super.onCreate(db)
                        insertDefaultAgentName(context, db)
                    }
                })
                .build()

        private fun insertDefaultAgentName(context: Context, db: SupportSQLiteDatabase)
        {
            val sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context)
            val defaultAgentName = sharedPrefs.getString("agentUsername", context.getString(R.string.settings_fragment_username_default_value))

            val contentValues = ContentValues()
            contentValues.put("agentId", 1L)
            contentValues.put("name", defaultAgentName)
            db.insert("Agent", OnConflictStrategy.REPLACE, contentValues)
        }
    }
}