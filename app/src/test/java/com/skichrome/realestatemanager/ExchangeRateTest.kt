package com.skichrome.realestatemanager

import com.skichrome.realestatemanager.utils.Utils
import org.junit.Assert.assertEquals
import org.junit.Test

class ExchangeRateTest
{
    @Test
    fun exchangeRateTest()
    {
        val dollars = 100_000
        val euroToDollar = Utils.convertDollarToEuro(dollars)
        val dollarToEuro = Utils.convertEuroToDollar(euroToDollar)

        assertEquals(dollars, dollarToEuro)
    }
}