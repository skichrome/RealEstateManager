package com.skichrome.realestatemanager.model.database.migrations

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

object MigrationOneTwo : Migration(1, 2)
{
    override fun migrate(database: SupportSQLiteDatabase)
    {
        database.execSQL("ALTER TABLE Realty ADD COLUMN currency INTEGER NOT NULL DEFAULT 0")
    }
}