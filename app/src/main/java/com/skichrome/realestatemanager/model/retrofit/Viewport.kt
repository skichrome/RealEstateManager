package com.skichrome.realestatemanager.model.retrofit

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Viewport()
{
    @SerializedName("northeast")
    @Expose
    var northeast: Northeast? = null
    @SerializedName("southwest")
    @Expose
    var southwest: Southwest? = null

    constructor(northeast: Northeast? = null, southwest: Southwest? = null) : this()
    {
        this.northeast = northeast
        this.southwest = southwest
    }
}
