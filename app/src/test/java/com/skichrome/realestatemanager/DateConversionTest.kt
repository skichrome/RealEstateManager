package com.skichrome.realestatemanager

import com.skichrome.realestatemanager.utils.Utils
import org.junit.Assert.assertEquals
import org.junit.Test
import java.text.SimpleDateFormat
import java.util.*

class DateConversionTest
{
    private val date = Date()
    private val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.FRANCE)

    @Test
    fun convertUSDateToEUDate()
    {
        val convertedDate = sdf.format(date)
        assertEquals(convertedDate, Utils.getTodayDateLocaleEU())
    }
}