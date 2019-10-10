package com.skichrome.realestatemanager

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.skichrome.realestatemanager.androidmanagers.NetManager
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import java.io.BufferedInputStream
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL

@RunWith(AndroidJUnit4::class)
class NetworkStateTest
{
    @Test
    fun isConnectedToInternet()
    {
        var isConnected = true
        val mockContext = InstrumentationRegistry.getInstrumentation().context

        val isAndroidConnected = NetManager(mockContext).isConnectedToInternet
        val url = URL("https://www.google.fr")

        val httpUrlConn = url.openConnection() as HttpURLConnection
        try
        {
            BufferedInputStream(httpUrlConn.inputStream)
        } catch (e: IOException)
        {
            isConnected = false
        } finally
        {
            httpUrlConn.disconnect()
        }

        assertEquals(isConnected, isAndroidConnected)
    }
}