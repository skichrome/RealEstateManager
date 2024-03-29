package com.skichrome.realestatemanager.model.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.skichrome.realestatemanager.model.database.migrations.MigrationOneTwo
import com.skichrome.realestatemanager.utils.DATABASE_NAME

@Database(
    entities = [Realty::class, Poi::class, RealtyType::class, MediaReference::class, PoiRealty::class, Agent::class],
    version = 2,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class RealEstateDatabase : RoomDatabase()
{
    abstract fun realtyDao(): RealtyDao
    abstract fun poiDao(): PoiDao
    abstract fun realtyTypeDao(): RealtyTypeDao
    abstract fun mediaReferenceDao(): MediaReferenceDao
    abstract fun poiRealtyDao(): PoiRealtyDao
    abstract fun agentDao(): AgentDao
    abstract fun rawQueryDao(): RawQueryDao

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
                .addMigrations(MigrationOneTwo)
                .fallbackToDestructiveMigration()
                .build()
    }
}