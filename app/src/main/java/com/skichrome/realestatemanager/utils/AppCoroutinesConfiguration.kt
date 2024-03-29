package com.skichrome.realestatemanager.utils

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

object AppCoroutinesConfiguration
{
    var uiDispatchers: CoroutineDispatcher = Dispatchers.Main
    var backgroundDispatchers: CoroutineDispatcher = Dispatchers.Default
    var ioDispatchers: CoroutineDispatcher = Dispatchers.IO
}