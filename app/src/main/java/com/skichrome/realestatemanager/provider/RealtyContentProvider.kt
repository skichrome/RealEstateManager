package com.skichrome.realestatemanager.provider

import android.content.ContentProvider
import android.content.ContentUris
import android.content.ContentValues
import android.database.Cursor
import android.net.Uri
import com.skichrome.realestatemanager.model.database.RealEstateDatabase
import com.skichrome.realestatemanager.model.database.Realty

class RealtyContentProvider : ContentProvider()
{
    companion object
    {
        const val AUTHORITY = "com.skichrome.realestatemanager.provider"
        val TABLE_NAME: String? = Realty::class.java.simpleName
        val REALTY_URI: Uri = Uri.parse("content://$AUTHORITY/")

        const val READ_ONLY_MODE = "This database is in read only mode !"
    }

    override fun onCreate(): Boolean = true

    override fun getType(uri: Uri): String? = "vnd.android.cursor.item/$AUTHORITY.$TABLE_NAME"

    override fun query(uri: Uri, projection: Array<out String>?, selection: String?, selArgs: Array<out String>?, sortOrder: String?): Cursor? =
        context?.let {
            val realtyId = ContentUris.parseId(uri)
            val cursor = RealEstateDatabase.getInstance(it).realtyDao().getRealtyByIdWithCursor(realtyId)
            cursor.setNotificationUri(it.contentResolver, uri)
            return cursor
        } ?: throw IllegalArgumentException("Failed to query row from uri [$uri]")

    override fun insert(uri: Uri, cv: ContentValues?): Uri? = throw IllegalAccessException(READ_ONLY_MODE)
    override fun update(uri: Uri, cv: ContentValues?, sel: String?, selArgs: Array<out String>?): Int = throw IllegalAccessException(READ_ONLY_MODE)
    override fun delete(uri: Uri, sel: String?, selArgs: Array<out String>?): Int = throw IllegalAccessException(READ_ONLY_MODE)
}