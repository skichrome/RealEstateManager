package com.skichrome.realestatemanager.model.database

import android.content.ContentValues
import android.content.Context
import androidx.room.*
import androidx.sqlite.db.SupportSQLiteDatabase
import com.skichrome.realestatemanager.R

@Database(
    entities = [Realty::class, Poi::class, RealtyType::class, MediaReference::class, PoiRealty::class],
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
                        insertPrePopulatedCategories(context, db)
                    }
                })
                .build()

        private fun insertPrePopulatedCategories(context: Context, db: SupportSQLiteDatabase) =
            context.resources.getStringArray(R.array.realty_categories).forEachIndexed { index, resource ->
                val contentValue = ContentValues()
                contentValue.put("realtyTypeId", index.toLong())
                contentValue.put("name", resource)
                db.insert("RealtyType", OnConflictStrategy.IGNORE, contentValue)
            }
    }
}