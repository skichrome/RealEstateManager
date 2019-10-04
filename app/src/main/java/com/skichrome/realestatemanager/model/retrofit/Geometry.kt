package com.skichrome.realestatemanager.model.retrofit

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Geometry()
{
    @SerializedName("location")
    @Expose
    var location: Location? = null
    @SerializedName("location_type")
    @Expose
    var locationType: String? = null
    @SerializedName("viewport")
    @Expose
    var viewport: Viewport? = null

    constructor(location: Location? = null, locationType: String? = null, viewport: Viewport? = null) : this()
    {
        this.location = location
        this.locationType = locationType
        this.viewport = viewport
    }
}