package com.skichrome.realestatemanager.model.retrofit

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Northeast()
{
    @SerializedName("lat")
    @Expose
    var lat: Double? = null
    @SerializedName("lng")
    @Expose
    var lng: Double? = null

    constructor(lat: Double? = null, lng: Double? = null) : this()
    {
        this.lat = lat
        this.lng = lng
    }
}
