package com.skichrome.realestatemanager.utils

import android.os.Environment
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

object StorageUtils
{
    @Throws(IOException::class)
    fun createOrGetImageFile(destination: File, fileName: String): File
    {
        val timeStamp = SimpleDateFormat("HH:mm:ss_dd-MM-yyyy", Locale.getDefault()).format(Date())
        return File(destination, "[$timeStamp]$fileName.jpg")
    }

    // ==================================
    // External Storage
    // ==================================

    fun isExternalStorageWritable(): Boolean
    {
        val state = Environment.getExternalStorageState()
        return Environment.MEDIA_MOUNTED == state
    }

    fun isExternalStorageReadable(): Boolean
    {
        val state = Environment.getExternalStorageState()
        return Environment.MEDIA_MOUNTED == state || Environment.MEDIA_MOUNTED_READ_ONLY == state
    }
}