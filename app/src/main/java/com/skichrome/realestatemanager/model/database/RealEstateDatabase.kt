package com.skichrome.realestatemanager.model.database

import android.content.ContentValues
import android.content.Context
import android.util.Log
import androidx.room.*
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [Realty::class, Poi::class, RealtyType::class, MediaReference::class],
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
                "RealEstateManagerDatabase.db"
            )
                .addCallback(object : RoomDatabase.Callback()
                {
                    override fun onCreate(db: SupportSQLiteDatabase)
                    {
                        super.onCreate(db)
                        Log.e("DATABASE", "Database created")

                        val contentValue = ContentValues()
                        contentValue.put("price", 120_000f)
                        contentValue.put("address", "12 avenue du pont")
                        contentValue.put("postCode", 95000)
                        contentValue.put("city", "J'ai pas d'inspiration")
                        contentValue.put("agent", "Bob")
                        contentValue.put("dateAdded", System.currentTimeMillis())
                        contentValue.put("fullDescription", "A big description")
                        contentValue.put("roomNumber", 4)
                        contentValue.put("status", false)
                        contentValue.put("surface", 45.57f)

                        db.insert("Realty", OnConflictStrategy.REPLACE, contentValue)
                    }

                    override fun onOpen(db: SupportSQLiteDatabase)
                    {
                        super.onOpen(db)
                        Log.e("DATABASE", "Database Opened")
                    }
                })
                .build()
    }
}